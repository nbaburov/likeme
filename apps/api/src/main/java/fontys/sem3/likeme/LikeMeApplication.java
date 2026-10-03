package fontys.sem3.likeme;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@SpringBootApplication
public class LikeMeApplication {

	public static void main(String[] args) {
		SpringApplication.run(LikeMeApplication.class, args);
	}

}
