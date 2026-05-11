package kr.co.aim.api.dto;

public class ResponseMessage<T> {
    private String result;
    private String message;
    private T data;

    public ResponseMessage(String result, String message, T data) {
        this.result = result;
        this.message = message;
        this.data = data;
    }

    // Getter, Setter 생략 (Lombok @Data 사용 권장)
    public String getResult() { return result; }
    public String getMessage() { return message; }
    public T getData() { return data; }
}