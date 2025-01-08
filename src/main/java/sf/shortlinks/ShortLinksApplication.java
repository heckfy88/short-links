package sf.shortlinks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ShortLinksApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShortLinksApplication.class, args);
    }

}
