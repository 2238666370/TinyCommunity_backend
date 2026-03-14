package com.community.util;
import com.community.exception.BusinessException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

/**
 * Jackson JSON工具类
 * 提供对象与JSON字符串之间的转换功能
 */
@Slf4j
public class JsonUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        // 初始化ObjectMapper配置
        configureObjectMapper(OBJECT_MAPPER);
    }

    /**
     * 配置ObjectMapper
     * @param mapper ObjectMapper实例
     */
    private static void configureObjectMapper(ObjectMapper mapper) {
        // 反序列化时忽略JSON中存在的未知属性
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // 序列化时忽略空值的属性
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        // 日期格式
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        // 注册Java 8时间模块
        mapper.registerModule(new JavaTimeModule());

        // 禁用将日期序列化为时间戳
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    /**
     * 获取ObjectMapper实例（可用于自定义配置）
     * @return ObjectMapper实例
     */
    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER.copy();
    }

    // ==================== 对象转JSON ====================

    /**
     * 将对象转为JSON字符串
     * @param object 要转换的对象
     * @return JSON字符串
     * @throws JsonProcessingException JSON处理异常
     */
    public static String toJson(Object object) throws JsonProcessingException {
        return OBJECT_MAPPER.writeValueAsString(object);
    }

    /**
     * 将对象转为JSON字符串（静默处理异常，返回null）
     * @param object 要转换的对象
     * @return JSON字符串，转换失败返回null
     */
    public static String toJsonSilently(Object object) {
        try {
            return toJson(object);
        } catch (JsonProcessingException e) {
            log.error("toJson error:{}",e.getMessage(),e);
            throw new BusinessException("服务器错误,请联系管理员");
        }
    }

    /**
     * 将对象转为格式化的JSON字符串
     * @param object 要转换的对象
     * @return 格式化后的JSON字符串
     * @throws JsonProcessingException JSON处理异常
     */
    public static String toPrettyJson(Object object) throws JsonProcessingException {
        return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(object);
    }

    // ==================== JSON转对象 ====================

    /**
     * 将JSON字符串转为指定类型的对象
     * @param json JSON字符串
     * @param clazz 目标类型
     * @param <T> 泛型类型
     * @return 转换后的对象
     * @throws JsonProcessingException JSON处理异常
     */
    public static <T> T fromJson(String json, Class<T> clazz) throws JsonProcessingException {
        return OBJECT_MAPPER.readValue(json, clazz);
    }

    /**
     * 将JSON字符串转为指定类型的对象（静默处理异常，返回null）
     * @param json JSON字符串
     * @param clazz 目标类型
     * @param <T> 泛型类型
     * @return 转换后的对象，转换失败返回null
     */
    public static <T> T fromJsonSilently(String json, Class<T> clazz) {
        try {
            return fromJson(json, clazz);
        } catch (JsonProcessingException e) {
            log.error("toJson error:{}",e.getMessage(),e);
            throw new BusinessException("服务器错误,请联系管理员");
        }
    }

    /**
     * 将JSON字符串转为复杂类型（如泛型）
     * @param json JSON字符串
     * @param typeReference TypeReference实例
     * @param <T> 泛型类型
     * @return 转换后的对象
     * @throws JsonProcessingException JSON处理异常
     */
    public static <T> T fromJson(String json, TypeReference<T> typeReference) throws JsonProcessingException {
        return OBJECT_MAPPER.readValue(json, typeReference);
    }

    // ==================== JSON转List ====================

    /**
     * 将JSON字符串转为List
     * @param json JSON字符串
     * @param elementType List中元素的类型
     * @param <T> 泛型类型
     * @return List对象
     * @throws JsonProcessingException JSON处理异常
     */
    public static <T> List<T> toList(String json, Class<T> elementType) throws JsonProcessingException {
        JavaType javaType = OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, elementType);
        return OBJECT_MAPPER.readValue(json, javaType);
    }

    /**
     * 将JSON字符串转为List（静默处理异常，返回null）
     * @param json JSON字符串
     * @param elementType List中元素的类型
     * @param <T> 泛型类型
     * @return List对象，转换失败返回null
     */
    public static <T> List<T> toListSilently(String json, Class<T> elementType) {
        try {
            return toList(json, elementType);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    // ==================== JSON转Map ====================

    /**
     * 将JSON字符串转为Map
     * @param json JSON字符串
     * @param keyType Map的key类型
     * @param valueType Map的value类型
     * @param <K> key泛型类型
     * @param <V> value泛型类型
     * @return Map对象
     * @throws JsonProcessingException JSON处理异常
     */
    public static <K, V> Map<K, V> toMap(String json, Class<K> keyType, Class<V> valueType) throws JsonProcessingException {
        JavaType javaType = OBJECT_MAPPER.getTypeFactory().constructMapType(Map.class, keyType, valueType);
        return OBJECT_MAPPER.readValue(json, javaType);
    }

    /**
     * 将JSON字符串转为String-Object的Map
     * @param json JSON字符串
     * @param valueType Map的value类型
     * @param <V> value泛型类型
     * @return Map对象
     * @throws JsonProcessingException JSON处理异常
     */
    public static <V> Map<String, V> toStringObjectMap(String json, Class<V> valueType) throws JsonProcessingException {
        return toMap(json, String.class, valueType);
    }

    /**
     * 将JSON字符串转为String-Object的Map（静默处理异常，返回null）
     * @param json JSON字符串
     * @param valueType Map的value类型
     * @param <V> value泛型类型
     * @return Map对象，转换失败返回null
     */
    public static <V> Map<String, V> toStringObjectMapSilently(String json, Class<V> valueType) {
        try {
            return toStringObjectMap(json, valueType);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    // ==================== 对象深拷贝 ====================

    /**
     * 通过JSON序列化实现对象的深拷贝
     * @param object 要拷贝的对象
     * @param clazz 目标类型
     * @param <T> 泛型类型
     * @return 深拷贝后的新对象
     * @throws IOException IO异常
     */
    public static <T> T deepCopy(T object, Class<T> clazz) throws IOException {
        String json = OBJECT_MAPPER.writeValueAsString(object);
        return OBJECT_MAPPER.readValue(json, clazz);
    }

    /**
     * 通过JSON序列化实现对象的深拷贝（静默处理异常，返回null）
     * @param object 要拷贝的对象
     * @param clazz 目标类型
     * @param <T> 泛型类型
     * @return 深拷贝后的新对象，转换失败返回null
     */
    public static <T> T deepCopySilently(T object, Class<T> clazz) {
        try {
            return deepCopy(object, clazz);
        } catch (IOException e) {
            return null;
        }
    }
}