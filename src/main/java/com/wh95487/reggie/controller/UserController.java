package com.wh95487.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wh95487.reggie.common.R;
import com.wh95487.reggie.entity.User;
import com.wh95487.reggie.service.UserService;
import com.wh95487.reggie.utils.SMSUtils;
import com.wh95487.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        String phone = user.getPhone();

        if(StringUtils.isNotEmpty(phone)){
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code={}",code);

            //SMSUtils.sendMessage("瑞吉外卖", "", phone, code);

            //将生成的验证码保存到Redis缓存，并设置过期时间
            redisTemplate.opsForValue().set(phone, code, 300, TimeUnit.SECONDS);

            //session.setAttribute(phone, code);
            return R.success(code);
        }

        return R.error("验证码发送失败");
    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session) {
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();

        //String codeInSession = session.getAttribute(phone).toString();

        String codeInSession = redisTemplate.opsForValue().get(phone).toString();
        redisTemplate.delete(phone);

        if(codeInSession != null && codeInSession.equals(code)) {
            LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
            lqw.eq(User::getPhone, phone);
            User user = userService.getOne(lqw);

            if(user == null) {
                User newUser = new User();
                newUser.setPhone(phone);
                newUser.setStatus(1);
                userService.save(newUser);
            }
            user = userService.getOne(lqw);
            session.setAttribute("user", user.getId());
            return R.success(user);
        }
        return R.error("登录失败");
    }

    @PostMapping("/loginout")
    public R<String> loginOut(HttpSession session){
        session.removeAttribute("user");
        return R.success("退出登录成功");
    }




}
