package cn.com.xuxiaowei.bo;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * 请求参数 - 密文
 *
 * @author xuxiaowei
 * @since 0.0.1
 */
@Data
public class RequestBo {

    /**
     * 当前调用者
     */
    @NotEmpty(message = "当前调用者 不能为空")
    private String currentUser;

    /**
     * 加密文本
     */
    @NotEmpty(message = "加密文本 不能为空")
    private String text;

}
