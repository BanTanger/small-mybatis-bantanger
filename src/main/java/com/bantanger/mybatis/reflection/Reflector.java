package com.bantanger.mybatis.reflection;

import com.bantanger.mybatis.reflection.invoker.GetFieldInvoker;
import com.bantanger.mybatis.reflection.invoker.Invoker;
import com.bantanger.mybatis.reflection.invoker.MethodInvoker;
import com.bantanger.mybatis.reflection.invoker.SetFieldInvoker;
import com.bantanger.mybatis.reflection.property.PropertyNamer;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 反射器，专门用于解耦对象信息，将对象包含的属性、方法以及关联的类都解析出来
 *
 * @author BanTanger 半糖
 * @Date 2023/5/23 12:22
 */
public class Reflector {

    private static boolean classCacheEnabled = true;

    private static final String[] EMPTY_STRING_ARRAY = new String[0];
    /**
     * 线程安全的缓存对象
     */
    private static final Map<Class<?>, Reflector> REFLECTOR_MAP = new ConcurrentHashMap<>();

    private Class<?> type;
    // get 属性列表
    private String[] readablePropertyNames = EMPTY_STRING_ARRAY;
    // set 属性列表
    private String[] writeablePropertyNames = EMPTY_STRING_ARRAY;
    // set 方法列表
    private Map<String, Invoker> setMethods = new HashMap<>();
    // get 方法列表
    private Map<String, Invoker> getMethods = new HashMap<>();
    // set 类型列表
    private Map<String, Class<?>> setTypes = new HashMap<>();
    // get 类型列表
    private Map<String, Class<?>> getTypes = new HashMap<>();
    // 构造函数
    private Constructor<?> defaultConstructor;

    private Map<String, String> caseInsensitivePropertyMap = new HashMap<>();

    public Reflector(Class<?> clazz) {
        this.type = clazz;
        // 加入构造函数
        addDefaultConstructor(clazz);
        // 加入 getter
        addGetMethods(clazz);
        // 加入 setter
        addSetMethods(clazz);
        // 加入字段
        addFields(clazz);
        readablePropertyNames = getMethods.keySet().toArray(new String[0]);
        writeablePropertyNames = setMethods.keySet().toArray(new String[0]);
        for (String propName : readablePropertyNames) {
            caseInsensitivePropertyMap.put(propName.toUpperCase(Locale.ENGLISH), propName);
        }
        for (String propName : writeablePropertyNames) {
            caseInsensitivePropertyMap.put(propName.toUpperCase(Locale.ENGLISH), propName);
        }
    }

    /**
     * 这是一个添加类中所有属性的方法，并且还会循环递归地添加其父类的属性。
     * <br> 1. 通过 getDeclaredFields() 方法获取到当前类中的所有属性，然后遍历每一个属性。
     * <br> 2. 在遍历属性之前，首先会通过 canAccessPrivateMethods() 方法判断当前是否能够访问私有方法，
     * 如果可以访问，就将该属性设置为可访问状态。如果该属性本身就是可访问的，就尝试将该属性添加到 setMethods 或 getMethods 集合中去。
     * <br> 3. 在添加属性之前，首先需要判断该属性是否已经被添加到 setMethods 或 getMethods 集合中。<br>
     * a. 如果该属性没有被添加到 setMethods 集合中，就需要判断该属性是否为 final 和 static 修饰的，
     * 如果不是，则将该属性添加到 setMethods 集合中，否则不进行任何操作。<br>
     * b. 如果该属性没有被添加到 getMethods 集合中，就直接将该属性添加到 getMethods 集合中。(ab两步主要避免重复添加同一个属性)
     * <br> 4. 最后，如果当前类有父类，会通过 getSuperclass() 方法获取到父类，并且递归调用 addFields() 方法，
     * 将父类中的属性也添加到 setMethods 或 getMethods 集合中。
     * @param clazz
     */
    private void addFields(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (canAccessPrivateMethods()) {
                try {
                    field.setAccessible(true);
                } catch (Exception e) {
                    // Ignored. This is only a final precaution, nothing we can do.
                }
            }
            if (field.isAccessible()) {
                if (!setMethods.containsKey(field.getName())) {
                    // issue #379 - removed the check for final because JDK 1.5 allows
                    // modification of final fields through reflection (JSR-133). (JGB)
                    // pr #16 - final static can only be set by the classloader
                    int modifiers = field.getModifiers();
                    if (!(Modifier.isFinal(modifiers) && Modifier.isStatic(modifiers))) {
                        addSetField(field);
                    }
                }
                if (!getMethods.containsKey(field.getName())) {
                    addGetField(field);
                }
            }
        }
        if (clazz.getSuperclass() != null) {
            addFields(clazz.getSuperclass());
        }
    }

    private void addGetField(Field field) {
        if (isValidPropertyName(field.getName())) {
            getMethods.put(field.getName(), new GetFieldInvoker(field));
            getTypes.put(field.getName(), field.getType());
        }
    }

    private void addSetField(Field field) {
        if (isValidPropertyName(field.getName())) {
            setMethods.put(field.getName(), new SetFieldInvoker(field));
            setTypes.put(field.getName(), field.getType());
        }
    }

    private void addSetMethods(Class<?> clazz) {
        Map<String, List<Method>> conflictingSetters = new HashMap<>();
        Method[] methods = getClassMethods(clazz);
        for (Method method : methods) {
            String name = method.getName();
            if (name.startsWith("set") && name.length() > 3) {
                if (method.getParameterTypes().length == 1) {
                    name = PropertyNamer.methodToProperty(name);
                    addMethodConflict(conflictingSetters, name, method);
                }
            }
        }
        resolveSetterConflicts(conflictingSetters);
    }

    private void resolveSetterConflicts(Map<String, List<Method>> conflictingSetters) {
        for (String propName : conflictingSetters.keySet()) {
            List<Method> setters = conflictingSetters.get(propName);
            Method firstMethod = setters.get(0);
            if (setters.size() == 1) {
                addSetMethod(propName, firstMethod);
            } else {
                Class<?> expectedType = getTypes.get(propName);
                if (expectedType == null) {
                    throw new RuntimeException("Illegal overloaded setter method with ambiguous type for property "
                            + propName + " in class " + firstMethod.getDeclaringClass() + ".  This breaks the JavaBeans " +
                            "specification and can cause unpredicatble results.");
                } else {
                    Iterator<Method> methods = setters.iterator();
                    Method setter = null;
                    while (methods.hasNext()) {
                        Method method = methods.next();
                        if (method.getParameterTypes().length == 1
                                && expectedType.equals(method.getParameterTypes()[0])) {
                            setter = method;
                            break;
                        }
                    }
                    if (setter == null) {
                        throw new RuntimeException("Illegal overloaded setter method with ambiguous type for property "
                                + propName + " in class " + firstMethod.getDeclaringClass() + ".  This breaks the JavaBeans " +
                                "specification and can cause unpredicatble results.");
                    }
                    addSetMethod(propName, setter);
                }
            }
        }
    }

    private void addSetMethod(String name, Method method) {
        if (isValidPropertyName(name)) {
            setMethods.put(name, new MethodInvoker(method));
            setTypes.put(name, method.getParameterTypes()[0]);
        }
    }

    /**
     * 这段代码实现的是用于获取指定类中所有 getter 方法的逻辑。它接收一个 Class 对象作为参数
     * <br> 1. 调用 getClassMethods 方法获取该类中所有的方法，然后对这些方法进行遍历，
     * <br> 2. 如果方法以 "get" 或 "is" 开头，且方法参数数量是 0，则将其方法名转换为属性名(除去 get、set、is 前缀)，
     * 并使用 addMethodConflict 方法将该 getter 方法添加到 conflictingGetters Map 对象中。
     * <br> 3. conflictingGetters 对象是用于存储冲突的方法的 Map，它的 Key 是方法名，Value 是关联的所有 Method 对象。
     * <br> 4. 如果存在多个方法名相同的 getter 方法，可能由于不同的方法参数，会产生命名冲突。
     * 这时，通过使用 addMethodConflict 方法，实现将同名方法添加到列表中去，方便后续处理。
     * <br> 5. 对 conflictingGetters 进行去重处理，确保每个 getter 方法的方法签名只有一个对应的 Method 对象，
     * 具体处理逻辑会在 resolveGetterConflicts 方法中实现。
     * @param clazz
     */
    private void addGetMethods(Class<?> clazz) {
        // 收集冲突方法，这样就可以快速地查找到方法签名对应的 Method 对象并去重。
        Map<String, List<Method>> conflictingGetters = new HashMap<>();
        Method[] methods = getClassMethods(clazz);
        for (Method method : methods) {
            String name = method.getName();
            if (name.startsWith("get") && name.length() > 3) {
                if (method.getParameterTypes().length == 0) {
                    name = PropertyNamer.methodToProperty(name);
                    addMethodConflict(conflictingGetters, name, method);
                }
            } else if (name.startsWith("is") && name.length() > 2) {
                if (method.getParameterTypes().length == 0) {
                    name = PropertyNamer.methodToProperty(name);
                    addMethodConflict(conflictingGetters, name, method);
                }
            }
        }
        resolveGetterConflicts(conflictingGetters);
    }

    /**
     * 这段代码实现了对具有相同属性名的 getter 方法的去重和冲突处理，其逻辑如下：
     * <br> 1. 遍历冲突列表中的每一个属性名。
     * <br> 2. 对于每个属性名，获取其对应的 getter 方法列表。
     * <br> 3. 如果该列表只有一个元素，则直接将该方法添加到属性到 getter 方法的映射列表中。
     * <br> 4. 如果该列表有多个元素，则需要对 getter 方法进行冲突处理，确定该属性应该选择哪个 getter 方法作为其对应的方法。
     * <br> 5. 从 getter 方法列表中随机选择一个作为初始 getter 方法。
     * <br> 6. 遍历 getter 方法列表中剩余的所有 getter 方法。
     * <br> 7. 对于每个 getter 方法，比较其返回值类型和初始 getter 方法的返回值类型，如果二者不相同，则需要进行冲突解决。
     * <br> 8. 如果二者的类型不同但存在继承关系，则选择子类作为 getter 类型。
     * <br> 9. 如果 getter 的返回类型不能赋值给初始 getter 的返回类型，则选择当前 getter 作为属性的 getter 方法。
     * <br> 10. 如果存在多个方法能够满足要求，则抛出异常。
     * <br>
     * <br> 最终会将属性名与对应的 getter 方法添加到映射列表中。
     * @param conflictingGetters
     */
    private void resolveGetterConflicts(Map<String, List<Method>> conflictingGetters) {
        for (String propName : conflictingGetters.keySet()) {
            List<Method> getters = conflictingGetters.get(propName);
            Iterator<Method> iterator = getters.iterator();
            Method firstMethod = iterator.next();
            if (getters.size() == 1) {
                addGetMethods(propName, firstMethod);
            } else {
                Method getter = firstMethod;
                Class<?> getterType = firstMethod.getReturnType();
                while (iterator.hasNext()) {
                    Method method = iterator.next();
                    Class<?> methodType = method.getReturnType();
                    if (methodType.equals(getterType)) {
                        throw new RuntimeException("Illegal overloaded getter method with ambiguous type for property "
                                + propName + " in class " + firstMethod.getDeclaringClass()
                                + ".  This breaks the JavaBeans " + "specification and can cause unpredicatble results.");
                    } else if (methodType.isAssignableFrom(getterType)) {
                        // OK getter type is descendant
                    } else if (getterType.isAssignableFrom(methodType)) {
                        getter = method;
                        getterType = methodType;
                    } else {
                        throw new RuntimeException("Illegal overloaded getter method with ambiguous type for property "
                                + propName + " in class " + firstMethod.getDeclaringClass()
                                + ".  This breaks the JavaBeans " + "specification and can cause unpredicatble results.");
                    }
                }
                addGetMethods(propName, getter);
            }
        }
    }

    private void addGetMethods(String name, Method method) {
        if (isValidPropertyName(name)) {
            getMethods.put(name, new MethodInvoker(method));
            getTypes.put(name, method.getReturnType());
        }
    }

    private boolean isValidPropertyName(String name) {
        return !(name.startsWith("$") || "serialVersionUID".equals(name) || "class".equals(name));
    }

    /**
     * <br> 这段代码实现了将 Method 数组中的非桥接方法添加到 Map 中的逻辑，同时保证每个方法签名只有一个对应的 Method 对象。
     * <br> 具体来说，它接收了三个参数，包括一个 Map 对象 conflictingMethods，一个方法名 name，以及一个 Method 对象 method。
     * <br> 这个 Map 存储了冲突的方法签名和关联的 Method 对象列表。
     * <br> 如果方法名已经存在于该 Map 中，则将该 Method 对象添加到相应的 List 中；否则，创建一个新的 List，将方法名和该 List 放入 Map 中。
     * <br>
     * <br> 总体来说，这段代码的作用是维护一个 Map，用于保存方法名和关联的 Method 对象，这样就可以快速地查找到方法签名对应的 Method 对象并去重。
     *
     * @param conflictingMethods
     * @param name
     * @param method
     */
    private void addMethodConflict(Map<String, List<Method>> conflictingMethods, String name, Method method) {
        List<Method> list = conflictingMethods.computeIfAbsent(name, k -> new ArrayList<>());
        list.add(method);
    }

    private Method[] getClassMethods(Class<?> clazz) {
        // 定制唯一方法，key为方法签名，value为方法
        Map<String, Method> uniqueMethods = new HashMap<>();
        Class<?> currentClass = clazz;
        while (currentClass != null) {
            addUniqueMethods(uniqueMethods, currentClass.getDeclaredMethods());

            // 检查该类继承的接口方法，因为该类可能是抽象类
            Class<?>[] interfaces = currentClass.getInterfaces();
            for (Class<?> anInterface : interfaces) {
                addUniqueMethods(uniqueMethods, anInterface.getMethods());
            }
            currentClass = currentClass.getSuperclass();
        }
        Collection<Method> methods = uniqueMethods.values();
        return methods.toArray(new Method[methods.size()]);
    }

    /**
     * 将 Method 数组中的非桥接方法添加到传入的 Map 中，同时去重，确保每个方法签名只有一个对应的 Method 对象。
     * 具体实现是通过遍历传入的 Method 数组，判断每个方法是否为桥接方法，如果不是，则获取该方法的签名字符串，
     * 并判断 Map 中是否已经存在该签名，如果不存在则将该方法添加到 Map 中。
     * 同时，如果当前对象可以访问私有方法，则将该 Method 对象设置为可访问状态，最终返回去重后的 Map。
     * <p>
     * 当我们使用泛型类或泛型方法时，由于类型擦除的存在，我们在编译期无法确定泛型的具体类型，编译器会自动生成桥接方法，以保证代码的兼容性和正确性。
     *
     * @param uniqueMethods
     * @param methods
     */
    private void addUniqueMethods(Map<String, Method> uniqueMethods, Method[] methods) {
        for (Method currentMethod : methods) {
            // 获取直接方法，而不是桥接方法
            if (!currentMethod.isBridge()) {
                // 获取签名
                String signature = getSignature(currentMethod);
                if (!uniqueMethods.containsKey(signature)) {
                    if (canAccessPrivateMethods()) {
                        try {
                            currentMethod.setAccessible(true);
                        } catch (Exception e) {
                        }
                    }

                    uniqueMethods.put(signature, currentMethod);
                }
            }
        }
    }

    /**
     * <br> 新建了一个StringBuilder对象sb，用于拼接签名信息。
     * <br> 然后获取method的返回值类型，如果非空，就在sb中添加返回值类型的名称和“#”符号。
     * <br> 接下来，将method的名称添加到sb中。紧接着，获取method的参数类型，并通过for循环遍历每个参数类型。
     * <br> 如果当前参数是第一个，就在sb末尾添加“:”符号，否则添加“,”符号。最后将参数类型名称也添加到sb中。
     * <br> 最后，返回所有拼接完毕的字符串。这个方法会根据Method对象获取到的信息，生成方法的签名字符串
     * <br>
     * <br> 例如“java.lang.String#toUpperCase:java.util.Locale”。
     *
     * @param method
     * @return
     */
    private String getSignature(Method method) {
        StringBuilder sb = new StringBuilder();
        Class<?> returnType = method.getReturnType();
        if (returnType != null) {
            sb.append(returnType.getName()).append('#');
        }
        sb.append(method.getName());
        Class<?>[] parameterTypes = method.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            if (i == 0) {
                sb.append(':');
            } else {
                sb.append(',');
            }
            sb.append(parameterTypes[i].getName());
        }
        return sb.toString();
    }

    /**
     * 向一个类中添加默认构造函数。它的实现过程如下：
     * <br> 1. 首先通过 clazz.getConstructors() 获取该类中所有公共构造函数（public constructors）。
     * <br> 2. 遍历构造函数数组，找到一个没有任何参数的构造函数作为目标。
     * <br> 3. 使用 canAccessPrivateMethods() 方法来判断当前环境是否可以访问该类的私有方法，如果可以，则将构造函数的可访问性设置为 true。
     * <br> 4. 判断构造函数是否可访问，如果可以，则将该构造函数设置为默认构造函数，就可以在后续使用该构造函数来创建新的实例了。
     * <br>
     * <br> 需要注意的是，在第三步设置访问标志时，如果环境不支持访问私有方法，则会抛出异常。为了避免程序因为访问权限问题而中断，所以代码中使用了try-catch语句来捕获并忽略该异常。
     *
     * @param clazz
     */
    private void addDefaultConstructor(Class<?> clazz) {
        Constructor<?>[] constructors = clazz.getConstructors();
        for (Constructor<?> constructor : constructors) {
            // 寻找无参构造作为默认构造器
            if (constructor.getParameterTypes().length == 0) {
                if (canAccessPrivateMethods()) {
                    try {
                        constructor.setAccessible(true);
                    } catch (Exception ignore) {
                    }
                }
                if (constructor.isAccessible()) {
                    this.defaultConstructor = constructor;
                }
            }
        }
    }

    /**
     * 检查当前的安全策略是否允许程序访问或者修改对象的私有属性或者方法
     *
     * @return 如果返回true，说明当前安全策略允许访问私有方法；如果返回false，则说明当前安全策略不允许访问私有方法。
     */
    private static boolean canAccessPrivateMethods() {
        try {
            SecurityManager securityManager = System.getSecurityManager();
            if (null != securityManager) {
                securityManager.checkPermission(new ReflectPermission("suppressAccessChecks"));
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }


    public Class<?> getType() {
        return type;
    }

    public Constructor<?> getDefaultConstructor() {
        if (defaultConstructor != null) {
            return defaultConstructor;
        } else {
            throw new RuntimeException("There is no default constructor for " + type);
        }
    }

    public boolean hasDefaultConstructor() {
        return defaultConstructor != null;
    }

    public Class<?> getSetterType(String propertyName) {
        Class<?> clazz = setTypes.get(propertyName);
        if (clazz == null) {
            throw new RuntimeException("There is no setter for property named '" + propertyName + "' in '" + type + "'");
        }
        return clazz;
    }

    public Invoker getGetInvoker(String propertyName) {
        Invoker method = getMethods.get(propertyName);
        if (method == null) {
            throw new RuntimeException("There is no getter for property named '" + propertyName + "' in '" + type + "'");
        }
        return method;
    }

    public Invoker getSetInvoker(String propertyName) {
        Invoker method = setMethods.get(propertyName);
        if (method == null) {
            throw new RuntimeException("There is no setter for property named '" + propertyName + "' in '" + type + "'");
        }
        return method;
    }

    /**
     * Gets the type for a property getter
     *
     * @param propertyName - the name of the property
     * @return The Class of the propery getter
     */
    public Class<?> getGetterType(String propertyName) {
        Class<?> clazz = getTypes.get(propertyName);
        if (clazz == null) {
            throw new RuntimeException("There is no getter for property named '" + propertyName + "' in '" + type + "'");
        }
        return clazz;
    }

    /**
     * Gets an array of the readable properties for an object
     *
     * @return The array
     */
    public String[] getGetablePropertyNames() {
        return readablePropertyNames;
    }

    /**
     * Gets an array of the writeable properties for an object
     *
     * @return The array
     */
    public String[] getSetablePropertyNames() {
        return writeablePropertyNames;
    }

    /**
     * Check to see if a class has a writeable property by name
     *
     * @param propertyName - the name of the property to check
     * @return True if the object has a writeable property by the name
     */
    public boolean hasSetter(String propertyName) {
        return setMethods.keySet().contains(propertyName);
    }

    /**
     * Check to see if a class has a readable property by name
     *
     * @param propertyName - the name of the property to check
     * @return True if the object has a readable property by the name
     */
    public boolean hasGetter(String propertyName) {
        return getMethods.keySet().contains(propertyName);
    }

    public String findPropertyName(String name) {
        return caseInsensitivePropertyMap.get(name.toUpperCase(Locale.ENGLISH));
    }

    /**
     * Gets an instance of ClassInfo for the specified class.
     * 得到某个类的反射器，是静态方法，而且要缓存，又要多线程，所以REFLECTOR_MAP是一个ConcurrentHashMap
     *
     * @param clazz The class for which to lookup the method cache.
     * @return The method cache for the class
     */
    public static Reflector forClass(Class<?> clazz) {
        if (classCacheEnabled) {
            // synchronized (clazz) removed see issue #461
            // 对于每个类来说，我们假设它是不会变的，这样可以考虑将这个类的信息(构造函数，getter,setter,字段)加入缓存，以提高速度
            Reflector cached = REFLECTOR_MAP.get(clazz);
            if (cached == null) {
                cached = new Reflector(clazz);
                REFLECTOR_MAP.put(clazz, cached);
            }
            return cached;
        } else {
            return new Reflector(clazz);
        }
    }

    public static void setClassCacheEnabled(boolean classCacheEnabled) {
        Reflector.classCacheEnabled = classCacheEnabled;
    }

    public static boolean isClassCacheEnabled() {
        return classCacheEnabled;
    }

}
