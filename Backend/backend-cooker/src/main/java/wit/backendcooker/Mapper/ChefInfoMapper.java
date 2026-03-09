package wit.backendcooker.mapper;

import org.apache.ibatis.annotations.Mapper;
import wit.backendcooker.Entity.ChefInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author lenovo
* @description 针对表【t_chef_info(厨师专属信息表)】的数据库操作Mapper
* @createDate 2026-03-09 20:40:49
* @Entity wit.backendcooker.Entity.ChefInfo
*/
@Mapper
public interface ChefInfoMapper extends BaseMapper<ChefInfo> {

}




