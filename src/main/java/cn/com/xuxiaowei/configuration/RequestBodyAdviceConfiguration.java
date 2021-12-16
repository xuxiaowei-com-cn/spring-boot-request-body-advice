package cn.com.xuxiaowei.configuration;


import cn.com.xuxiaowei.annotation.DecryptAnnotation;
import cn.com.xuxiaowei.bo.Request;
import cn.com.xuxiaowei.bo.RequestBo;
import cn.com.xuxiaowei.exception.ParamException;
import cn.com.xuxiaowei.utils.RequestUtils;
import cn.com.xuxiaowei.utils.ValidationUtils;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.symmetric.AES;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Type;

/**
 * {@link Controller}、{@link RestController} 请求处理
 *
 * @author xuxiaowei
 * @since 0.0.1
 */
@Slf4j
@ControllerAdvice
public class RequestBodyAdviceConfiguration implements RequestBodyAdvice {

    @Override
    public boolean supports(MethodParameter methodParameter, @NonNull Type targetType,
                            @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        return methodParameter.hasMethodAnnotation(DecryptAnnotation.class);
    }

    @NonNull
    @Override
    public HttpInputMessage beforeBodyRead(@NonNull HttpInputMessage inputMessage, @NonNull MethodParameter parameter,
                                           @NonNull Type targetType,
                                           @NonNull Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        return inputMessage;
    }

    @NonNull
    @SneakyThrows
    @Override
    public Object afterBodyRead(@NonNull Object body, @NonNull HttpInputMessage inputMessage,
                                @NonNull MethodParameter parameter, @NonNull Type targetType,
                                @NonNull Class<? extends HttpMessageConverter<?>> converterType) {

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;

        assert servletRequestAttributes != null;

        // 获取 Http 请求
        HttpServletRequest request = servletRequestAttributes.getRequest();

        // 获取请求流
        String inputStream = RequestUtils.getInputStream(request);
        // 将请求流原文放入请求中
        request.setAttribute("INPUT_STREAM", inputStream);

        log.info("解密前的数据：{}", inputStream);

        ObjectMapper objectMapper = new ObjectMapper();

        // 请求流转实体类
        RequestBo requestBo = objectMapper.readValue(inputStream, RequestBo.class);

        // 验证请求实体类
        ValidationUtils.validate(requestBo);

        // 获取注解
        DecryptAnnotation methodAnnotation = parameter.getMethodAnnotation(DecryptAnnotation.class);

        assert methodAnnotation != null;

        // 用于查询数据库（配置文件）中的 key与iv
        String interfaceName = methodAnnotation.interfaceName();
        String currentUser = requestBo.getCurrentUser();

        // 根据上面的 interfaceName 与 currentUser 查询数据库（配置文件）中的 key与iv
        String key = "9dd963975afa28a8";
        String iv = "f1d84ddc2ce249ab";

        AES aes = new AES(Mode.CTS, Padding.PKCS5Padding, key.getBytes(), iv.getBytes());

        String text = requestBo.getText();

        String textDecrypt;

        try {
            textDecrypt = aes.decryptStr(text);
            log.info("解密后的数据：{}", textDecrypt);

            // 将 输入流-解密 放入请求中
            request.setAttribute("INPUT_STREAM_DECRYPT", textDecrypt);

        } catch (Exception e) {
            log.error("解密失败：", e);
            throw new ParamException("A002", "解密失败", "text");
        }

        Object result = objectMapper.readValue(textDecrypt, body.getClass());

        if (result instanceof Request) {

            Long currentTimeMillis = ((Request) result).getCurrentTimeMillis();
            if (currentTimeMillis == null) {
                throw new ParamException("A003", "时间戳 不能为空", "currentTimeMillis");
            }

            // 有效期：60 秒
            long effective = 60 * 1000;

            // 时间差
            long expire = System.currentTimeMillis() - currentTimeMillis;

            // 是否在有效时间内
            if (Math.abs(expire) > effective) {
                throw new ParamException("A004", "时间戳 不合法", "currentTimeMillis");
            }

            return result;
        } else {
            throw new ParamException("A005", String.format("请求参数类型：%s 未继承：%s", result.getClass().getName(), Request.class.getName()), null);
        }

    }

    @Override
    public Object handleEmptyBody(Object body, @NonNull HttpInputMessage inputMessage, @NonNull MethodParameter parameter,
                                  @NonNull Type targetType, @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        return body;
    }

}
