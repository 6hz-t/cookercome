package wit.backendcooker;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import wit.backendcooker.Utils.JwtTokenUtil;

@SpringBootTest
class BackendCookerApplicationTests {
    @Autowired
    JwtTokenUtil jwtTokenUtil = null;
    @Test
    void contextLoads() {



        String token = jwtTokenUtil.generateToken("1234567890");
        System.out.println(token);
    }

}
