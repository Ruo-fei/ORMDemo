package com.example.ORMDemo.IOC;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

public class Container {
    private final Map<String, Object> objectFactory = new HashMap<>();

    public static void start() throws Exception{
        Container container = new Container();
        List<Class<?>> classes = container.scan();
        container.register(classes);
        container.injectObjects(classes);
    }

    private List<Class<?>> scan() {
        return Arrays.asList(A.class, B.class, Starter.class);
    }

    private boolean register(List<Class<?>> classes) throws Exception{
        for (Class<?> impClass : classes) {
            Annotation[] annotations = impClass.getAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation.annotationType() == Component.class) {
                    objectFactory.put(impClass.getSimpleName(), impClass.getDeclaredConstructor(null).newInstance());
                }
            }
        }
        return true;
    }

    private boolean injectObjects(List<Class<?>> classes) throws Exception{
        for (Class<?> impClass : classes) {
            Field[] fields = impClass.getDeclaredFields();
            Object curInstance = objectFactory.get(impClass.getSimpleName());
            for (Field f : fields) {
                Annotation[] annotations = f.getAnnotations();
                for (Annotation a : annotations) {
                    if (a.annotationType() == Autowired.class) {
                        Class<?> type = f.getType();
                        Object injectInstance = objectFactory.get(type.getSimpleName());
                        f.setAccessible(true);
                        f.set(curInstance, injectInstance);
                    }
                }
            }
        }
        return true;
    }
}

@Component
class A {
    @Override
    public String toString() {
        return "This is A instance";
    }
}

@Component
class B {
    @Override
    public String toString() {
        return "This is B instance";
    }
}

@Component
class Starter {
    @Autowired
    private static A a;
    @Autowired
    private static B b;

    public static void main(String[] args) throws Exception{
        Container.start();
        System.out.println(a);
        System.out.println(b);
    }
}
