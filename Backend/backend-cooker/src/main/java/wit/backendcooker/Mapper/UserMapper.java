package wit.backendcooker.mapper;

import org.apache.ibatis.annotations.Mapper;
import wit.backendcooker.Entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author lenovo
* @description 针对表【t_user(统一用户表（客户 + 厨师 + 管理员）)】的数据库操作Mapper
* @createDate 2026-03-08 14:22:30
* @Entity wit.backendcooker.Entity.User
*/
@Mapper
public interface UserMapper extends BaseMapper<User> {

}




