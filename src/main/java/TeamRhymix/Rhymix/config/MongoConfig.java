package TeamRhymix.Rhymix.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.mongodb.client.MongoClients;

@Configuration
public class MongoConfig {

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(
                MongoClients.create("mongodb+srv://<username>:<password>@cluster0.dhxbw4l.mongodb.net/rhymix?retryWrites=true&w=majority"),
                "rhymix"
        );
    }
}

