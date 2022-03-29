package cc.jinhx.process.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Json工具类，依赖jackson
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
     * object转换成json
     */
    public static <T> String objectToJson(T obj){
        if(Objects.isNull(obj)){
            return null;
        }
        try {
            return obj instanceof String ? (String) obj : objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.error("object转换成json失败 msg={}", ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    /**
     * json转换成object
     */
    public static <T> T jsonToObject(String src, Class<T> clazz){
        if(StringUtils.isEmpty(src) || Objects.isNull(clazz)){
            return null;
        }
        try {
            return clazz.equals(String.class) ? (T) src : objectMapper.readValue(src, clazz);
        } catch (Exception e) {
            log.error("json转换成object失败 msg={}", ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    /**
     * json转换成map
     */
    public static <T> Map<String, Object> jsonToMap(String src) {
        if(StringUtils.isEmpty(src)){
            return null;
        }
        try {
            return objectMapper.readValue(src, Map.class);
        } catch (Exception e) {
            log.error("json转换成map失败 msg={}", ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    /**
     * json转换成list
     */
    public static <T> List<T> jsonToList(String jsonArrayStr, Class<T> clazz) {
        try{
            JavaType javaType = objectMapper.getTypeFactory().constructParametricType(ArrayList.class, clazz);
            return objectMapper.readValue(jsonArrayStr, javaType);
        }catch (Exception e) {
            log.error("json转换成list失败 msg={}", ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

}
