package backendadmin.server.controller;


import backendadmin.common.result.Result;
import backendadmin.pojo.dto.LoginDTO;
import backendadmin.pojo.entity.User;
import backendadmin.server.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 管理员登录控制器（基于UserService）
 */
@Tag(name="sb")
@RestController
@RequestMapping("/admin")
@CrossOrigin // 解决跨域
public class LoginController {


    @Autowired
    private UserService userService;

    /**
     * 管理员登录接口
     * 仅允许role=2的用户登录
     */
    @PostMapping("/login")
    public Result<User> login(@Valid @RequestBody LoginDTO loginDTO) {
        try {
            // 调用UserService的adminLogin方法
            User adminUser = userService.adminLogin(loginDTO);
            return Result.success(adminUser);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}
