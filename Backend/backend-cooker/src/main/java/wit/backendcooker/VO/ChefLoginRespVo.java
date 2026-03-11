package wit.backendcooker.VO;

import lombok.Data;

/*
 * @author ：jee
 * @date ：2026/3/816:58
 * @version: 1.0
 --------------------------
 */
@Data

public class ChefLoginRespVo {
    private String token;
    private String msg;
    //200登录成功 400登录失败
    private String code;






}
