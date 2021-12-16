package cn.com.xuxiaowei.configuration;

import cn.com.xuxiaowei.exception.ParamException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link Controller}、{@link RestController} 异常处理
 *
 * @author 徐晓伟
 */
@Slf4j
@ControllerAdvice
public class ControllerAdviceConfiguration {

    /**
     * 参数异常
     *
     * @param request   请求
     * @param response  响应
     * @param exception 异常
     * @return 返回 验证结果
     */
    @ResponseBody
    @ExceptionHandler(ParamException.class)
    public Map<String, Object> paramException(HttpServletRequest request, HttpServletResponse response,
                                              ParamException exception) {
        Map<String, Object> map = new HashMap<>(4);

        map.put("code", exception.getCode());
        map.put("msg", exception.getMsg());
        map.put("field", exception.getField());

        return map;
    }

    /**
     * 无法识别的属性异常
     *
     * @param exception 异常
     * @param request   请求
     * @return 返回 验证结果
     */
    @ResponseBody
    @ExceptionHandler(UnrecognizedPropertyException.class)
    public Map<String, Object> unrecognizedPropertyException(UnrecognizedPropertyException exception, WebRequest request) {
        Map<String, Object> map = new HashMap<>(4);

        map.put("code", "B001");

        List<JsonMappingException.Reference> path = exception.getPath();
        // idCardList[0].dateList
        StringBuilder fieldStringBuilder = new StringBuilder();
        reference(path, fieldStringBuilder);

        String field = fieldStringBuilder.toString();

        map.put("msg", String.format("无效属性：%s", field));
        map.put("field", field);

        log.error("请求时数据转换失败：{}，异常：{}", map, exception);

        return map;
    }

    /**
     * 异常字段转换
     *
     * @param referenceList 异常字段
     * @param stringBuilder 异常字段节点
     */
    private void reference(List<JsonMappingException.Reference> referenceList, StringBuilder stringBuilder) {
        for (JsonMappingException.Reference reference : referenceList) {
            String fieldName = reference.getFieldName();
            if (fieldName == null) {
                String description = reference.getDescription();
                String name = ArrayList.class.getName();
                if (description.contains(name)) {
                    String replace = description.replace(name, "");
                    stringBuilder.append(replace);
                }
            } else {
                int length = stringBuilder.length();
                if (length == 0) {
                    stringBuilder.append(fieldName);
                } else {
                    stringBuilder.append(".").append(fieldName);
                }
            }
        }
    }

}
