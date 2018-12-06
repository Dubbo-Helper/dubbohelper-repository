package com.dubbohelper.admin.common.enums;

public enum FileTypeEnum {
    /**
     * markdown文件类型
     */
    API_DOC("/apidoc/", ".md","apidoc文档"),
    /**
     * 文本文件类型
     */
    APPLICATIONS("/applications/", ".txt","jar包信息记录文档");

    private String path;

    private String suffix;

    private String desc;

    FileTypeEnum(String path, String suffix, String desc) {
        this.path = path;
        this.suffix = suffix;
        this.desc = desc;
    }

    public String getSuffix() {
        return suffix;
    }

    public String getDesc() {
        return desc;
    }

    public String getPath() {
        return path;
    }
}
