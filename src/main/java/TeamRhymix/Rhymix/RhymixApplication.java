package TeamRhymix.Rhymix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Value;
import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class RhymixApplication {

	@Value("${spring.data.mongodb.uri:NOT_FOUND}")
	private String mongoUri;

	public static void main(String[] args) {
		SpringApplication.run(RhymixApplication.class, args);
	}

	@PostConstruct
	public void logMongoUri() {
		System.out.println("🔍 Spring이 실제 사용한 URI: " + mongoUri);
	}
}
