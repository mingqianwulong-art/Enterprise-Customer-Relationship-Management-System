package com.crm.business.task;

import com.crm.business.entity.Opportunity;
import com.crm.business.service.IOpportunityService;
import com.crm.system.service.ISysMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 商机停滞预警定时任务
 * 每天凌晨2:30扫描超过15天未推进阶段的进行中商机，推送通知给负责人
 *
 * @author CRM
 */
@Component
public class OpportunityStagnantTask {

    private static final Logger log = LoggerFactory.getLogger(OpportunityStagnantTask.class);

    /** 超过该天数未推进阶段的商机视为停滞 */
    private static final int STAGNANT_DAYS = 15;

    /** 商机阶段名称 */
    private static final String[] STAGE_NAMES = {
            null, "需求确认", "方案报价", "商务谈判", "合同签订", "已赢单", "已输单"
    };

    @Autowired
    private IOpportunityService opportunityService;

    @Autowired
    private ISysMessageService messageService;

    /**
     * 每天凌晨2:30执行商机停滞扫描，推送消息通知
     */
    @Scheduled(cron = "0 30 2 * * ?")
    public void scanStagnant() {
        log.info("[商机停滞预警] 开始扫描{}天未推进的商机...", STAGNANT_DAYS);
        try {
            List<Opportunity> stagnant = opportunityService.listStagnant(STAGNANT_DAYS);
            if (stagnant.isEmpty()) {
                log.info("[商机停滞预警] 未发现停滞商机");
                return;
            }
            log.warn("[商机停滞预警] 发现{}个停滞商机，开始推送通知...", stagnant.size());
            int pushCount = 0;
            for (Opportunity opp : stagnant) {
                String stageName = stageName(opp.getStage());
                String content = String.format(
                        "商机【%s】已超过%d天未推进阶段（当前阶段：%s），请尽快跟进！",
                        opp.getOppName(), STAGNANT_DAYS, stageName);
                messageService.sendMessage(
                        opp.getOwnerId(),
                        "商机停滞预警：" + opp.getOppName(),
                        content,
                        2,
                        opp.getId(),
                        "opportunity");
                pushCount++;
            }
            log.info("[商机停滞预警] 共推送{}条预警消息", pushCount);
        } catch (Exception e) {
            log.error("[商机停滞预警] 执行失败", e);
        }
    }

    private String stageName(Integer stage) {
        if (stage == null || stage < 1 || stage >= STAGE_NAMES.length) {
            return "未知";
        }
        return STAGE_NAMES[stage];
    }
}
