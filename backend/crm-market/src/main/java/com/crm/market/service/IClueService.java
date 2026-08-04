package com.crm.market.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.crm.market.entity.Clue;
import com.crm.market.vo.CluePageDTO;

/**
 * 线索服务接口
 *
 * @author CRM
 */
public interface IClueService extends IService<Clue> {

    /**
     * 分页查询线索
     *
     * @param dto 分页查询条件
     * @return 分页结果
     */
    IPage<Clue> page(CluePageDTO dto);

    /**
     * 新增线索
     *
     * @param clue 线索信息
     * @return 是否成功
     */
    boolean addClue(Clue clue);

    /**
     * 修改线索
     *
     * @param clue 线索信息
     * @return 是否成功
     */
    boolean updateClue(Clue clue);

    /**
     * 删除线索
     *
     * @param id 线索ID
     * @return 是否成功
     */
    boolean deleteClue(Long id);

    /**
     * 分配线索给销售
     *
     * @param clueId 线索ID
     * @param userId 销售用户ID
     * @return 是否成功
     */
    boolean assignClue(Long clueId, Long userId);

    /**
     * 抢单
     *
     * @param clueId 线索ID
     * @param userId 当前用户ID
     * @return 是否成功
     */
    boolean claimClue(Long clueId, Long userId);

    /**
     * 转化为客户
     *
     * @param clueId     线索ID
     * @param customerId 转化后的客户ID
     * @return 是否成功
     */
    boolean convertClue(Long clueId, Long customerId);

    /**
     * 退回线索（重置为待分配状态，清空负责人）
     *
     * @param clueId 线索ID
     * @return 是否成功
     */
    boolean returnClue(Long clueId);
}
