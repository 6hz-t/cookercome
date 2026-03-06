package com.hs.backend.cooker.controller;

import com.hs.backend.cooker.entity.Cooker;
import com.hs.backend.cooker.service.CookerService;
import com.hs.backend.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

/**
 * 厨师控制器
 */
@RestController
@RequestMapping("/api/cooker")
@CrossOrigin(origins = "*")
public class CookerController {
    
    @Autowired
    private CookerService cookerService;
    
    /**
     * 厨师登录
     */
    @PostMapping("/login")
    public Result<Cooker> login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        
        Cooker cooker = cookerService.login(username, password);
        cooker.setPassword(null);
        
        return Result.success("登录成功", cooker);
    }
    
    /**
     * 厨师注册
     */
    @PostMapping("/register")
    public Result<Cooker> register(@RequestBody Cooker cooker) {
        Cooker registered = cookerService.register(cooker);
        registered.setPassword(null);
        
        return Result.success("注册成功，等待审核", registered);
    }
    
    /**
     * 获取厨师信息
     */
    @GetMapping("/{id}")
    public Result<Cooker> getById(@PathVariable String id) {
        Cooker cooker = cookerService.getById(id).orElseThrow(() -> new RuntimeException("厨师不存在"));
        cooker.setPassword(null);
        
        return Result.success(cooker);
    }
    
    /**
     * 更新厨师信息
     */
    @PutMapping("/{id}")
    public Result<Cooker> update(@PathVariable String id, @RequestBody Cooker cooker) {
        cooker.setId(id);
        Cooker updated = cookerService.update(cooker);
        updated.setPassword(null);
        
        return Result.success("更新成功", updated);
    }
    
    /**
     * 获取所有厨师
     */
    @GetMapping("/list")
    public Result<List<Cooker>> getAll() {
        List<Cooker> cookers = cookerService.getAll();
        cookers.forEach(c -> c.setPassword(null));
        
        return Result.success(cookers);
    }
    
    /**
     * 删除厨师
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable String id) {
        cookerService.delete(id);
        return Result.success("删除成功", null);
    }
}
