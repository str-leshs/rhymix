package TeamRhymix.Rhymix.controller;

import TeamRhymix.Rhymix.domain.Post;
import TeamRhymix.Rhymix.dto.PostDto;
import TeamRhymix.Rhymix.mapper.PostMapper;
import TeamRhymix.Rhymix.service.impl.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final PostMapper postMapper;

    @PostMapping
    public ResponseEntity<PostDto> createPost(@RequestBody PostDto postDto) {
        Post saved = postService.savePost(postDto);
        return ResponseEntity.ok(postMapper.toDto(saved));
    }

    @GetMapping("/today")
    public ResponseEntity<PostDto> getTodayPost() {
        Post post = postService.getTodayPost("lion01");
        return post != null ? ResponseEntity.ok(postMapper.toDto(post)) : ResponseEntity.notFound().build();
    }

//    @GetMapping("/today")
//    public ResponseEntity<PostDto> getTodayPost(@RequestParam String userId) {
//        Post post = postService.getTodayPost(userId);
//        return post != null ? ResponseEntity.ok(postMapper.toDto(post)) : ResponseEntity.notFound().build();
//    }
}
