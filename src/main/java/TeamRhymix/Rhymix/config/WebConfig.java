package TeamRhymix.Rhymix.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("info");
        registry.addViewController("/info").setViewName("info");
        registry.addViewController("/login").setViewName("login/login");
        registry.addViewController("/find-id").setViewName("login/find-id");
        registry.addViewController("/find-password").setViewName("login/find-password");
        registry.addViewController("/profile").setViewName("my/profile");
        registry.addViewController("/join/agreement").setViewName("join/agreement");
        registry.addViewController("/join/form").setViewName("join/form");
        registry.addViewController("/today").setViewName("post/today");
        registry.addViewController("/customizing").setViewName("my/customizing");
        registry.addViewController("/playlist").setViewName("playlist/playlist");
        registry.addViewController("/monthly").setViewName("playlist/monthly");
        registry.addViewController("recommend").setViewName("neighbor/recommend");
    }
}
