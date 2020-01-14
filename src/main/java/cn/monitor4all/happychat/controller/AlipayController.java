package cn.monitor4all.happychat.controller;

import cn.monitor4all.happychat.service.AlipayLoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

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

    /**
     * 支付宝回调访问地址
     * @param request
     * @return
     */
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

