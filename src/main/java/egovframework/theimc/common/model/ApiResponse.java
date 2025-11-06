package egovframework.theimc.common.model;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {

	private int code;
	private String message;
	private T data;

	public ApiResponse(HttpStatus status, String message) {
    this.code = status.value();
    this.message = message;
    this.data = null;
	}	

	public static <T> ApiResponse<T> success(String message) {
		return ApiResponse.<T>builder()
				.code(HttpStatus.OK.value())
				.message(message)
				.data(null)
				.build();
	}

	public static <T> ApiResponse<T> success(String message, T data) {
		return ApiResponse.<T>builder()
				.code(HttpStatus.OK.value())
				.message(message)
				.data(data)
				.build();
	}

	public static <T> ApiResponse<T> success(T data) {
		return ApiResponse.<T>builder()
				.code(HttpStatus.OK.value())
				.message("요청이 성공적으로 처리되었습니다.")
				.data(data)
				.build();
	}

	public static <T> ApiResponse<T> error(HttpStatus status, String message) {
		return ApiResponse.<T>builder()
				.code(status.value())
				.message(message)
				.data(null)
				.build();
	}

}
