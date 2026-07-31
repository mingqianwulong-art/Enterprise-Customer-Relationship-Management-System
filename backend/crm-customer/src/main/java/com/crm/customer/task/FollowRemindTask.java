package com.crm.customer.task;

import com.crm.customer.entity.Customer;
import com.crm.customer.entity.FollowRecord;
import com.crm.customer.mapper.CustomerMapper;
import com.crm.customer.service.IFollowRecordService;
import com.crm.system.service.ISysMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 跟进提醒定时任务
 * 每天早上8点扫描今日待跟进记录，推送提醒消息
 *
 * @author CRM
 */
@Component
public class FollowRemindTask {

    private static final Logger log = LoggerFactory.getLogger(FollowRemindTask.class);

    /** 跟进方式名称 */
    private static final String[] FOLLOW_TYPE_NAMES = {null, "电话", "上门拜访", "微信", "其他"};

    @Autowired
    private IFollowRecordService followRecordService;

    @Autowired
    private ISysMessageService messageService;

    @Autowired
    private CustomerMapper customerMapper;

    /**
     * 每天早上8点推送跟进提醒
     */
    @Scheduled(cron = "0 0 8 * * ?")
    public void sendFollowRemind() {
        log.info("[跟进提醒] 开始扫描今日待跟进记录...");
        try {
            List<FollowRecord> pendingList = followRecordService.listTodayPending();
            if (pendingList.isEmpty()) {
                log.info("[跟进提醒] 今日无待跟进记录");
                return;
            }
            int count = 0;
            for (FollowRecord record : pendingList) {
                Customer customer = customerMapper.selectById(record.getCustomerId());
                String customerName = customer != null ? customer.getName() : "未知客户";
                String followType = followTypeName(record.getFollowType());
                String content = String.format(
                        "客户【%s】今日需跟进，跟进方式：%s",
                        customerName, followType);
                messageService.sendMessage(
                        record.getUserId(),
                        "跟进提醒：" + customerName,
                        content,
                        1,
                        record.getCustomerId(),
                        "customer");
                count++;
            }
            log.info("[跟进提醒] 共推送{}条跟进提醒", count);
        } catch (Exception e) {
            log.error("[跟进提醒] 执行失败", e);
        }
    }

    private String followTypeName(Integer type) {
        if (type == null || type < 1 || type >= FOLLOW_TYPE_NAMES.length) {
            return "其他";
        }
        return FOLLOW_TYPE_NAMES[type];
    }
}
