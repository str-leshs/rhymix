package TeamRhymix.Rhymix.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiaryDto {
    private String nickname;
    private String diaryTitle;
    private String diaryContent;
    private String diaryImage;
    private boolean deleteImage;
}
