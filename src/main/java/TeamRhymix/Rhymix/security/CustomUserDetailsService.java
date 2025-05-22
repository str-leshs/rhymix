package TeamRhymix.Rhymix.security;

import TeamRhymix.Rhymix.domain.User;
import TeamRhymix.Rhymix.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor    //final 필드를 자동으로 생성자 주입
public class CustomUserDetailsService implements UserDetailsService {

    //사용자 정보를 조회할 커스텀 UserRepository (nickname을 기준으로 사용자 조회 가능)
    private final UserRepository userRepository;

    //nickname은 SecurityConfig에서 usernameParameter("nickname")으로 지정한 값
    //로그인 시 이 메서드가 호출되어 사용자를 인증함
    @Override
    public UserDetails loadUserByUsername(String nickname) throws UsernameNotFoundException {
        User user = userRepository.findByNickname(nickname);  // nickname을 기준으로 조회
        if (user == null) { //nickname으로 사용자를 찾지 못하면 → Spring Security는 로그인 실패로 처리
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + nickname);
        }

        // Spring Security가 기본 제공하는 User 객체를 생성
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getNickname()) // nickname을 Spring Security의 username으로 설정
                .password(user.getPassword())
                .roles("USER")
                .build();
    }

}

