package com.crm.business.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.crm.business.entity.Payment;
import com.crm.business.mapper.PaymentMapper;
import com.crm.system.service.ISysMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * 回款提醒定时任务
 * 每天早上9点扫描3天内即将到期的待回款记录，推送提醒给负责人
 *
 * @author CRM
 */
@Component
public class PaymentRemindTask {

    private static final Logger log = LoggerFactory.getLogger(PaymentRemindTask.class);

    /** 提前提醒天数 */
    private static final int REMIND_DAYS_BEFORE = 3;

    @Autowired
    private PaymentMapper paymentMapper;

    @Autowired
    private ISysMessageService messageService;

    /**
     * 每天早上9点推送回款提醒
     */
    @Scheduled(cron = "0 0 9 * * ?")
    public void sendPaymentRemind() {
        log.info("[回款提醒] 开始扫描{}天内到期的待回款记录...", REMIND_DAYS_BEFORE);
        try {
            LocalDate today = LocalDate.now();
            LocalDate deadline = today.plusDays(REMIND_DAYS_BEFORE);

            // 查询待回款（status=0）且计划日期在今天到3天内的记录
            LambdaQueryWrapper<Payment> wrapper = new LambdaQueryWrapper<Payment>()
                    .eq(Payment::getStatus, 0)
                    .isNotNull(Payment::getPlanDate)
                    .ge(Payment::getPlanDate, today)
                    .le(Payment::getPlanDate, deadline);
            List<Payment> pendingList = paymentMapper.selectList(wrapper);

            if (pendingList.isEmpty()) {
                log.info("[回款提醒] 近{}天无待回款记录", REMIND_DAYS_BEFORE);
                return;
            }

            int count = 0;
            for (Payment payment : pendingList) {
                String content = String.format(
                        "客户【%s】的回款计划【%s】将于%s到期，计划金额：%s元，请及时跟进回款。",
                        payment.getCustomerName(),
                        payment.getPaymentNo(),
                        payment.getPlanDate(),
                        payment.getPlanAmount());
                messageService.sendMessage(
                        payment.getOwnerId(),
                        "回款提醒：" + payment.getPaymentNo(),
                        content,
                        3,
                        payment.getId(),
                        "payment");
                count++;
            }
            log.info("[回款提醒] 共推送{}条回款提醒", count);
        } catch (Exception e) {
            log.error("[回款提醒] 执行失败", e);
        }
    }
}
