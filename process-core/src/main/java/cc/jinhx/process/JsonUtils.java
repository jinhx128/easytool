package cc.jinhx.process;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

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
            log.error("objectConvertToJson fail error=", e);
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
        if(StringUtils.isEmpty(src) || Objects.isNull(clazz)){
            return null;
        }

        try {
            return clazz.equals(String.class) ? (T) src : objectMapper.readValue(src, clazz);
        } catch (Exception e) {
            log.error("jsonConvertToObject fail error=", e);
            return null;
        }
    }

    /**
     * json转换map
     *
     * @param src src
     * @return Map<String, Object>
     */
    public static Map<String, Object> jsonConvertToMap(String src) {
        if(StringUtils.isEmpty(src)){
            return Collections.emptyMap();
        }

        try {
            return objectMapper.readValue(src, Map.class);
        } catch (Exception e) {
            log.error("jsonConvertToMap fail error=", e);
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
        if(StringUtils.isEmpty(src) || Objects.isNull(clazz)){
            return Collections.emptyList();
        }

        try{
            return objectMapper.readValue(src, objectMapper.getTypeFactory().constructParametricType(ArrayList.class, clazz));
        }catch (Exception e) {
            log.error("jsonConvertToList fail error=", e);
            return Collections.emptyList();
        }
    }

}

