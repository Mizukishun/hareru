package org.kiharu.hareru;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@MapperScan("org.kiharu.hareru.mapper")
public class HareruApplication {

    public static void main(String[] args) {
        SpringApplication.run(HareruApplication.class, args);
    }

}
