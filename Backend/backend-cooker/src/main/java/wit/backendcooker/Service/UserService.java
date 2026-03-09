package wit.backendcooker.service;

import wit.backendcooker.Entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import wit.backendcooker.VO.ChefLoginRespVo;
import wit.backendcooker.VO.ChefLoginVo;

/**
* @author lenovo
* @description 针对表【t_user(统一用户表（客户 + 厨师 + 管理员）)】的数据库操作Service
* @createDate 2026-03-08 14:22:30
*/
public interface UserService extends IService<User> {
    ChefLoginRespVo login(ChefLoginVo chefLoginVo);
    ChefLoginRespVo register(ChefLoginVo chefLoginVo);

}
