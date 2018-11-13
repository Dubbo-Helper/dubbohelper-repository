package com.dubbohelper.common.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 分页请求PageQueryParamDTO
 *
 * @author lijinbo
 * @since 1.0.0
 */
@Getter
@Setter
@ToString
public class PageQueryParamDTO extends RequestDTO<PageQueryParamDTO> {

    private Integer pageSize;

    private Integer pageIndex;

    public PageQueryParamDTO() {
        this.pageSize = 1;
        this.pageIndex = 20;
    }
}
