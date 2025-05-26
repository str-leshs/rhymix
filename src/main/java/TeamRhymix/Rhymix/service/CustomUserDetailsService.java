// src/main/java/TeamRhymix/Rhymix/security/CustomUserDetailsService.java
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
        User user = userRepository.findByNickname(nickname); // nickname으로 찾기
        if (user == null) {
            throw new UsernameNotFoundException("존재하지 않는 사용자입니다.");
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getNickname())
                .password(user.getPassword())
                .roles("USER")
                .build();
    }
}
