package com.crm.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crm.business.entity.Payment;
import com.crm.business.mapper.PaymentMapper;
import com.crm.business.service.IPaymentService;
import com.crm.business.vo.PaymentPageDTO;
import com.crm.common.exception.BusinessException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 回款服务实现
 *
 * @author CRM
 */
@Service
public class PaymentServiceImpl extends ServiceImpl<PaymentMapper, Payment> implements IPaymentService {

    /**
     * 分页查询回款
     */
    @Override
    public IPage<Payment> page(PaymentPageDTO dto) {
        Page<Payment> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        LambdaQueryWrapper<Payment> wrapper = new LambdaQueryWrapper<Payment>()
                .eq(dto.getContractId() != null,
                        Payment::getContractId, dto.getContractId())
                .eq(dto.getCustomerId() != null,
                        Payment::getCustomerId, dto.getCustomerId())
                .eq(dto.getStatus() != null,
                        Payment::getStatus, dto.getStatus())
                .orderByDesc(Payment::getCreateTime);
        return baseMapper.selectPage(page, wrapper);
    }

    /**
     * 回款详情
     */
    @Override
    public Payment getById(Long id) {
        Payment payment = baseMapper.selectById(id);
        if (payment == null) {
            throw new BusinessException("回款记录不存在");
        }
        return payment;
    }

    /**
     * 新增回款记录
     */
    @Override
    public boolean addPayment(Payment payment) {
        // 默认待回款
        if (payment.getStatus() == null) {
            payment.setStatus(0);
        }
        return baseMapper.insert(payment) > 0;
    }

    /**
     * 修改回款记录
     */
    @Override
    public boolean updatePayment(Payment payment) {
        if (payment.getId() == null) {
            throw new BusinessException("回款ID不能为空");
        }
        return baseMapper.updateById(payment) > 0;
    }

    /**
     * 删除回款（逻辑删除）
     */
    @Override
    public boolean deletePayment(Long id) {
        return baseMapper.deleteById(id) > 0;
    }

    /**
     * 确认回款
     */
    @Override
    public boolean confirmPayment(Long id, BigDecimal actualAmount, LocalDate actualDate) {
        Payment payment = baseMapper.selectById(id);
        if (payment == null) {
            throw new BusinessException("回款记录不存在");
        }
        Payment update = new Payment();
        update.setId(id);
        update.setActualAmount(actualAmount);
        update.setActualDate(actualDate);
        // 实际金额 >= 计划金额视为已回款，否则部分回款
        BigDecimal planAmount = payment.getPlanAmount();
        if (planAmount != null && actualAmount != null && actualAmount.compareTo(planAmount) >= 0) {
            update.setStatus(2);
        } else {
            update.setStatus(1);
        }
        return baseMapper.updateById(update) > 0;
    }
}
