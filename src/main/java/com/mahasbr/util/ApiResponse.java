package com.mahasbr.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ApiResponse<T> {
	private boolean success;
	private String message;
	private T data;
	
	public static <T> ApiResponse<T> ok(T data, String message) {
		return new ApiResponse<>(true, message, data);
	}

	public static <T> ApiResponse<T> fail(String message) {
		return new ApiResponse<>(false, message, null);
	}
	
	 public static <T> ApiResponse<T> success(T data) {
	        return new ApiResponse<>(true, "SUCCESS", data);
	    }

}
