package com.dubbohelper.common.dto.result;

import com.dubbohelper.common.enums.ResultCodeEnum;
import lombok.ToString;

import java.util.List;

/**
 * 列表Result
 *
 * @param <T>
 * @author lijinbo
 * @since 1.0.0
 */
@ToString
public class ListResultDTO<T> extends ResultDTO {

    private List<T> list;

    public ListResultDTO() {
    }

    public ListResultDTO(boolean success, String code, String description) {
        super(success, code, description);
    }

    public ListResultDTO(boolean success, ResultCodeEnum resultCode, String description) {
        super(success, resultCode, description);
    }

    public List<T> getList() {
        return this.list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
