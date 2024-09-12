package com.zdouble.types.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Response<T> implements Serializable {
    // 响应状态码 0000:成功 0001:失败
    private String code;
    // 响应信息
    private String info;
    // 响应数据
    private T data;

    public static <T> Response<T> success(T data) {
        return Response.<T>builder().code("0000").info("success").data(data).build();
    }

    public static <T> Response<T> fail() {
        return Response.<T>builder().code("0001").info("fail").data(null).build();
    }

    public static <T> Response<T> fail(String info) {
        return Response.<T>builder().code("0001").info(info).data(null).build();
    }
    public static <T> Response<T> fail(String info, T data) {
        return Response.<T>builder().code("0001").info(info).data(data).build();
    }
}
