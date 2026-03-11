package wit.backendcooker.Service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import wit.backendcooker.Entity.ChefInfo;
import wit.backendcooker.Mapper.ChefInfoMapper;
import wit.backendcooker.Service.ChefInfoService;
import org.springframework.stereotype.Service;
import wit.backendcooker.VO.ChefInfoVo;

/**
* @author lenovo
* @description 针对表【t_chef_info(厨师专属信息表)】的数据库操作Service实现
* @createDate 2026-03-09 20:40:49
*/
@Service
public class ChefInfoServiceImpl extends ServiceImpl<ChefInfoMapper, ChefInfo>
    implements ChefInfoService{

    @Override
    public ChefInfoVo getChefInfoByUsername(Long userId) {
        ChefInfo  chef =baseMapper.selectOne(new LambdaQueryWrapper<ChefInfo>().eq(ChefInfo::getUserId, userId));


        ChefInfoVo chefInfoVo = new ChefInfoVo();

        return chefInfoVo;
    }
}




