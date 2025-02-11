package com.sanjeet.chat.sdk.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class HandleApiResponse {

    public ResponseEntity<ApiResponse<String>> handleApiFailedResponse(HttpStatus code,String message){
        ApiResponse<String> apiResponse = ApiResponse.error(ApiResponseFlag.FAILED.getFlag(),message);
        return ResponseEntity.status(code).body(apiResponse);
    }

    public <T> ResponseEntity<ApiResponse<T>> handleApiSuccessResponse(HttpStatus code, String message, T payload) {
        ApiResponse<T> apiResponse = ApiResponse.success(ApiResponseFlag.SUCCESS.getFlag(), message, payload);
        return ResponseEntity.status(code).body(apiResponse);
    }
}
