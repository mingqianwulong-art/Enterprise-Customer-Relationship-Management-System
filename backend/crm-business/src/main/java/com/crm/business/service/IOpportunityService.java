package com.crm.business.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.crm.business.entity.Opportunity;
import com.crm.business.vo.FunnelVO;
import com.crm.business.vo.OpportunityPageDTO;

import java.util.List;

/**
 * 商机服务接口
 *
 * @author CRM
 */
public interface IOpportunityService extends IService<Opportunity> {

    /**
     * 分页查询商机
     *
     * @param dto 分页查询条件
     * @return 分页结果
     */
    IPage<Opportunity> page(OpportunityPageDTO dto);

    /**
     * 商机详情
     *
     * @param id 商机ID
     * @return 商机信息
     */
    Opportunity getById(Long id);

    /**
     * 新增商机
     *
     * @param opp 商机信息
     * @return 是否成功
     */
    boolean addOpportunity(Opportunity opp);

    /**
     * 修改商机
     *
     * @param opp 商机信息
     * @return 是否成功
     */
    boolean updateOpportunity(Opportunity opp);

    /**
     * 删除商机（逻辑删除）
     *
     * @param id 商机ID
     * @return 是否成功
     */
    boolean deleteOpportunity(Long id);

    /**
     * 修改商机阶段
     *
     * @param id    商机ID
     * @param stage 商机阶段
     * @return 是否成功
     */
    boolean changeStage(Long id, Integer stage);

    /**
     * 销售漏斗数据（按阶段统计数量和金额）
     *
     * @return 漏斗统计数据
     */
    List<FunnelVO> getFunnelData();

    /**
     * 查询停滞预警商机（超过指定天数未推进阶段的进行中商机）
     *
     * @param days 停滞天数阈值
     * @return 停滞商机列表
     */
    List<Opportunity> listStagnant(int days);
}
