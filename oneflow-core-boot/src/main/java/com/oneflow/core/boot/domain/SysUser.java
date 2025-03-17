package com.oneflow.core.boot.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

@Data
public class SysUser {

    /**
     * 用户id
     */
    @Schema(accessMode = READ_ONLY)
    private Long userId;
    /**
     * 工号
     */
    @Schema(accessMode = READ_ONLY)
    private String userName;
    /**
     * 昵称
     */
    @Schema(accessMode = READ_ONLY)
    private String nickName;
    /**
     * 部门id
     */
    @Schema(accessMode = READ_ONLY)
    private String deptId;
    /**
     * 角色id（多个）
     */
    @Schema(accessMode = READ_ONLY)
    private String roleId;
    /**
     * 角色名（多个）
     */
    @Schema(accessMode = READ_ONLY)
    private String roleName;

}
