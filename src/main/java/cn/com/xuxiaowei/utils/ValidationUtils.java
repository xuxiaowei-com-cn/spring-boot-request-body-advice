package cn.com.xuxiaowei.utils;

import cn.com.xuxiaowei.exception.ParamException;
import com.google.common.base.Joiner;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * 验证工具类
 *
 * @author 徐晓伟
 */
public class ValidationUtils {

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    /**
     * 验证数据
     *
     * @param object 数据
     * @throws ParamException 参数异常
     */
    public static void validate(Object object) throws ParamException {

        Set<ConstraintViolation<Object>> validate = VALIDATOR.validate(object);

        // 验证结果异常
        if (validate.size() > 0) {
            List<String> fieldList = new LinkedList<>();
            List<String> msgList = new LinkedList<>();
            for (ConstraintViolation<Object> next : validate) {
                fieldList.add(next.getPropertyPath().toString());
                msgList.add(next.getMessage());
            }

            String field = Joiner.on(",").join(fieldList);
            String msg = Joiner.on(",").join(msgList);

            throw new ParamException("A001", msg, field);
        }
    }

}
