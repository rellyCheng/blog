package com.relly.blog.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.io.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 转换工具类
 *
 * @author Yvan 2018/9/29 15:54
 */
@Slf4j
public class ConvertUtils {
    /**
     * convert entityList to DtoList
     *
     * @param entityList
     * @param tClass
     * @param <F>
     * @param <T>
     * @return
     */
    public static <F, T> List<T> entityListToDtoList(List<F> entityList, Class<T> tClass) {
        if (entityList == null || entityList.isEmpty()) {
            return null;
        }
        List<T> dtoList = new ArrayList<>();
        for (F f : entityList) {
            T t = entityToDto(f, tClass);
            dtoList.add(t);
        }
        return dtoList;
    }

    /**
     * convert entity to Dto
     *
     * @param entity
     * @param dtoClass
     * @param <F>
     * @param <T>
     * @return
     */
    public static <F, T> T entityToDto(F entity, Class<T> dtoClass) {

        if (entity == null || dtoClass == null) {
            return null;
        }
        Object model = null;
        try {
            model = dtoClass.newInstance();
        } catch (InstantiationException e) {
            log.error("entityToDto : 实例化异常", e);
        } catch (IllegalAccessException e) {
            log.error("entityToDto : 访问权限异常", e);
        }
        assert model != null;
        BeanUtils.copyProperties(entity, model);
        return (T) model;

    }

    /**
     * ListDeepCopy
     *
     * @param src sourceList
     * @param <T> dataType
     * @return List
     */
    public static <T> List<T> deepCopy(List<T> src) throws IOException, ClassNotFoundException {

        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(src);

        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream in = new ObjectInputStream(byteIn);
        @SuppressWarnings("unchecked")
        List<T> dest = (List<T>) in.readObject();
        return dest;
    }

    public static Object populate(Object src, Object target) {

        Method[] srcMethods = src.getClass().getMethods();

        Method[] targetMethods = target.getClass().getMethods();

        for (Method m : srcMethods) {

            String srcName = m.getName();

            if (srcName.startsWith("get")) {

                try {

                    Object result = m.invoke(src);

                    for (Method mm : targetMethods) {

                        String targetName = mm.getName();

                        if (targetName.startsWith("set")
                                && targetName.substring(3, targetName.length())

                                .equals(srcName.substring(3, srcName.length()))) {

                            mm.invoke(target, result);

                        }

                    }

                } catch (Exception e) {

                }

            }

        }

        return target;

    }

}
