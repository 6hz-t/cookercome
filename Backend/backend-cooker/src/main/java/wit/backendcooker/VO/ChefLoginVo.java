package wit.backendcooker.VO;

/*
 * @author ：jee
 * @date ：2026/3/815:58
 * @version: 1.0
 --------------------------
 */

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChefLoginVo {

    @NotBlank
    private String username;
    @NotBlank
    private String password;
}
