package backendadmin.server.mapper;

import backendadmin.pojo.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;


@Mapper
public interface UserMapper  {

    /*
    * 根据id查询用户
    * */
    @Select("SELECT * from t_user where id =#{id} ")
    User selectById(int id);
}




