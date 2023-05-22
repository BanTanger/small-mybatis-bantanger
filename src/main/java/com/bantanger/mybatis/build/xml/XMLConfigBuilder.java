package com.bantanger.mybatis.build.xml;

import com.bantanger.mybatis.build.BaseBuilder;
import com.bantanger.mybatis.dataSource.DataSourceFactory;
import com.bantanger.mybatis.io.Resources;
import com.bantanger.mybatis.mapping.BoundSql;
import com.bantanger.mybatis.mapping.Environment;
import com.bantanger.mybatis.mapping.MappedStatement;
import com.bantanger.mybatis.mapping.SqlCommandType;
import com.bantanger.mybatis.session.Configuration;
import com.bantanger.mybatis.transaction.TransactionFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;

import javax.sql.DataSource;
import java.io.Reader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 专门用于解析核心配置文件 mybatis-config-datasource.xml 的解析类
 * @author BanTanger 半糖
 * @Date 2023/3/13 13:28
 */
public class XMLConfigBuilder extends BaseBuilder {

    private Element root;

    public XMLConfigBuilder(Reader reader) {
        // 1. 调用父类初始化 Configuration，每次都采用 new 的方式调用 configuration，不会出现并发问题
        super(new Configuration());
        // 2. dom4j 处理 xml
        SAXReader saxReader = new SAXReader();
        try {
            Document document = saxReader.read(new InputSource(reader));
            root = document.getRootElement();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    /**
     * configuration 标签的格式
     *
     * <configuration>
     *     <environments default="development">
     *         <environment id="development">
     *             <transactionManager type="JDBC"/>
     * <!--            <dataSource type="UNPOOLED">-->
     *             <dataSource type="POOLED">
     *                 <property name="driver" value="com.mysql.jdbc.Driver"/>
     *                 <property name="url" value="jdbc:mysql://127.0.0.1:3306/jdbc?useUnicode=true"/>
     *                 <property name="username" value="root"/>
     *                 <property name="password" value="123456"/>
     *             </dataSource>
     *         </environment>
     *     </environments>
     *
     *     <mappers>
     *         <mapper resource="mapper/User_Mapper.xml"/>
     *     </mappers>
     * </configuration>
     */
    public Configuration parse() {
        try {
            // 环境：解析 <environments> 标签
            environmentsElement(root.element("environments"));
            // 解析映射器: 解析 <mappers> 标签
            mapperElement(root.element("mappers"));
        } catch (Exception e) {
            throw new RuntimeException("Error parsing SQL Mapper");
        }
        return configuration;
    }

    /**
     * <environments default="development">
     *         <environment id="development">
     *             <transactionManager type="JDBC"/>
     * <!--            <dataSource type="UNPOOLED">-->
     *             <dataSource type="POOLED">
     *                 <property name="driver" value="com.mysql.jdbc.Driver"/>
     *                 <property name="url" value="jdbc:mysql://127.0.0.1:3306/jdbc?useUnicode=true"/>
     *                 <property name="username" value="root"/>
     *                 <property name="password" value="123456"/>
     *             </dataSource>
     *         </environment>
     *     </environments>
     */
    private void environmentsElement(Element context) throws Exception {
        // <environments default="development">
        String environment = context.attributeValue("default"); // environment = development
        // <environment id="development"></environment> 标签集合
        List<Element> environmentList = context.elements("environment");
        for (Element e : environmentList) {
            // <environment id="development">
            String id = e.attributeValue("id"); // id = development
            if (environment.equals(id)) {
                // 事务管理器
                TransactionFactory txFactory = (TransactionFactory) typeAliasRegistry
                        .resolveAlias(e.element("transactionManager")
                                .attributeValue("type")).newInstance();
                // 数据源
                Element dataSourceElement = e.element("dataSource");
                DataSourceFactory dataSourceFactory = (DataSourceFactory) typeAliasRegistry
                        .resolveAlias(dataSourceElement.attributeValue("type")).newInstance();

                List<Element> propertyList = dataSourceElement.elements("property");
                Properties props = new Properties();
                for (Element property : propertyList) {
                    props.setProperty(property.attributeValue("name"), property.attributeValue("value"));
                }
                dataSourceFactory.setProperties(props);
                DataSource dataSource = dataSourceFactory.getDataSource();
                // 构建环境
                Environment.Builder environmentBuilder = new Environment.Builder(id)
                        .transactionFactory(txFactory)
                        .dataSource(dataSource);
                configuration.setEnvironment(environmentBuilder.build());
            }
        }
    }

    /**
     * 1. 根据传参获取到 xml 文件里所有的 Element 元素，从中获取 <mapper> 标签
     * 2. 根据资源路径进行映射配置文件的加载解析
     * 3. 封装到 mappedStatement --> configuration 进行统一维护处理
     * @param mappers
     * @throws Exception
     */
    private void mapperElement(Element mappers) throws Exception {
        List<Element> mapperList = mappers.elements("mapper");
        for (Element e : mapperList) {
            // 每个 Element e 就是一个 mapper 标签
            // <mapper resource="mapper/User_Mapper.xml"/>
            String resource = e.attributeValue("resource");
            Reader reader = Resources.getResourceAsReader(resource);
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(new InputSource(reader));
            Element root = document.getRootElement();
            // 命名空间
            String namespace = root.attributeValue("namespace");

            // SELECT
            List<Element> selectNodes = root.elements("select");
            for (Element node : selectNodes) {
                String id = node.attributeValue("id");
                String parameterType = node.attributeValue("parameterType");
                String resultType = node.attributeValue("resultType");
                String sql = node.getTextTrim();

                // 通过正则进行占位符 ? 匹配，在 mybatis 源码中是采用 GenericTokenParser 进行动态替换
                Map<Integer, String> parameterMappings = new HashMap<>();
                Pattern pattern = Pattern.compile("(#\\{(.*?)})");
                Matcher matcher = pattern.matcher(sql);
                for (int i = 1; matcher.find(); i++) {
                    String g1 = matcher.group(1); // 整个 #{}, 包括里面的内容
                    String g2 = matcher.group(2); // #{} 里面具体的内容
                    parameterMappings.put(i, g2); // 保存占位符内容，为了后续传参对象成员变量的比较
                    sql = sql.replace(g1, "?");
                }

                // statementId: namespace.id
                String msId = namespace + "." + id;
                String nodeName = node.getName();
                SqlCommandType sqlCommandType = SqlCommandType.valueOf(nodeName.toUpperCase(Locale.ENGLISH));

                BoundSql boundSql = new BoundSql(sql, parameterMappings, parameterType, resultType);

                // 封装 mappedStatement 对象
                MappedStatement mappedStatement = new MappedStatement.Builder(
                        configuration, msId, sqlCommandType, boundSql).build();

                // 添加解析 SQL，将封装好的 mappedStatement 对象封装到 configuration 的 map 集合中
                configuration.addMappedStatement(mappedStatement);
            }

            // 注册 Mapper 映射器
            configuration.addMapper(Resources.classForName(namespace));
        }
    }
}
