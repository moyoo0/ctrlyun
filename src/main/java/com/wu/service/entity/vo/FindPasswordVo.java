package com.wu.service.entity.vo;

import io.swagger.annotations.ApiModelProperty;

import lombok.Data;
@Data
public class FindPasswordVo {
    @ApiModelProperty(value = "账号")
    private String account;
    @ApiModelProperty(value = "密码")
    private String password;
    @ApiModelProperty(value = "验证码")
    private String code;

}
