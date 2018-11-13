package com.dubbohelper.common.dto.result.page;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 分页结果PaginationDTO
 *
 * @author lijinbo
 * @since 1.0.0
 */
@Getter
@Setter
@ToString
public class PaginationDTO implements Serializable {

    private Integer pageIndex;
    private Integer pageSize;
    private Integer totalRecord;
    private Integer totalPage;

    public PaginationDTO() {
    }

    public PaginationDTO(Integer pageIndex, Integer pageSize, Integer totalRecord, Integer totalPage) {
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        this.totalRecord = totalRecord;
        this.totalPage = totalPage;
    }
}
