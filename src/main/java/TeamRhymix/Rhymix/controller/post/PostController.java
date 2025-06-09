package TeamRhymix.Rhymix.controller.post;

import TeamRhymix.Rhymix.domain.Chat;
import TeamRhymix.Rhymix.domain.Post;
import TeamRhymix.Rhymix.dto.PostDto;
import TeamRhymix.Rhymix.mapper.PostMapper;
import TeamRhymix.Rhymix.service.PostService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("api/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final PostMapper postMapper;
    private final MongoTemplate mongoTemplate;

    /**
     * 오늘의 추천곡 저장 API
     * - 클라이언트로부터 받은 PostDto를 Post 엔티티로 변환 후 저장합니다.
     * - 저장된 Post 엔티티를 다시 DTO로 변환해 반환합니다.
     *
     * @param postDto 오늘의 추천곡 정보
     * @return 저장된 추천곡 정보 (PostDto)
     */
    @PostMapping
    public ResponseEntity<PostDto> createPost(@RequestBody PostDto postDto) {
        Post saved = postService.savePost(postDto);
        return ResponseEntity.ok(postMapper.toDto(saved));
    }

    @GetMapping("/today")
    public ResponseEntity<PostDto> getTodayPost(
            @RequestParam(required = false) String nickname,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails
    ) {
        if (nickname == null) {
            if (userDetails == null) {
                return ResponseEntity.status(401).build();
            }
            nickname = userDetails.getUsername(); // 본인 닉네임
        }

        Post post = postService.getTodayPost(nickname);

        return post != null
                ? ResponseEntity.ok(postMapper.toDto(post))
                : ResponseEntity.notFound().build();
    }



    @GetMapping("/monthly")
    public ResponseEntity<List<PostDto>> getMonthlyPosts(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails,
            @RequestParam int year,
            @RequestParam int month
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        String nickname = userDetails.getUsername(); // = userId

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1);

        List<Post> posts = postService.findPostsByUserAndMonth(nickname, startDate, endDate);
        List<PostDto> dtos = posts.stream()
                .map(postMapper::toDto)
                .toList();

        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/{postId}/chat")
    public ResponseEntity<?> addChatToPost(@PathVariable String postId,
                                           @RequestBody Chat chat) {
        chat.setCreatedAt(LocalDateTime.now());

        // 게시글 찾기
        Post post = mongoTemplate.findById(postId, Post.class);
        if (post == null) {
            return ResponseEntity.notFound().build();
        }

        // 댓글 추가 및 저장
        post.getChats().add(chat);
        mongoTemplate.save(post);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{postId}/chats")
    public ResponseEntity<List<Chat>> getChatsForPost(@PathVariable String postId) {
        Post post = mongoTemplate.findById(postId, Post.class);
        if (post == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(post.getChats());
    }



}
