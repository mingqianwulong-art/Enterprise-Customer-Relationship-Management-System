package com.crm.market.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 外部渠道线索归集 DTO
 * <p>
 * 供抖音/微信公域/官网表单等外部渠道推送线索数据
 *
 * @author CRM
 */
@Data
public class ExternalClueDTO {

    /** 线索名称（必填） */
    @NotBlank(message = "线索名称不能为空")
    private String clueName;

    /** 公司名称 */
    private String company;

    /** 联系电话（必填） */
    @NotBlank(message = "联系电话不能为空")
    private String phone;

    /** 邮箱 */
    private String email;

    /** 线索来源（如：抖音、微信、官网表单、展会） */
    @NotBlank(message = "线索来源不能为空")
    private String source;

    /** 渠道标识（外部渠道的唯一标识，用于统计） */
    private String channelCode;

    /** 所属行业 */
    private String industry;

    /** 所在区域 */
    private String region;

    /** 线索等级（1低 2中 3高，默认2） */
    private Integer level;

    /** 需求描述 */
    private String description;

    /** 外部线索唯一标识（用于去重，如表单ID） */
    private String externalId;
}
