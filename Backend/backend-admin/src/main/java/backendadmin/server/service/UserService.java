package backendadmin.server.service;

import backendadmin.pojo.dto.LoginDTO;
import backendadmin.pojo.entity.User;

import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

/**
* @author air
* @description 针对表【t_user(统一用户表（客户 + 厨师 + 管理员）)】的数据库操作Service
*/
@Service
public interface UserService {

    //用户登录校验
    User adminLogin(@Valid LoginDTO loginDTO);
}
