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
@RequestMapping("/api")
public class ChefTestController {

    @PostMapping("/test")
    public String test() {
        return "test111aaadwsdaa";
    }
}
