package com.crm.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crm.business.entity.Contract;
import com.crm.business.mapper.ContractMapper;
import com.crm.business.service.IContractService;
import com.crm.business.vo.ContractPageDTO;
import com.crm.common.exception.BusinessException;
import com.crm.system.service.DataPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 合同服务实现
 *
 * @author CRM
 */
@Service
public class ContractServiceImpl extends ServiceImpl<ContractMapper, Contract> implements IContractService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Autowired
    private DataPermissionService dataPermissionService;

    /**
     * 分页查询合同
     */
    @Override
    public IPage<Contract> page(ContractPageDTO dto) {
        Page<Contract> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        LambdaQueryWrapper<Contract> wrapper = new LambdaQueryWrapper<Contract>()
                .like(dto.getContractName() != null && !dto.getContractName().isEmpty(),
                        Contract::getContractName, dto.getContractName())
                .eq(dto.getCustomerId() != null,
                        Contract::getCustomerId, dto.getCustomerId())
                .eq(dto.getStatus() != null,
                        Contract::getStatus, dto.getStatus())
                .orderByDesc(Contract::getCreateTime);
        // 数据权限过滤
        List<Long> visibleOwnerIds = dataPermissionService.getVisibleOwnerIds();
        if (visibleOwnerIds != null) {
            wrapper.in(Contract::getOwnerId, visibleOwnerIds);
        }
        return baseMapper.selectPage(page, wrapper);
    }

    /**
     * 合同详情
     */
    @Override
    public Contract getById(Long id) {
        Contract contract = baseMapper.selectById(id);
        if (contract == null) {
            throw new BusinessException("合同不存在");
        }
        return contract;
    }

    /**
     * 新增合同（自动生成合同编号 HT-yyyyMMdd-序号）
     */
    @Override
    public boolean addContract(Contract contract) {
        validateContractDates(contract);
        // 自动生成合同编号：HT-yyyyMMdd-001
        contract.setContractNo(generateContractNo());
        // 默认待审批
        if (contract.getStatus() == null) {
            contract.setStatus(0);
        }
        return baseMapper.insert(contract) > 0;
    }

    /**
     * 生成合同编号，格式：HT-yyyyMMdd-序号（当天第几份合同）
     */
    private String generateContractNo() {
        String today = LocalDate.now().format(DATE_FORMATTER);
        String prefix = "HT-" + today + "-";
        // 查询当天已有的合同数
        LambdaQueryWrapper<Contract> wrapper = new LambdaQueryWrapper<>();
        wrapper.likeRight(Contract::getContractNo, prefix);
        long count = baseMapper.selectCount(wrapper);
        return prefix + String.format("%03d", count + 1);
    }

    /**
     * 修改合同
     */
    @Override
    public boolean updateContract(Contract contract) {
        if (contract.getId() == null) {
            throw new BusinessException("合同ID不能为空");
        }
        validateContractDates(contract);
        return baseMapper.updateById(contract) > 0;
    }

    /**
     * 校验合同日期逻辑
     */
    private void validateContractDates(Contract contract) {
        if (contract.getStartDate() != null && contract.getSignedDate() != null
                && contract.getStartDate().isBefore(contract.getSignedDate())) {
            throw new BusinessException("开始日期不能早于签订日期");
        }
        if (contract.getEndDate() != null && contract.getStartDate() != null
                && contract.getEndDate().isBefore(contract.getStartDate())) {
            throw new BusinessException("结束日期不能早于开始日期");
        }
    }

    /**
     * 删除合同（逻辑删除）
     */
    @Override
    public boolean deleteContract(Long id) {
        return baseMapper.deleteById(id) > 0;
    }

    /**
     * 审批合同
     */
    @Override
    public boolean approve(Long id, Long approverId) {
        Contract contract = baseMapper.selectById(id);
        if (contract == null) {
            throw new BusinessException("合同不存在");
        }
        if (contract.getStatus() != null && contract.getStatus() != 0) {
            throw new BusinessException("该合同已审批，无法重复审批");
        }
        Contract update = new Contract();
        update.setId(id);
        update.setStatus(1);
        update.setApproverId(approverId);
        update.setApproveTime(LocalDateTime.now());
        return baseMapper.updateById(update) > 0;
    }
}
