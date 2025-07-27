package TeamRhymix.Rhymix.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //콘솔에 상황에 따른 플레이리스트 생성 불가 이유 보여줄 수 있도록
//    @ExceptionHandler(PlaylistException.class)
//    public ResponseEntity<?> handlePlaylistException(PlaylistException ex) {
//        return ResponseEntity
//                .badRequest()
//                .body(Map.of(
//                        "error", ex.getErrorCode().name(),
//                        "message", ex.getErrorCode().getMessage()
//                ));
//    }

    //TODO 사용자에게 UI 상으로 알림을 보여줄 수 있도록
    @ExceptionHandler(PlaylistException.class)
    public ResponseEntity<Map<String, String>> handlePlaylistException(PlaylistException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("errorCode", ex.getErrorCode().name());
        response.put("message", ex.getErrorCode().getMessage());
        return ResponseEntity.badRequest().body(response);
    }



}

