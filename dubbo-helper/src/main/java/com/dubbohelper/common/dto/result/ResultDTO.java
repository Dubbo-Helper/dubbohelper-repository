package com.dubbohelper.common.dto.result;

import com.dubbohelper.common.enums.ResultCodeEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 基础result
 *
 * @author lijinbo
 * @since 1.0.0
 */
@ToString
public class ResultDTO extends BaseResult {

    private ResultDTO.BizResultDTO bizResultDTO;

    public ResultDTO() {
        this.bizResultDTO = new ResultDTO.BizResultDTO();
    }

    public ResultDTO(boolean success, String code, String description) {
        this.bizResultDTO = new ResultDTO.BizResultDTO();
        super.setSuccess(success);
        super.setCode(code);
        super.setDescription(description);

        ResultDTO.BizResultDTO bizResultDTO = this.createBizResult();
        bizResultDTO.setCode(code);
        bizResultDTO.setSuccess(success);
        bizResultDTO.setDescription(description);
    }

    public ResultDTO(boolean success, ResultCodeEnum resultCode, String description) {
        this(success, resultCode.getCode(), description);
    }

    public ResultDTO(String sid, boolean success, String code, String description) {
        this(success, code, description);
        super.setSid(sid);
    }

    public ResultDTO.BizResultDTO createBizResult() {
        if (this.bizResultDTO == null) {
            this.bizResultDTO = new ResultDTO.BizResultDTO();
        }

        return this.bizResultDTO;
    }

    public ResultDTO.BizResultDTO getBizResultDTO() {
        return this.bizResultDTO;
    }

    @Getter
    @Setter
    @ToString
    public final class BizResultDTO implements Serializable {

        private Boolean success;

        private String code;

        private String description;

        private BizResultDTO() {
            this.success = Boolean.TRUE;
        }

        public boolean isSuccess() {
            return this.success;
        }
    }
}
