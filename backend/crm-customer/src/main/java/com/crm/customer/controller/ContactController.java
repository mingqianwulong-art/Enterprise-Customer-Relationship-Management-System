package com.crm.customer.controller;

import com.crm.common.api.R;
import com.crm.common.constant.Perms;
import com.crm.customer.entity.Contact;
import com.crm.customer.service.IContactService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 联系人控制器
 *
 * @author CRM
 */
@Tag(name = "联系人管理")
@RestController
@RequestMapping("/customer/contact")
public class ContactController {

    @Autowired
    private IContactService contactService;

    /**
     * 查某客户的联系人列表
     */
    @Operation(summary = "查询客户联系人列表")
    @PreAuthorize("hasAuthority('" + Perms.CUSTOMER_CONTACT_LIST + "')")
    @GetMapping("/list/{customerId}")
    public R listByCustomerId(@PathVariable Long customerId) {
        return R.ok(contactService.listByCustomerId(customerId));
    }

    /**
     * 新增联系人（校验 customerId 必填）
     */
    @Operation(summary = "新增联系人")
    @PreAuthorize("hasAuthority('" + Perms.CUSTOMER_CONTACT_ADD + "')")
    @PostMapping
    public R add(@RequestBody Contact contact) {
        return contactService.addContact(contact) ? R.ok("新增联系人成功") : R.fail("新增联系人失败");
    }

    /**
     * 修改联系人
     */
    @Operation(summary = "修改联系人")
    @PreAuthorize("hasAuthority('" + Perms.CUSTOMER_CONTACT_EDIT + "')")
    @PutMapping
    public R update(@RequestBody Contact contact) {
        return contactService.updateContact(contact) ? R.ok("修改联系人成功") : R.fail("修改联系人失败");
    }

    /**
     * 删除联系人
     */
    @Operation(summary = "删除联系人")
    @PreAuthorize("hasAuthority('" + Perms.CUSTOMER_CONTACT_DELETE + "')")
    @DeleteMapping("/{id}")
    public R delete(@PathVariable Long id) {
        return contactService.deleteContact(id) ? R.ok("删除联系人成功") : R.fail("删除联系人失败");
    }
}
