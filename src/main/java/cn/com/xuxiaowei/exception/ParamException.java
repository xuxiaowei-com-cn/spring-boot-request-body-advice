package cn.com.xuxiaowei.exception;

import lombok.Getter;
import lombok.ToString;

/**
 * 参数异常
 *
 * @author xuxiaowei
 * @since 0.0.1
 */
@ToString
@Getter
public class ParamException extends Exception {

    /**
     * 错误码
     */
    private final String code;

    /**
     * 错误消息
     */
    private final String msg;

    /**
     * 错误字段
     */
    private final String field;

    public ParamException(String code, String msg, String field) {
        this.code = code;
        this.msg = msg;
        this.field = field;
    }

}
