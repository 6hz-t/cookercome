package com.hs.backend.vo;


import lombok.Data;

@Data
public class UserQueryVo {

    private String keyword;  // 搜索关键词（用户名/ID）
    private Integer pageNum = 1;  // 当前页（默认第1页）
    private Integer pageSize = 10; // 每页条数（默认10条）
}
