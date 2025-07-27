package TeamRhymix.Rhymix.global.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    FUTURE_MONTH("해당 월이 되지 않았습니다."),
    CURRENT_MONTH_INCOMPLETE("해당 월은 아직 진행 중입니다. 익월 1일에 확인해주세요."),
    NO_POSTS("추천곡이 없어 플레이리스트를 생성할 수 없습니다."),
    USER_NOT_FOUND("사용자를 찾을 수 없습니다."),
    INVALID_ID_FORMAT("잘못된 ID 형식입니다."),
    PLAYLIST_NOT_FOUND("플레이리스트를 찾을 수 없습니다."),
    UNAUTHORIZED("로그인이 필요합니다.");

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }

}
