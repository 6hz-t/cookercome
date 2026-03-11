package com.hs.backend.cooker.service.impl;

import com.hs.backend.cooker.entity.Cooker;
import com.hs.backend.cooker.repository.CookerRepository;
import com.hs.backend.cooker.service.CookerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 厨师服务实现类
 */
@Service
public class CookerServiceImpl implements CookerService {
    
    @Autowired
    private CookerRepository cookerRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public Cooker login(String username, String password) {
        Optional<Cooker> cookerOpt = cookerRepository.findByUsername(username);
        if (cookerOpt.isEmpty()) {
            throw new RuntimeException("用户名不存在");
        }
        
        Cooker cooker = cookerOpt.get();
        if (!passwordEncoder.matches(password, cooker.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        
        if (cooker.getStatus() != null && cooker.getStatus() == 2) {
            throw new RuntimeException("账号已被禁用");
        }
        
        return cooker;
    }
    
    @Override
    public Cooker register(Cooker cooker) {
        // 检查用户名是否存在
        if (cookerRepository.existsByUsername(cooker.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 检查手机号是否存在
        if (cooker.getPhone() != null && cookerRepository.existsByPhone(cooker.getPhone())) {
            throw new RuntimeException("手机号已被注册");
        }
        
        // 加密密码
        cooker.setPassword(passwordEncoder.encode(cooker.getPassword()));
        
        // 设置默认状态：0-审核中
        if (cooker.getStatus() == null) {
            cooker.setStatus(0);
        }
        
        cooker.setCreateTime(new Date());
        cooker.setUpdateTime(new Date());
        
        return cookerRepository.save(cooker);
    }
    
    @Override
    public Optional<Cooker> getById(String id) {
        return cookerRepository.findById(id);
    }
    
    @Override
    public Optional<Cooker> getByUsername(String username) {
        return cookerRepository.findByUsername(username);
    }
    
    @Override
    public Cooker update(Cooker cooker) {
        Optional<Cooker> existingOpt = cookerRepository.findById(cooker.getId());
        if (existingOpt.isEmpty()) {
            throw new RuntimeException("厨师不存在");
        }
        
        Cooker existing = existingOpt.get();
        
        // 更新字段
        if (cooker.getRealName() != null) {
            existing.setRealName(cooker.getRealName());
        }
        if (cooker.getPhone() != null) {
            existing.setPhone(cooker.getPhone());
        }
        if (cooker.getIdCard() != null) {
            existing.setIdCard(cooker.getIdCard());
        }
        if (cooker.getAvatar() != null) {
            existing.setAvatar(cooker.getAvatar());
        }
        if (cooker.getIntroduction() != null) {
            existing.setIntroduction(cooker.getIntroduction());
        }
        if (cooker.getDishKinds() != null) {
            existing.setDishKinds(cooker.getDishKinds());
        }
        if (cooker.getCertificates() != null) {
            existing.setCertificates(cooker.getCertificates());
        }
        
        existing.setUpdateTime(new Date());
        
        return cookerRepository.save(existing);
    }
    
    @Override
    public List<Cooker> getAll() {
        return cookerRepository.findAll();
    }
    
    @Override
    public void delete(String id) {
        cookerRepository.deleteById(id);
    }
}
