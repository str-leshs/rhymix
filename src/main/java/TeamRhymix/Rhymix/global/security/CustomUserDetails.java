package TeamRhymix.Rhymix.global.security;

import TeamRhymix.Rhymix.domain.user.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
public class CustomUserDetails implements UserDetails {
    //Spring Security의 핵심 인터페이스로, 인증된 사용자에 대한 정보를 제공

    private final User user;

    // 인증이 성공했을 때 User 객체를 받아 CustomUserDetails로 wrapping
    public CustomUserDetails(User user) {
        this.user = user;
    }

    //권한(Role) 설정 없이 빈 리스트 반환 -> 추후에 권한 필요 시 GrantedAuthority를 반환하도록 수정
    @Override public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override public String getPassword() {
        return user.getPassword();
    }

    @Override public String getUsername() {
        return user.getNickname();  // 로그인 기준이 nickname
    }

    @Override public boolean isAccountNonExpired() { return true; } // 계정 만료 여부 (true = 만료되지 않음)
    @Override public boolean isAccountNonLocked() { return true; }  // 계정 잠김 여부 (true = 잠기지 않음)
    @Override public boolean isCredentialsNonExpired() { return true; } // 비밀번호 만료 여부
    @Override public boolean isEnabled() { return true; }   // 계정 활성화 여부
}
