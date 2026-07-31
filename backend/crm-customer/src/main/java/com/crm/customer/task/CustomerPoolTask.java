package com.crm.customer.task;

import com.crm.customer.service.ICustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 客户公海池定时任务
 * 每天凌晨2点扫描超过30天未跟进的客户，自动回收至公海池
 *
 * @author CRM
 */
@Component
public class CustomerPoolTask {

    private static final Logger log = LoggerFactory.getLogger(CustomerPoolTask.class);

    /** 超过该天数未跟进的客户自动回收至公海 */
    private static final int RECYCLE_DAYS = 30;

    @Autowired
    private ICustomerService customerService;

    /**
     * 每天凌晨2点执行公海池自动回收
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void autoRecycleToPool() {
        log.info("[公海池回收] 开始扫描{}天未跟进客户...", RECYCLE_DAYS);
        try {
            int count = customerService.autoRecycleToPool(RECYCLE_DAYS);
            log.info("[公海池回收] 扫描完成，共回收{}个客户至公海池", count);
        } catch (Exception e) {
            log.error("[公海池回收] 执行失败", e);
        }
    }
}
