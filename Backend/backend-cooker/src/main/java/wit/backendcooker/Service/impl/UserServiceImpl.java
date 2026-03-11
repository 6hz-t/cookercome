package wit.backendcooker.Service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import wit.backendcooker.Entity.ChefInfo;
import wit.backendcooker.Entity.User;
import wit.backendcooker.Mapper.ChefInfoMapper;
import wit.backendcooker.Service.ChefInfoService;
import wit.backendcooker.Utils.JwtTokenUtil;
import wit.backendcooker.Utils.RedisUtil;
import wit.backendcooker.VO.ChefLoginRespVo;
import wit.backendcooker.VO.ChefLoginVo;
import wit.backendcooker.Service.UserService;
import wit.backendcooker.Mapper.UserMapper;
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
    private ChefInfoMapper chefInfoMapper;


    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;


    @Autowired
    private RedisUtil redisUtil;
    @Override
    public ChefLoginRespVo login(ChefLoginVo chefLoginVo) {
       log.info("用户登录中，用户名：{}", chefLoginVo.getUsername());

       ChefLoginRespVo chefLoginRespVo = new ChefLoginRespVo();


        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone, chefLoginVo.getUsername())
                        .select(User::getPassword);
        User user = userMapper.selectOne(queryWrapper);
       if (user != null && user.getPassword().equals(chefLoginVo.getPassword())){
           chefLoginRespVo.setMsg("登录成功");
           chefLoginRespVo.setCode("200");
           String token = jwtTokenUtil.generateToken(chefLoginVo.getUsername());
           redisUtil.set("token:" + token, chefLoginVo.getUsername());
           chefLoginRespVo.setToken(token);
           log.info("用户登录成功，用户名：{}", chefLoginVo.getUsername());

        }else{
            chefLoginRespVo.setMsg("登录失败");
            chefLoginRespVo.setCode("400");
            log.info("用户登录失败，用户名：{}", chefLoginVo.getUsername());
        }




       return chefLoginRespVo;
    }

    @Override
    public ChefLoginRespVo register(ChefLoginVo chefLoginVo) {
        log.info("用户注册中，用户名：{}", chefLoginVo.getUsername());
        ChefLoginRespVo chefLoginRespVo = new ChefLoginRespVo();
        
        // 1. 检查手机号是否已存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone, chefLoginVo.getUsername());
        User existingUser= userMapper.selectOne(queryWrapper);
                
        if (existingUser != null) {
            log.warn("注册失败，手机号已存在：{}", chefLoginVo.getUsername());
            chefLoginRespVo.setMsg("手机号已被注册");
            chefLoginRespVo.setCode("400");
            return chefLoginRespVo;
        }
        
        // 2. 创建用户
        User newUser = new User();
        newUser.setPhone(chefLoginVo.getUsername());
        newUser.setPassword(chefLoginVo.getPassword());
        newUser.setRole(1);
        userMapper.insert(newUser);
                
        ChefInfo chefInfo = new ChefInfo();
        chefInfo.setUserId(Long.valueOf(chefLoginVo.getUsername()));
        chefInfoMapper.insert(chefInfo);
        chefLoginRespVo.setMsg("注册成功");
        chefLoginRespVo.setCode("200");
        return chefLoginRespVo;



    }
}




