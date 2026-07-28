package com.crm.customer.controller;

import com.crm.common.api.R;
import com.crm.customer.entity.Tag;
import com.crm.customer.service.ITagService;
import com.crm.system.annotation.Log;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 标签控制器
 *
 * @author CRM
 */
@io.swagger.v3.oas.annotations.tags.Tag(name = "标签管理")
@RestController
@RequestMapping("/customer/tag")
public class TagController {

    @Autowired
    private ITagService tagService;

    /**
     * 查全部标签
     */
    @Operation(summary = "查询全部标签")
    @GetMapping("/list")
    public R listAll() {
        return R.ok(tagService.listAll());
    }

    /**
     * 新增标签
     */
    @Operation(summary = "新增标签")
    @Log("新增标签")
    @PostMapping
    public R add(@RequestBody Tag tag) {
        return tagService.addTag(tag) ? R.ok("新增标签成功") : R.fail("新增标签失败");
    }

    /**
     * 修改标签
     */
    @Operation(summary = "修改标签")
    @PutMapping
    public R update(@RequestBody Tag tag) {
        return tagService.updateTag(tag) ? R.ok("修改标签成功") : R.fail("修改标签失败");
    }

    /**
     * 删除标签
     */
    @Operation(summary = "删除标签")
    @DeleteMapping("/{id}")
    public R delete(@PathVariable Long id) {
        return tagService.deleteTag(id) ? R.ok("删除标签成功") : R.fail("删除标签失败");
    }
}
