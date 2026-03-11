package backendadmin.server.service.impl;

import backendadmin.pojo.dto.LoginDTO;

import backendadmin.pojo.entity.User;
import backendadmin.server.service.UserService;
import backendadmin.server.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

/**
* @author air
* @description 针对表【t_user(统一用户表（客户 + 厨师 + 管理员）)】的数据库操作Service实现
* @createDate 2026-03-11 10:27:48
*/
@Service
public class UserServiceImpl  implements UserService{

    @Autowired
    private UserMapper userMapper;

    // 使用BCrypt加密（需先配置Bean）
    @Autowired(required = false) // 非必须，兼容MD5
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public User adminLogin(LoginDTO loginDTO) {
        // 1. 根据id查询用户
        User dbUser = userMapper.selectById(Integer.parseInt(loginDTO.getUsername()));

        // 2. 校验用户是否存在
        if (dbUser == null) {
            throw new RuntimeException("用户名不存在");
        }

        // 3. 核心：校验是否为管理员（role=2）
        if (dbUser.getRole() != 2) {
            throw new RuntimeException("该账号无管理员权限");
        }

        // 4. 校验密码（兼容BCrypt/MD5，优先BCrypt）
        boolean passwordMatch;
        if (passwordEncoder != null) {
            // BCrypt加密校验（推荐）
            passwordMatch = passwordEncoder.matches(loginDTO.getPassword(), dbUser.getPassword());
        } else {
            // MD5加密校验（兼容旧数据）
            String inputPwd = DigestUtils.md5DigestAsHex(loginDTO.getPassword().getBytes());
            passwordMatch = inputPwd.equals(dbUser.getPassword());
        }

        if (!passwordMatch) {
            throw new RuntimeException("密码错误");
        }

        // 5. 脱敏：隐藏密码后返回
        dbUser.setPassword(null);
        return dbUser;
    }
}




