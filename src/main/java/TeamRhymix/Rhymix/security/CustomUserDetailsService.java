package TeamRhymix.Rhymix.security;

import TeamRhymix.Rhymix.domain.User;
import TeamRhymix.Rhymix.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String nickname) throws UsernameNotFoundException {
        User user = userRepository.findByNickname(nickname);  // nickname을 기준으로 조회
        if (user == null) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + nickname);
        }

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getNickname()) // nickname을 Spring Security의 username으로 설정
                .password(user.getPassword())
                .roles("USER")
                .build();
    }

}

