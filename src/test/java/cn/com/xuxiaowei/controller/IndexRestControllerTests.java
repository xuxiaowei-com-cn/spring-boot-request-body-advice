package cn.com.xuxiaowei.controller;

import cn.com.xuxiaowei.bo.RequestBo;
import cn.com.xuxiaowei.bo.TeacherBo;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.symmetric.AES;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;

/**
 * 主页 测试类
 *
 * @author 徐晓伟
 */
@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
class IndexRestControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void decryptInterface() throws Exception {

        TeacherBo teacherBo = new TeacherBo();
        teacherBo.setUsername("xxw");
        teacherBo.setPassword("123");
        teacherBo.setCurrentTimeMillis(System.currentTimeMillis());

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(teacherBo);

        String key = "9dd963975afa28a8";
        String iv = "f1d84ddc2ce249ab";
        AES aes = new AES(Mode.CTS, Padding.PKCS5Padding, key.getBytes(), iv.getBytes());
        String text = aes.encryptBase64(json);

        RequestBo requestParam = new RequestBo();
        requestParam.setText(text);
        requestParam.setCurrentUser("-");

        String content = objectMapper.writeValueAsString(requestParam);

        HttpHeaders httpHeaders = new HttpHeaders();

        MockHttpServletRequestBuilder requestBuilder =
                MockMvcRequestBuilders.post("/decrypt/interface")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .headers(httpHeaders);

        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        String contentStr = response.getContentAsString(StandardCharsets.UTF_8);

        log.info("调用结果：{}", contentStr);

    }

}
