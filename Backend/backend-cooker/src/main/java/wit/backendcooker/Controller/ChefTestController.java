package wit.backendcooker.Controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * @author ：jee
 * @date ：2026/3/815:48
 * @version: 1.0
 --------------------------
 */
@RestController
@RequestMapping("/api/test")
public class ChefTestController {

    @PostMapping("/test001")
    public String test() {
        return "test111";
    }
}
