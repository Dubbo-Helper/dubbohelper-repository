package com.dubbohelper.common.dto.result;

import com.dubbohelper.common.enums.ResultCodeEnum;
import lombok.ToString;

/**
 * 通用result
 *
 * @param <T>
 * @author lijinbo
 * @since 1.0.0
 */
@ToString
public class ItemResultDTO<T> extends ResultDTO {

    private T item;

    public ItemResultDTO() {
    }

    public ItemResultDTO(boolean success, String code, String description) {
        super(success, code, description);
    }

    public ItemResultDTO(boolean success, ResultCodeEnum resultCode, String description) {
        super(success, resultCode, description);
    }

    public T getItem() {
        return this.item;
    }

    public void setItem(T item) {
        this.item = item;
    }
}
