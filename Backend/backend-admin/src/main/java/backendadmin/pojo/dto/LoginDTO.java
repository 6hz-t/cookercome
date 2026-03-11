package backendadmin.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 登录请求DTO
 * 接收前端登录参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginDTO {

    private String username;
    private String password;




}
