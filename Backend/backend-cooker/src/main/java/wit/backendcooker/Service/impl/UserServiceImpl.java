package wit.backendcooker.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import wit.backendcooker.Entity.User;
import wit.backendcooker.Utils.JwtTokenUtil;
import wit.backendcooker.Utils.RedisUtil;
import wit.backendcooker.VO.ChefLoginRespVo;
import wit.backendcooker.VO.ChefLoginVo;
import wit.backendcooker.service.UserService;
import wit.backendcooker.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author lenovo
* @description 针对表【t_user(统一用户表（客户 + 厨师 + 管理员）)】的数据库操作Service实现
* @createDate 2026-03-08 14:22:30
*/

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{


    @Autowired
    private JwtTokenUtil jwtTokenUtil;


    @Autowired
    private RedisUtil redisUtil;
    @Override
    public ChefLoginRespVo login(ChefLoginVo chefLoginVo) {
       log.info("用户登录中，用户名：{}", chefLoginVo.getUsername());
       String token = jwtTokenUtil.generateToken(chefLoginVo.getUsername());
       ChefLoginRespVo chefLoginRespVo = new ChefLoginRespVo();
       chefLoginRespVo.setToken(token);
       chefLoginRespVo.setUsername(chefLoginVo.getUsername());
       //键：token:{accessToken字符串}（精准定位单个 Token）；值：用户 ID（极简存储，快速关联用户）；
        redisUtil.set("token:" + token, chefLoginVo.getUsername());

        log.info("用户登录成功，用户名：{}", chefLoginVo.getUsername());


       return chefLoginRespVo;
    }
}




