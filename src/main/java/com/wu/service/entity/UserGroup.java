package com.wu.service.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "user_group")
public class UserGroup {
    private static final long serialVersionUID = 1L;

    @TableId(value = "group_id")
    private String groupId;

    @ApiModelProperty(value = "管理员id")
    private String masterId;

}
