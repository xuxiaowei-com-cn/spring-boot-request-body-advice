package cn.com.xuxiaowei.utils;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 请求工具类
 *
 * @author xuxiaowei
 * @since 0.0.1
 */
public class RequestUtils {

    /**
     * 请求参数流
     *
     * @param request 请求
     * @return 返回 请求参数流
     * @throws IOException 读取流异常
     * @see HttpServletRequest#getContentLength()  请关注是否为负数
     */
    public static String getInputStream(HttpServletRequest request) throws IOException {
        ServletInputStream inputStream = request.getInputStream();
        int contentLength = request.getContentLength();
        if (contentLength == -1) {
            // GET 请求时
            // DELETE、HEAD、OPTIONS 请求未设置流时
            return null;
        } else {
            byte[] bytes = new byte[contentLength];
            String characterEncoding = request.getCharacterEncoding();
            inputStream.read(bytes);
            return new String(bytes, characterEncoding);
        }
    }

}
