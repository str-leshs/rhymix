package TeamRhymix.Rhymix.domain.neighbor.service;

import TeamRhymix.Rhymix.domain.neighbor.entity.Neighbor;
import TeamRhymix.Rhymix.domain.user.entity.User;
import TeamRhymix.Rhymix.domain.neighbor.dto.NeighborDto;
import TeamRhymix.Rhymix.domain.user.dto.UserDto;
import TeamRhymix.Rhymix.domain.neighbor.mapper.NeighborMapper;
import TeamRhymix.Rhymix.domain.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NeighborServiceImpl implements NeighborService {

    private final MongoTemplate mongoTemplate;
    private final NeighborMapper neighborMapper;
    private final UserMapper userMapper;

    @Override
    public List<NeighborDto> getNeighbors(String ownerNickname) {
        // neighbors 컬렉션에서 이웃 닉네임 목록 조회
        Query query = new Query(Criteria.where("ownerNickname").is(ownerNickname));
        Neighbor neighbor = mongoTemplate.findOne(query, Neighbor.class);

        if (neighbor == null || neighbor.getNeighbors() == null || neighbor.getNeighbors().isEmpty()) {
            return List.of();
        }

        // users 컬렉션에서 이웃 사용자 정보 조회
        Query userQuery = new Query(Criteria.where("nickname").in(neighbor.getNeighbors()));
        List<User> users = mongoTemplate.find(userQuery, User.class);

        // User → NeighborDto 변환
        return users.stream()
                .map(neighborMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void addNeighbor(String ownerNickname, String neighborNickname) {
        // 이웃 닉네임을 neighbors 배열에 추가 (중복 방지)
        Query query = new Query(Criteria.where("ownerNickname").is(ownerNickname));
        Update update = new Update().addToSet("neighbors", neighborNickname);
        mongoTemplate.upsert(query, update, Neighbor.class);
    }

    @Override
    public List<NeighborDto> getSuggestedNeighbors(String currentNickname) {
        // 본인 및 기존 이웃 제외
        Query neighborQuery = new Query(Criteria.where("ownerNickname").is(currentNickname));
        Neighbor neighbor = mongoTemplate.findOne(neighborQuery, Neighbor.class);

        List<String> excludedNicknames = new ArrayList<>();
        excludedNicknames.add(currentNickname); // 본인 제외
        if (neighbor != null && neighbor.getNeighbors() != null) {
            excludedNicknames.addAll(neighbor.getNeighbors());
        }

        // 현재 유저의 선호 장르 조회
        Query currentUserQuery = new Query(Criteria.where("nickname").is(currentNickname));
        User currentUser = mongoTemplate.findOne(currentUserQuery, User.class);
        List<String> myGenres = currentUser != null && currentUser.getPreferredGenres() != null
                ? currentUser.getPreferredGenres()
                : List.of();

        if (myGenres.isEmpty()) {
            return List.of(); // 선호 장르가 없으면 추천 불가
        }

        // 나와 같은 장르를 가진 유저들 중, 본인과 기존 이웃 제외
        Criteria criteria = new Criteria().andOperator(
                Criteria.where("preferredGenres").in(myGenres),
                Criteria.where("nickname").nin(excludedNicknames)
        );

        Query userQuery = new Query(criteria);
        List<User> candidates = mongoTemplate.find(userQuery, User.class);

        // 무작위 5명 추출
        Collections.shuffle(candidates);
        return candidates.stream()
                .limit(5)
                .map(neighborMapper::toDto)
                .collect(Collectors.toList());
    }


    @Override
    public void removeNeighbor(String ownerNickname, String targetNickname) {
        Query query = new Query(Criteria.where("ownerNickname").is(ownerNickname));
        Update update = new Update().pull("neighbors", targetNickname);
        mongoTemplate.updateFirst(query, update, Neighbor.class);
    }

    @Override
    public Page<NeighborDto> searchNeighbors(String myNickname, String genre, String keyword, int page, int size) {
        // 제외 대상 닉네임 목록 (본인 + 기존 이웃)
        Query neighborQuery = new Query(Criteria.where("ownerNickname").is(myNickname));
        Neighbor neighbor = mongoTemplate.findOne(neighborQuery, Neighbor.class);

        List<String> excludedNicknames = new ArrayList<>();
        excludedNicknames.add(myNickname);
        if (neighbor != null && neighbor.getNeighbors() != null) {
            excludedNicknames.addAll(neighbor.getNeighbors());
        }

        // 검색 조건 구성
        Criteria criteria = new Criteria();
        List<Criteria> conditions = new ArrayList<>();

        // 본인 및 기존 이웃 제외 조건
        conditions.add(Criteria.where("nickname").nin(excludedNicknames));

        //genre 파라미터 정규화 후 검색 조건 추가
        if (genre != null && !genre.isEmpty()) {
            String normalizedGenre = genre.toLowerCase().replace("-", "").replace(" ", "");
            conditions.add(Criteria.where("preferredGenres").in(List.of(normalizedGenre)));
        }

        // 이웃의 아이디로 검색
        if (keyword != null && !keyword.isEmpty()) {
            conditions.add(Criteria.where("nickname").regex(".*" + keyword + ".*", "i"));
        }

        // 조건 통합
        criteria.andOperator(conditions.toArray(new Criteria[0]));

        Query query = new Query(criteria);
        long total = mongoTemplate.count(query, User.class);
        query.skip((long) (page - 1) * size).limit(size);

        List<User> result = mongoTemplate.find(query, User.class);
        List<NeighborDto> dtoList = result.stream()
                .map(user -> new NeighborDto(
                        user.getNickname(),
                        user.getProfileImage(),
                        user.getPreferredGenres() != null ? user.getPreferredGenres() : List.of()
                ))
                .toList();

        return new PageImpl<>(dtoList, PageRequest.of(page - 1, size), total);
    }



    @Override
    public int countSearchResults(String myNickname, String genre, String keyword) {
        // 본인 및 기존 이웃 제외
        Query neighborQuery = new Query(Criteria.where("ownerNickname").is(myNickname));
        Neighbor neighbor = mongoTemplate.findOne(neighborQuery, Neighbor.class);

        List<String> excludedNicknames = new ArrayList<>();
        excludedNicknames.add(myNickname);
        if (neighbor != null && neighbor.getNeighbors() != null) {
            excludedNicknames.addAll(neighbor.getNeighbors());
        }

        // 검색 조건 구성
        Criteria criteria = new Criteria();
        List<Criteria> andConditions = new ArrayList<>();

        andConditions.add(Criteria.where("nickname").nin(excludedNicknames));

        // genre 파라미터 정규화 후 검색 조건 추가
        if (genre != null && !genre.isBlank()) {
            String normalizedGenre = genre.toLowerCase().replace("-", "").replace(" ", "");
            andConditions.add(Criteria.where("preferredGenres").in(List.of(normalizedGenre)));
        }

        // 이웃의 아이디로 검색
        if (keyword != null && !keyword.isBlank()) {
            andConditions.add(Criteria.where("nickname").regex(".*" + keyword + ".*", "i"));
        }

        criteria.andOperator(andConditions.toArray(new Criteria[0]));
        Query countQuery = new Query(criteria);
        return (int) mongoTemplate.count(countQuery, User.class);
    }

    @Override
    public UserDto getNeighborProfile(String nickname) {
        Query query = new Query(Criteria.where("nickname").is(nickname));
        User user = mongoTemplate.findOne(query, User.class);
        if (user == null) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다: " + nickname);
        }
        return userMapper.toDto(user);
    }



}
