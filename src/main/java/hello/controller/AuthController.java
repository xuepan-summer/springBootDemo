package hello.controller;

import hello.service.User;
import hello.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import java.util.Map;

@Controller
public class AuthController {
    private UserDetailsService userDetailsService;
    private AuthenticationManager authenticationManager;
    private UserService userService;

    @Inject
    public AuthController(UserService userService, UserDetailsService userDetailsService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.userDetailsService = userDetailsService;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("/auth")
    @ResponseBody
    public Object auth() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByUserName(username);
        if (username.contains("anonymous")) {
            return new Result("ok", "用户未登录", false);
        }
        return new Result("ok", "用户已登录", true, user);
    }

    @PostMapping("/auth/login")
    @ResponseBody
    public Result login(@RequestBody Map<String, String> usernameAndPasswordJson) {
        //1.从前台拿到输入的用户名密码
        String username = usernameAndPasswordJson.get("username");
        String password = usernameAndPasswordJson.get("password");

        UserDetails userDetails = null;
        //根据用户名去数据库拿密码
        try {
            userDetails = userDetailsService.loadUserByUsername(username);
        } catch (UsernameNotFoundException e) {
            return new Result("fail", "用户不存在", false);
        }
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());

        try {
            //输入密码与查询密码进行比对
            authenticationManager.authenticate(token);
            //用户信息存储在cookie
            SecurityContextHolder.getContext().setAuthentication(token);

            User loggedUser = userService.getUserByUserName(username);
            System.out.println(loggedUser);
            return new Result("ok", "登录成功", true, loggedUser);
        } catch (BadCredentialsException e) {
            return new Result("fail", "密码不正确", false);
        }
    }

    @PostMapping("/auth/register")
    @ResponseBody
    public Result register(@RequestBody Map<String, String> usernameAndPasswordJson) {
        String username = usernameAndPasswordJson.get("username");
        String password = usernameAndPasswordJson.get("password");
        if (meetUserCondition(username) && meetPasswordCondition(password)) {
            User user = userService.getUserByUserName(username);
            if (user == null) {
                userService.save(username, password);
                //修改时间！！！
                User registerUser = userService.getUserByUserName(username);
                return new Result("ok", "注册成功", false, registerUser);
            } else {
                return new Result("fail", "该用户已被注册", false);
            }
        }
        return new Result("fail", "用户名或密码不符合要求", false);
    }

    private boolean meetPasswordCondition(String password) {
        return password.length() >= 6 && password.length() <= 16;
    }

    private boolean meetUserCondition(String username) {
        return username.length() >= 1 && username.length() <= 15;
    }

    @GetMapping("/auth/logout")
    @ResponseBody
    public Result logout() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userService.getUserByUserName(username);
        if (currentUser == null) {
            return new Result("fail", "用户尚未登录", false);
        } else {
            SecurityContextHolder.clearContext();
            return new Result("ok", "注销成功", false);
        }
    }

    public static class Result {
        String status;
        String msg;
        boolean isLogin;
        Object data;

        public Result(String status, String msg, boolean isLogin) {
            this(status, msg, isLogin, null);
        }

        public Result(String status, String msg, boolean isLogin, Object data) {
            this.status = status;
            this.msg = msg;
            this.isLogin = isLogin;
            this.data = data;
        }

        public String getStatus() {
            return status;
        }

        public String getMsg() {
            return msg;
        }

        public boolean getIsLogin() {
            return isLogin;
        }

        public Object getData() {
            return data;
        }
    }
}

