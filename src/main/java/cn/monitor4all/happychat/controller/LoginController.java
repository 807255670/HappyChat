package cn.monitor4all.happychat.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created By Fan Huiliang
 * 2019-07-20 20:15
 */
@Controller
@RequestMapping("/")
public class LoginController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    StringRedisTemplate redisTemplate;

    /**
     * 判断是否已登录，已登录就直接跳转主页
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    @GetMapping("/")
    public ModelAndView index(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException{
        LOGGER.info("has cookie?:"+String.valueOf(request.getCookies()!=null));
        if(request.getCookies()!=null){
            for(Cookie cookie:request.getCookies()){
                if(cookie.getName().equals("token")){
                    String token = cookie.getValue();
                    if(redisTemplate.hasKey("token_"+token)){
                        request.setAttribute("userid",redisTemplate.opsForValue().get("token_"+token));
                        return new ModelAndView("chat");
                    }
                }
            }
        }
        return new ModelAndView("redirect:/auth");
    }

    /**
     * 重定向到阿里认证
     * @param request
     * @return
     */
    @GetMapping("/auth")
    public String auth(HttpServletRequest request){
        LOGGER.info("auth----");
        return "redirect:"+"https://openauth.alipay.com/oauth2/publicAppAuthorize.htm?"+
                "app_id=2019071465849264&scope=auth_user&redirect_uri="+
                "http://dwtqhe.natappfree.cc/alipay/auth";
    }

    /**
     * 设置cokie和session  返回主页
     * @param response
     * @param request
     * @return
     */
    @GetMapping("/login")
    public ModelAndView login(HttpServletResponse response, HttpServletRequest request){
        LOGGER.info("logining------");
        String userid = request.getAttribute("userid").toString();

        //设置token至redis
        String token = UUID.randomUUID().toString();
        Integer expire = 7200;//2h
        redisTemplate.opsForValue().set("token_"+token,userid,expire, TimeUnit.SECONDS);

        //设置token至cookie
        Cookie cookie = new Cookie("token",token);
        cookie.setPath("/");
        cookie.setMaxAge(7200);
        response.addCookie(cookie);
        return new ModelAndView("chat");
    }

    @GetMapping("/logout")
    public ModelAndView logout(HttpServletRequest request,
                               HttpServletResponse response,
                               Map<String,Object> map){
        Cookie tokenCookie=null;
        for(Cookie cookie:request.getCookies()){
            if("token".equals(cookie.getName())){
                tokenCookie = cookie;
            }
        }
        if(tokenCookie != null){
            //清除redis
            redisTemplate.opsForValue().getOperations().delete("token_"+tokenCookie.getValue());

            //清除token
            Cookie cookie = new Cookie("token",null);
            cookie.setPath("/");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }

        map.put("url","/wechat_order/seller/login.html");
        return new ModelAndView("common/success",map);
    }

}
