package com.dubbohelper.common.enums;

/**
 * 接口返回code枚举
 *
 * @author lijinbo
 * @since 1.0.0
 */
public enum ResultCodeEnum  {

    /** 成功 */
    SUCCESS(1, "0000", "成功"),
    /** 成功 */
    FAIL(2, "9999", "失败"),
    /** 参数效验错误 */
    PARAM_FAIL(3, "9000", "参数效验错误");

    private Integer value;
    private String code;
    private String desc;

    ResultCodeEnum(Integer value, String code, String desc) {
        this.value = value;
        this.code = code;
        this.desc = desc;
    }

    public Integer getValue() {
        return this.value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
