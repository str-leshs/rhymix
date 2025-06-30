package TeamRhymix.Rhymix.controller.post;

import TeamRhymix.Rhymix.domain.User;
import TeamRhymix.Rhymix.domain.Chat;
import TeamRhymix.Rhymix.domain.Post;
import TeamRhymix.Rhymix.dto.PostDto;
import TeamRhymix.Rhymix.mapper.PostMapper;
import TeamRhymix.Rhymix.service.PostService;

import TeamRhymix.Rhymix.domain.Track;
import TeamRhymix.Rhymix.dto.PostRequestDto;
import TeamRhymix.Rhymix.dto.PostResponseDto;


import TeamRhymix.Rhymix.service.TrackService;
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
    private final TrackService trackService;
    private final PostMapper postMapper;
    private final MongoTemplate mongoTemplate;

    /**
     * 오늘의 추천곡 저장 API
     */
    @PostMapping
    public ResponseEntity<PostResponseDto> createPost(
            @RequestBody PostRequestDto request,
            @AuthenticationPrincipal User userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        String userId = userDetails.getUsername();

        // 트랙 먼저 저장
        Track track = trackService.findOrSaveTrack(request.getTrackId());

        // 포스트 저장
        Post saved = postService.savePost(request, userId);

        PostResponseDto response = PostResponseDto.builder()
                .postId(saved.getId())
                .trackTitle(track.getTitle())
                .trackArtist(track.getArtist())
                .mood(request.getMood())
                .weather(request.getWeather())
                .comment(request.getComment())
                .build();

        return ResponseEntity.ok(response);
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