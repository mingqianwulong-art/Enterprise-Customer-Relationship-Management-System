package com.crm.market.service;

import com.crm.market.entity.Clue;

/**
 * 线索自动分配规则引擎
 * <p>
 * 规则策略（按优先级）：
 * 1. 区域+行业精确匹配：找到同时负责该区域和行业的销售
 * 2. 区域匹配：找到负责该区域的销售
 * 3. 负载均衡：分配给当前未完成线索最少的活跃销售
 *
 * @author CRM
 */
public interface ClueAssignRuleService {

    /**
     * 自动分配线索
     * <p>
     * 根据线索的区域、行业信息，按规则匹配最佳销售并分配
     *
     * @param clueId 线索ID
     * @return 分配的销售用户ID，null 表示无可用销售
     */
    Long autoAssign(Long clueId);

    /**
     * 自动分配线索并发送通知（异步执行）
     *
     * @param clue 线索信息
     */
    void autoAssignAndNotify(Clue clue);

    /**
     * 批量自动分配所有待分配线索
     *
     * @return 成功分配的线索数量
     */
    int batchAutoAssign();
}
