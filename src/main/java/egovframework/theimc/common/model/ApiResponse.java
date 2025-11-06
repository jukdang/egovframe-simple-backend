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

}
