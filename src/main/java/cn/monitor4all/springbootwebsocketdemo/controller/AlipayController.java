package cn.monitor4all.springbootwebsocketdemo.controller;

import cn.monitor4all.springbootwebsocketdemo.service.AlipayLoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created By Fan Huiliang
 * 2019-07-14 15:01
 */
@Controller
@RequestMapping("/alipay")
public class AlipayController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlipayController.class);
    @Autowired
    private AlipayLoginService alipayLoginService;

    @RequestMapping("/auth")
    public String getAuthCode(HttpServletRequest request) {

        LOGGER.info("alipay auth-----");
        //从request中获取授权信息
        String authCode = request.getParameter("auth_code");

        if (!StringUtils.isEmpty(authCode)) {
            //获取userid
            String userid = alipayLoginService.getUserid(authCode);
            request.setAttribute("userid",userid);
            return "forward:/login";
        }
        return "";
    }
}

