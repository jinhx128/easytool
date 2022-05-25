package cc.jinhx.easytool.process;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * json工具，依赖jackson
 *
 * @author jinhx
 * @since 2022-03-21
 */
@Slf4j
public class JsonUtils {

    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.NONE);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        // 解决java8的时间类型问题
        objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * object转换json
     *
     * @param obj obj
     * @return String
     */
    public static String objectConvertToJson(Object obj){
        if(Objects.isNull(obj)){
            return null;
        }

        try {
            return obj instanceof String ? (String) obj : objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.info("process objectConvertToJson fail error={}", e);
            return null;
        }
    }

    /**
     * json转换object
     *
     * @param src src
     * @param clazz clazz
     * @return T
     */
    public static <T> T jsonConvertToObject(String src, Class<T> clazz){
        if(Objects.isNull(src) || src.length() < 1 || Objects.isNull(clazz)){
            return null;
        }

        try {
            return clazz.equals(String.class) ? (T) src : objectMapper.readValue(src, clazz);
        } catch (Exception e) {
            log.info("process jsonConvertToObject fail error=", e);
            return null;
        }
    }

    /**
     * object转换object
     *
     * @param obj obj
     * @param clazz clazz
     * @return T
     */
    public static <T> T objectConvertToObject(Object obj, Class<T> clazz){
        return jsonConvertToObject(objectConvertToJson(obj), clazz);
    }

    /**
     * json转换map
     *
     * @param src src
     * @return Map<String, Object>
     */
    public static Map<String, Object> jsonConvertToMap(String src) {
        if(Objects.isNull(src) || src.length() < 1){
            return Collections.emptyMap();
        }

        try {
            return objectMapper.readValue(src, Map.class);
        } catch (Exception e) {
            log.info("process jsonConvertToMap fail error=", e);
            return Collections.emptyMap();
        }
    }

    /**
     * json转换list
     *
     * @param src src
     * @param clazz clazz
     * @return List<T>
     */
    public static <T> List<T> jsonConvertToList(String src, Class<T> clazz) {
        if(Objects.isNull(src) || src.length() < 1 || Objects.isNull(clazz)){
            return Collections.emptyList();
        }

        try{
            return objectMapper.readValue(src, objectMapper.getTypeFactory().constructParametricType(ArrayList.class, clazz));
        }catch (Exception e) {
            log.info("process jsonConvertToList fail error=", e);
            return Collections.emptyList();
        }
    }

}

