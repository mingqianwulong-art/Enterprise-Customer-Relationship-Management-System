package com.crm.business.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.crm.business.entity.Payment;
import com.crm.business.vo.PaymentPageDTO;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 回款服务接口
 *
 * @author CRM
 */
public interface IPaymentService extends IService<Payment> {

    /**
     * 分页查询回款
     *
     * @param dto 分页查询条件
     * @return 分页结果
     */
    IPage<Payment> page(PaymentPageDTO dto);

    /**
     * 回款详情
     *
     * @param id 回款ID
     * @return 回款信息
     */
    Payment getById(Long id);

    /**
     * 新增回款记录
     *
     * @param payment 回款信息
     * @return 是否成功
     */
    boolean addPayment(Payment payment);

    /**
     * 修改回款记录
     *
     * @param payment 回款信息
     * @return 是否成功
     */
    boolean updatePayment(Payment payment);

    /**
     * 删除回款（逻辑删除）
     *
     * @param id 回款ID
     * @return 是否成功
     */
    boolean deletePayment(Long id);

    /**
     * 确认回款
     *
     * @param id           回款ID
     * @param actualAmount 实际回款金额
     * @param actualDate   实际回款日期
     * @return 是否成功
     */
    boolean confirmPayment(Long id, BigDecimal actualAmount, LocalDate actualDate);
}
