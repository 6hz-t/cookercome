package wit.backendcooker.VO;

/*
 * @author ：jee
 * @date ：2026/3/921:16
 * @version: 1.0
 --------------------------
 */

import lombok.Data;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ChefInfoVo {




        private String username;
        private String relName;
        private String phone;
        private String password;
        private String idCardFront;
        private String idCardBack;
        private Integer gender;
        private String address;
        private Float lon;
        private Float lat;
        private String introduction;

        private MultipartFile avatar;
        private MultipartFile idCardFrontFile;
        private MultipartFile idCardBackFile;
        private MultipartFile qualificationFile;












}
