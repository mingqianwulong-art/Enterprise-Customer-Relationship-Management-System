package com.crm.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 修改密码请求DTO
 *
 * @author CRM
 */
@Data
public class ChangePasswordDTO {

    /** 旧密码 */
    @NotBlank(message = "原密码不能为空")
    private String oldPassword;

    /** 新密码（8-20位，必须包含大小写字母、数字和特殊字符） */
    @NotBlank(message = "新密码不能为空")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\w\\s]).{8,20}$",
            message = "密码必须8-20位，且包含大小写字母、数字和特殊字符")
    private String newPassword;
}
