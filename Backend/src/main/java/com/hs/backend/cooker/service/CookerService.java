package com.hs.backend.cooker.service;

import com.hs.backend.cooker.entity.Cooker;
import java.util.List;
import java.util.Optional;

/**
 * 厨师服务接口
 */
public interface CookerService {
    
    /**
     * 厨师登录
     */
    Cooker login(String username, String password);
    
    /**
     * 厨师注册
     */
    Cooker register(Cooker cooker);
    
    /**
     * 根据 ID 获取厨师
     */
    Optional<Cooker> getById(String id);
    
    /**
     * 根据用户名获取厨师
     */
    Optional<Cooker> getByUsername(String username);
    
    /**
     * 更新厨师信息
     */
    Cooker update(Cooker cooker);
    
    /**
     * 获取所有厨师
     */
    List<Cooker> getAll();
    
    /**
     * 删除厨师
     */
    void delete(String id);
}
