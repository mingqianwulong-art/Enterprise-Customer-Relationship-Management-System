package com.crm.business.task;

import com.crm.business.entity.Opportunity;
import com.crm.business.service.IOpportunityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 商机停滞预警定时任务
 * 每天凌晨2:30扫描超过15天未推进阶段的进行中商机
 *
 * @author CRM
 */
@Component
public class OpportunityStagnantTask {

    private static final Logger log = LoggerFactory.getLogger(OpportunityStagnantTask.class);

    /** 超过该天数未推进阶段的商机视为停滞 */
    private static final int STAGNANT_DAYS = 15;

    @Autowired
    private IOpportunityService opportunityService;

    /**
     * 每天凌晨2:30执行商机停滞扫描
     */
    @Scheduled(cron = "0 30 2 * * ?")
    public void scanStagnant() {
        log.info("[商机停滞预警] 开始扫描{}天未推进的商机...", STAGNANT_DAYS);
        try {
            List<Opportunity> stagnant = opportunityService.listStagnant(STAGNANT_DAYS);
            if (stagnant.isEmpty()) {
                log.info("[商机停滞预警] 未发现停滞商机");
            } else {
                log.warn("[商机停滞预警] 发现{}个停滞商机：", stagnant.size());
                for (Opportunity opp : stagnant) {
                    log.warn("[商机停滞预警] 商机ID={} 名称={} 阶段={} 阶段变更时间={} 负责人={}",
                            opp.getId(), opp.getOppName(), opp.getStage(),
                            opp.getStageChangeTime(), opp.getOwnerId());
                }
            }
        } catch (Exception e) {
            log.error("[商机停滞预警] 执行失败", e);
        }
    }
}
