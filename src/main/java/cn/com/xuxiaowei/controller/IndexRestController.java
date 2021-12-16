package cn.com.xuxiaowei.controller;

import cn.com.xuxiaowei.annotation.DecryptAnnotation;
import cn.com.xuxiaowei.bo.TeacherBo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 主页
 *
 * @author xuxiaowei
 * @since 0.0.1
 */
@RestController
public class IndexRestController {

    /**
     * 主页
     *
     * @param request  请求
     * @param response 响应
     * @return 返回 数据
     */
    @RequestMapping
    public ResponseEntity<Map<String, Object>> index(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<>(4);

        return ResponseEntity.ok(map);
    }

    /**
     * 解密
     *
     * @param request  请求
     * @param response 响应
     * @param teacherBo  加密参数
     * @return 返回 数据
     */
    @RequestMapping("/decrypt")
    public ResponseEntity<Map<String, Object>> decrypt(HttpServletRequest request, HttpServletResponse response,
                                                       @RequestBody TeacherBo teacherBo) {
        Map<String, Object> map = new HashMap<>(4);

        map.put("teacher", teacherBo);

        return ResponseEntity.ok(map);
    }

    /**
     * 解密
     *
     * @param request   请求
     * @param response  响应
     * @param teacherBo 加密参数
     * @return 返回 数据
     */
    @RequestMapping("/decrypt/interface")
    @DecryptAnnotation(interfaceName = "interface-1")
    public ResponseEntity<Map<String, Object>> decryptAnnotation(HttpServletRequest request, HttpServletResponse response,
                                                                 @RequestBody TeacherBo teacherBo) {
        Map<String, Object> map = new HashMap<>(4);

        map.put("teacher", teacherBo);

        return ResponseEntity.ok(map);
    }

}
