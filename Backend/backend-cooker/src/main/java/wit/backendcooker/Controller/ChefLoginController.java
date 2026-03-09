package wit.backendcooker.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wit.backendcooker.Result.Result;
import wit.backendcooker.VO.ChefLoginRespVo;
import wit.backendcooker.VO.ChefLoginVo;

/*
 * @author ：jee
 * @date ：2026/3/813:17
 * @version: 1.0
 --------------------------
 */
@RestController
@RequestMapping("/api/chef")
@Tag(name = "登录注册")

public class ChefLoginController {


    @Autowired
    private wit.backendcooker.service.UserService userService;
    @Operation( summary = "登录")
    @PostMapping("/login")
    public Result<ChefLoginRespVo> login(@RequestBody ChefLoginVo chefLoginVo) {


        return Result.success(userService.login(chefLoginVo));
    }
}
