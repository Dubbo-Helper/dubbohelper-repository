package com.dubbohelper.common.dto.result.page;

import com.dubbohelper.common.dto.result.ResultDTO;
import com.dubbohelper.common.enums.ResultCodeEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 分页结果PaginationResultDTO
 *
 * @param <T>
 * @author lijinbo
 * @since 1.0.0
 */
@Getter
@Setter
public class PaginationResultDTO<T> extends ResultDTO {

    private PaginationDTO paginationDTO;
    private List<T> results;

    public PaginationResultDTO() {
    }

    public PaginationResultDTO(boolean success, String code, String description) {
        super(success, code, description);
    }

    public PaginationResultDTO(boolean success, ResultCodeEnum resultCode, String description) {
        super(success, resultCode, description);
    }

    public PaginationResultDTO(boolean success, ResultCodeEnum resultCode) {
        super(success, resultCode, "");
    }
}
