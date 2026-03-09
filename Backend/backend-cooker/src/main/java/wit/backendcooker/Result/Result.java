package wit.backendcooker.Result;

/*
 * @author ：jee
 * @date ：2026/3/816:00
 * @version: 1.0
 --------------------------
 */

import lombok.Data;

@Data
public class Result<T> {


    private Integer code;
    private String message;
    private T data;
    public Result() {
    }
    public Result(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
    public Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", data);
    }
    public static <T> Result<T> error(String message) {
        return new Result<>(500, message);
    }
    public static <T> Result<T> done(Integer code, String message) {
        return new Result<>(code, message);
    }
}
