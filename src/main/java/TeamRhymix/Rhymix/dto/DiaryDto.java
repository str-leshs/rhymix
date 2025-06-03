package TeamRhymix.Rhymix.dto;

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
}
