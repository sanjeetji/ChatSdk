package com.sanjeet.chat.sdk.utils;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL) // Exclude null fields from JSON
public class ApiResponse<T> {
    private int flag; // 1 for success, 0 for failure
    private String message;
    private T data; // Generic type to handle any kind of response
    private List<T> listData; // Generic type to handle any kind of response

    // Static factory methods
    public static <T> ApiResponse<T> success(int flag, String message, T data) {
        return new ApiResponse<>(flag, message, data);
    }

    public static <T> ApiResponse<T> success(int flag, String message, List<T> data) {
        return new ApiResponse<>(flag, message, data);
    }

    public static <T> ApiResponse<T> success(int flag, String message) {
        return new ApiResponse<>(flag, message);
    }

    public static <T> ApiResponse<T> error(int flag, String message) {
        return new ApiResponse<>(flag, message); // Pass null explicitly
    }

    // Constructors
    public ApiResponse(int flag, String message, T data) {
        this.flag = flag;
        this.message = message;
        this.data = data;
    }

    public ApiResponse(int flag, String message, List<T> listData) {
        this.flag = flag;
        this.message = message;
        this.listData = listData;
    }

    public ApiResponse(int flag, String message) {
        this.flag = flag;
        this.message = message;
    }



    // Getters and Setters
    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public List<T> getListData() {
        return listData;
    }

    public void setListData(List<T> listData) {
        this.listData = listData;
    }
}
