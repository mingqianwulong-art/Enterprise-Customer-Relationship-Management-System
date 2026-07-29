package com.crm.market.controller;

import com.crm.common.api.R;
import com.crm.market.entity.Knowledge;
import com.crm.market.service.IKnowledgeService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 知识库控制器
 *
 * @author CRM
 */
@io.swagger.v3.oas.annotations.tags.Tag(name = "知识库")
@RestController
@RequestMapping("/market/knowledge")
public class KnowledgeController {

    @Autowired
    private IKnowledgeService knowledgeService;

    /**
     * 分页查询知识库
     */
    @Operation(summary = "分页查询知识库")
    @GetMapping("/page")
    public R page(@RequestParam(defaultValue = "1") Integer pageNum,
                  @RequestParam(defaultValue = "10") Integer pageSize,
                  @RequestParam(required = false) String title,
                  @RequestParam(required = false) String category) {
        return R.ok(knowledgeService.page(pageNum, pageSize, title, category));
    }

    /**
     * 知识详情（浏览次数+1）
     */
    @Operation(summary = "知识详情")
    @GetMapping("/{id}")
    public R getDetail(@PathVariable Long id) {
        return R.ok(knowledgeService.getById(id));
    }

    /**
     * 新增知识
     */
    @Operation(summary = "新增知识")
    @PostMapping
    public R add(@RequestBody Knowledge knowledge) {
        return knowledgeService.addKnowledge(knowledge) ? R.ok("新增知识成功") : R.fail("新增知识失败");
    }

    /**
     * 修改知识
     */
    @Operation(summary = "修改知识")
    @PutMapping
    public R update(@RequestBody Knowledge knowledge) {
        return knowledgeService.updateKnowledge(knowledge) ? R.ok("修改知识成功") : R.fail("修改知识失败");
    }

    /**
     * 删除知识
     */
    @Operation(summary = "删除知识")
    @DeleteMapping("/{id}")
    public R delete(@PathVariable Long id) {
        return knowledgeService.deleteKnowledge(id) ? R.ok("删除知识成功") : R.fail("删除知识失败");
    }
}
