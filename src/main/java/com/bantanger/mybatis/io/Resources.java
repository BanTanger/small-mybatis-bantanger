package com.bantanger.mybatis.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * 通过类加载器获得 resource 的辅助类
 * @author BanTanger 半糖
 * @Date 2023/3/13 13:21
 */
public class Resources {

    public static Reader getResourceAsReader(String resource) throws IOException {
        return new InputStreamReader(getResourceAsStream(resource));
    }

    /**
     * 类加载器将资源文件进行读取并转化成 IO 字节流
     * @param resource
     * @return
     * @throws IOException
     */
    public static InputStream getResourceAsStream(String resource) throws IOException {
        ClassLoader[] classLoaders = getClassLoaders();
        for (ClassLoader classLoader : classLoaders) {
            // TODO 缺乏判空处理，增强健壮性
            InputStream inputStream = classLoader.getResourceAsStream(resource);
            if (null != inputStream) {
                return inputStream;
            }
        }
        throw new IOException("Could not find resource " + resource);
    }

    /**
     * 在 mybatis 源码中，使用 wrapper 织入所有类加载器
     * 包括参数指定类加载器、系统指定默认类加载器、当前线程绑定的类加载器、当前类使用的类加载器
     * 之所以使用 wrapper 去织入这么多类加载器，主要是还是对上层做一层包装，
     * 屏蔽底层细节和一些边界处理（例如对配置文件、转化字节流判空处理等等）
     * @return
     */
    private static ClassLoader[] getClassLoaders() {
        return new ClassLoader[] {
                // 系统指定默认类加载器
                ClassLoader.getSystemClassLoader(),
                // 当前线程绑定的类加载器
                Thread.currentThread().getContextClassLoader()};
    }

    /*
     * Loads a class
     *
     * @param className - the class to fetch
     * @return The loaded class
     * @throws ClassNotFoundException If the class cannot be found (duh!)
     */
    public static Class<?> classForName(String className) throws ClassNotFoundException {
        return Class.forName(className);
    }
}
