package com.crm.customer.vo;

import com.crm.customer.entity.Contact;
import com.crm.customer.entity.Customer;
import com.crm.customer.entity.FollowRecord;
import com.crm.customer.entity.Tag;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 客户详情VO（含联系人、标签、跟进记录）
 *
 * @author CRM
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CustomerVO extends Customer {

    /** 负责人姓名 */
    private String ownerName;

    /** 联系人列表 */
    private List<Contact> contacts;

    /** 标签列表 */
    private List<Tag> tags;

    /** 跟进记录列表（最近5条） */
    private List<FollowRecord> followRecords;
}
