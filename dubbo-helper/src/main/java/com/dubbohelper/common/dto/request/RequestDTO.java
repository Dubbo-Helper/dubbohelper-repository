package com.dubbohelper.common.dto.request;

import lombok.ToString;

/**
 * 通用request
 *
 * @param <T>
 * @author lijinbo
 * @since 1.0.0
 */
@ToString
public class RequestDTO<T> extends BaseRequest {

    /**
     * 请求数据，可为基本类型（包装类），可以为其它可序列化对象
     */
    private T data;

    public RequestDTO(){

    }

    public RequestDTO(T data){
        this.data = data;
    }

    public static <T> RequestDTO<T> create() {
        return new RequestDTO<T>();
    }

    public RequestDTO<T> sid(String sid){
        this.setSid(sid);
        return this;
    }

    public RequestDTO<T> data(T data){
        this.data = data;
        return this;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
