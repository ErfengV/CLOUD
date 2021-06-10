package cloud.demo.controller;

import cloud.demo.bean.User;
import cloud.demo.service.HdfsService;
import cloud.demo.service.UserService;
import cloud.demo.util.RedisUtil;
import com.ac.comehome.entity.Result;
import com.ac.comehome.entity.ResultError;
import com.ac.comehome.enums.ResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;


/**
 * @author 帅气的二峰
 */
@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {

    @Autowired
    private HdfsService hdfsService;

    @Autowired
    private UserService userService;
//    /**
//     * 登录
//     *
//     * @param username 用户名
//     * @param password 密码
//     * @return
//     */
//    @PostMapping(value = "/login")
//    public Result login(@RequestParam("username") String username,
//                        @RequestParam("password") String password,
//                        HttpSession session
//    ) {
//        System.out.println(username + "===" + password);
//        if ("123456".equals(password)) {
//            RedisUtil.set(session.getId(), username, 20000);
//            return Result.success();
//        }
//        throw new ResultError(ResultCode.WRONG_ACCOUNT_OR_PWD);
//    }

    @PostMapping(value = "/register")
    public Result register(@RequestParam("username") String username,
                           @RequestParam("password1") String password1,
                           @RequestParam("password2") String password2
    ) {
        Assert.hasText(username,"用户名不能为空");
        Assert.hasText(password1,"密码不能为空");
        if(!password1.equals(password2)){
            throw new ResultError(ResultCode.WRONG_ACCOUNT_PWD);
        }
        User u=new User();
        u.setUsername(username);
        u.setPwd(password1);
        userService.insert(u);
        hdfsService.mkdir(username);
        return Result.success();

    }


}