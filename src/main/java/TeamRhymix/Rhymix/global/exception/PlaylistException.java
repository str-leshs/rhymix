package TeamRhymix.Rhymix.global.exception;

public class PlaylistException extends RuntimeException {
    private final ErrorCode errorCode;

    public PlaylistException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
