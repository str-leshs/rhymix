package TeamRhymix.Rhymix.domain.user.repository;

import TeamRhymix.Rhymix.domain.user.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    // 닉네임으로 사용자 조회
    Optional<User> findByNickname(String nickname);

    // username으로 사용자 조회 (ID 찾기 등)
    Optional<User> findByUsername(String username);

    // 이메일 존재 여부
    boolean existsByEmail(String email);
}
