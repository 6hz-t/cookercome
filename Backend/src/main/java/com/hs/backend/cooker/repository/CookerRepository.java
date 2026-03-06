package com.hs.backend.cooker.repository;

import com.hs.backend.cooker.entity.Cooker;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * 厨师数据访问层
 */
@Repository
public interface CookerRepository extends MongoRepository<Cooker, String> {
    
    /**
     * 根据用户名查找厨师
     */
    Optional<Cooker> findByUsername(String username);
    
    /**
     * 根据手机号查找厨师
     */
    Optional<Cooker> findByPhone(String phone);
    
    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 检查手机号是否存在
     */
    boolean existsByPhone(String phone);
}
