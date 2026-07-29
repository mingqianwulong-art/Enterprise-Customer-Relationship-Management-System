package com.crm.business.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.crm.business.entity.Contract;
import com.crm.business.vo.ContractPageDTO;

/**
 * 合同服务接口
 *
 * @author CRM
 */
public interface IContractService extends IService<Contract> {

    /**
     * 分页查询合同
     *
     * @param dto 分页查询条件
     * @return 分页结果
     */
    IPage<Contract> page(ContractPageDTO dto);

    /**
     * 合同详情
     *
     * @param id 合同ID
     * @return 合同信息
     */
    Contract getById(Long id);

    /**
     * 新增合同（自动生成合同编号 HT-yyyyMMdd-xxx）
     *
     * @param contract 合同信息
     * @return 是否成功
     */
    boolean addContract(Contract contract);

    /**
     * 修改合同
     *
     * @param contract 合同信息
     * @return 是否成功
     */
    boolean updateContract(Contract contract);

    /**
     * 删除合同（逻辑删除）
     *
     * @param id 合同ID
     * @return 是否成功
     */
    boolean deleteContract(Long id);

    /**
     * 审批合同
     *
     * @param id          合同ID
     * @param approverId  审批人ID
     * @return 是否成功
     */
    boolean approve(Long id, Long approverId);
}
