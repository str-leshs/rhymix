package TeamRhymix.Rhymix.service.impl;

import TeamRhymix.Rhymix.domain.Post;
import TeamRhymix.Rhymix.dto.PostDto;

public interface PostService {
    Post savePost(PostDto dto);
    Post getTodayPost(String userId);
}
