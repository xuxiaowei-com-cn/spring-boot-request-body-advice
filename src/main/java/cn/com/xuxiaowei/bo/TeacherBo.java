package cn.com.xuxiaowei.bo;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 老师
 *
 * @author xuxiaowei
 * @since 0.0.1
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TeacherBo extends Request {

    private String username;

    private String password;

}
