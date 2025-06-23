package TeamRhymix.Rhymix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.beans.factory.annotation.Value;
import jakarta.annotation.PostConstruct;

@SpringBootApplication(scanBasePackages = "TeamRhymix.Rhymix")  // ✅ 명시적으로 모든 패키지 스캔
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
