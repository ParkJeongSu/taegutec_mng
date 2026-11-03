package kr.co.aim.common.error;

public class PreconditionNotMetException extends RuntimeException {

    // 기본 생성자
    public PreconditionNotMetException() {
        super();
    }

    // 메시지만 받는 생성자
    public PreconditionNotMetException(String message) {
        super(message);
    }

    // 메시지와 원인 예외를 함께 받는 생성자
    public PreconditionNotMetException(String message, Throwable cause) {
        super(message, cause);
    }

    // 원인 예외만 받는 생성자
    public PreconditionNotMetException(Throwable cause) {
        super(cause);
    }

    /**
     * 선행 트랜잭션 미존재 시 사용할 편의 생성자
     * 예: new PreconditionNotMetException(110L, "502529318");
     * 출력: 선행 트랜잭션(cTransTy: 110)이 존재하지 않습니다. (cOrderId: 502529318)
     */
    public PreconditionNotMetException(Long targetTransTy, String orderId) {
        super(String.format("선행 트랜잭션(cTransTy: %s)이 존재하지 않습니다. (cOrderId: %s)", targetTransTy, orderId));
    }
}