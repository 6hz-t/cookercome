package wit.backendcooker.VO;

/*
 * @author ：jee
 * @date ：2026/3/921:16
 * @version: 1.0
 --------------------------
 */

import lombok.Data;

@Data
public class ChefInfoVo {





        /*
        * 姓名
        * 手机号
        * 身份证正面
        * 身份证反面
        * 性别
        * 身份证号
        * 头像
        * 注册时间
        * 状态（0：在线，1：离线）
        * 评分
        * 位置经度
        * 位置纬度
        * 简介
        * 审核状态
        * 菜系
        *
        *
        *
        * 城市
        * 地区
        * 详细地址
        *
        *
        * */
        private String phone;
        private String password;
        private String idCardFront;
        private String idCardBack;
        private String name;
        private String sex;






}
