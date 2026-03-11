package wit.backendcooker.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import wit.backendcooker.Entity.ChefInfo;

/**
* @author lenovo
* @description 针对表【t_chef_info(厨师专属信息表)】的数据库操作 Mapper
* @createDate 2026-03-10 15:17:51
* @Entity wit.backendcooker.Entity.ChefInfo
*/
@Mapper
public interface ChefInfoMapper extends BaseMapper<ChefInfo> {

}




