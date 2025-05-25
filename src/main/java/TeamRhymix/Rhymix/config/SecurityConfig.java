package TeamRhymix.Rhymix.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
@EnableWebSecurity  // Spring Security를 활성화하고 사용자 정의 보안 설정을 적용
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    // 사용자 인증 시 사용자 정보를 가져오기 위한 커스텀 UserDetailsService를 주입
    public SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())   //CSRF 보호는 REST API를 사용할 경우 일반적으로 비활성화
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/image/**", "/favicon.ico").permitAll()
                        .requestMatchers("/", "/info", "/join/**", "/find-id", "/find-password", "/api/users/**","/monthly").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("nickname")         // 사용자 식별자로 'nickname' 사용
                        .passwordParameter("password")         // 비밀번호 (기본값이라 생략 가능)
                        .defaultSuccessUrl("/main", true) //로그인 성공 시 이동할 페이지 -> main
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")   // 로그아웃 요청 URL
                        .logoutSuccessUrl("/")  // 로그아웃 후 이동할 페이지 -> info
                        .invalidateHttpSession(true) // 세션 무효화
                        .deleteCookies("JSESSIONID") // 쿠키 삭제
                        .permitAll()
                )
                .sessionManagement(session -> session
                        .invalidSessionUrl("/login") // 세션이 만료됐을 때 로그인 페이지로 이동
                )
                .userDetailsService(userDetailsService) //로그인 시 사용자의 인증 정보를 UserDetailsService를 통해 조회
                .build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {  //사용자 비밀번호를 BCrypt 해시 알고리즘으로 암호화
        return new BCryptPasswordEncoder();
    }

}