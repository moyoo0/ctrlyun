package com.wu.service.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "group_mem")
public class GroupMem {
    private static final long serialVersionUID = 1L;

    @TableId(value = "record")
    private int record;

    @ApiModelProperty(value = "组id")
    @TableField(value="group_id")
    private String groupId;

    @ApiModelProperty(value = "组员id")
    @TableField(value="mem_id")
    private String memId;
}
