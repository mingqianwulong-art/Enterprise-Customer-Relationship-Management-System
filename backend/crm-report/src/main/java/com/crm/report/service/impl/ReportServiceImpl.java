package com.crm.report.service.impl;

import com.crm.report.dto.ReportQueryDTO;
import com.crm.report.mapper.ReportMapper;
import com.crm.report.service.IReportService;
import com.crm.report.vo.*;
import com.crm.system.service.DataPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 报表服务实现
 */
@Service
public class ReportServiceImpl implements IReportService {

    @Autowired
    private ReportMapper reportMapper;

    @Autowired
    private DataPermissionService dataPermissionService;

    @Override
    public DashboardOverviewVO getOverview() {
        DashboardOverviewVO vo = new DashboardOverviewVO();
        List<Long> ownerIds = dataPermissionService.getVisibleOwnerIds();

        // 客户
        vo.setTotalCustomers(reportMapper.countCustomers(ownerIds));
        vo.setMonthlyNewCustomers(reportMapper.countMonthlyNewCustomers(ownerIds));
        vo.setPoolCustomers(reportMapper.countPoolCustomers(ownerIds));

        // 线索
        Long totalClues = reportMapper.countClues(ownerIds);
        Long convertedClues = reportMapper.countConvertedClues(ownerIds);
        vo.setTotalClues(totalClues);
        vo.setConvertedClues(convertedClues);
        if (totalClues > 0) {
            vo.setClueConversionRate(BigDecimal.valueOf(convertedClues)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalClues), 1, RoundingMode.HALF_UP));
        } else {
            vo.setClueConversionRate(BigDecimal.ZERO);
        }

        // 商机
        Long totalOpps = reportMapper.countOpportunities(ownerIds);
        Long wonOpps = reportMapper.countWonOpportunities(ownerIds);
        vo.setTotalOpportunities(totalOpps);
        vo.setActiveOpportunities(reportMapper.countActiveOpportunities(ownerIds));
        vo.setWonOpportunities(wonOpps);
        if (totalOpps > 0) {
            vo.setWinRate(BigDecimal.valueOf(wonOpps)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalOpps), 1, RoundingMode.HALF_UP));
        } else {
            vo.setWinRate(BigDecimal.ZERO);
        }

        // 合同
        vo.setTotalContracts(reportMapper.countContracts(ownerIds));
        BigDecimal contractAmount = reportMapper.sumContractAmount(ownerIds);
        vo.setTotalContractAmount(contractAmount);

        // 回款
        BigDecimal receivedAmount = reportMapper.sumReceivedAmount(ownerIds);
        vo.setTotalReceivedAmount(receivedAmount);
        if (contractAmount.compareTo(BigDecimal.ZERO) > 0) {
            vo.setPaymentRate(receivedAmount
                    .multiply(BigDecimal.valueOf(100))
                    .divide(contractAmount, 1, RoundingMode.HALF_UP));
        } else {
            vo.setPaymentRate(BigDecimal.ZERO);
        }

        // 工单
        vo.setTotalOrders(reportMapper.countOrders(ownerIds));
        vo.setPendingOrders(reportMapper.countPendingOrders(ownerIds));
        vo.setCompletedOrders(reportMapper.countCompletedOrders(ownerIds));
        vo.setAvgSatisfaction(reportMapper.avgSatisfaction(ownerIds));

        return vo;
    }

    @Override
    public List<TrendVO> getTrend(int months) {
        return reportMapper.getTrend(months, dataPermissionService.getVisibleOwnerIds());
    }

    @Override
    public List<ServiceStatsVO> getOrderStatusStats() {
        return reportMapper.getOrderStatusStats(dataPermissionService.getVisibleOwnerIds());
    }

    @Override
    public List<SalesRankingVO> getSalesRanking() {
        return reportMapper.getSalesRanking(dataPermissionService.getVisibleOwnerIds());
    }

    @Override
    public List<CustomerSourceVO> getCustomerIndustryStats() {
        return reportMapper.getCustomerIndustryStats(dataPermissionService.getVisibleOwnerIds());
    }

    @Override
    public List<CustomerSourceVO> getClueSourceStats() {
        return reportMapper.getClueSourceStats(dataPermissionService.getVisibleOwnerIds());
    }

    @Override
    public List<Map<String, Object>> getCustomCustomerReport(ReportQueryDTO dto) {
        return reportMapper.getCustomCustomerReport(dto, dataPermissionService.getVisibleOwnerIds());
    }

    @Override
    public List<Map<String, Object>> getCustomSalesReport(ReportQueryDTO dto) {
        return reportMapper.getCustomSalesReport(dto, dataPermissionService.getVisibleOwnerIds());
    }

    /**
     * 销售趋势预测
     * <p>
     * 使用最小二乘法线性回归，基于历史 N 个月的合同金额、新增客户数、新增商机数，
     * 预测未来 N 个月的趋势。
     */
    @Override
    public ForecastVO salesForecast(int forecastMonths, int historyMonths) {
        List<TrendVO> history = reportMapper.getTrend(historyMonths, dataPermissionService.getVisibleOwnerIds());
        ForecastVO vo = new ForecastVO();

        // 预测下一个月份
        YearMonth nextMonth = YearMonth.now().plusMonths(1);
        vo.setMonth(nextMonth.format(DateTimeFormatter.ofPattern("yyyy年MM月")));
        vo.setHistory(history);

        if (history.isEmpty()) {
            vo.setForecastAmount(BigDecimal.ZERO);
            vo.setForecastCustomers(0L);
            vo.setForecastOpportunities(0L);
            return vo;
        }

        // 构造 x, y 数组（x = 0,1,2,...）
        int n = history.size();
        double sumX = 0, sumY = 0, sumXY = 0, sumXX = 0;
        for (int i = 0; i < n; i++) {
            double x = i;
            double y = history.get(i).getContractAmount().doubleValue();
            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumXX += x * x;
        }
        // 最小二乘法求斜率和截距：y = a*x + b
        double denom = n * sumXX - sumX * sumX;
        double a = denom == 0 ? 0 : (n * sumXY - sumX * sumY) / denom;
        double b = (sumY - a * sumX) / n;
        double forecastAmount = Math.max(0, a * n + b);

        vo.setForecastAmount(BigDecimal.valueOf(forecastAmount).setScale(2, RoundingMode.HALF_UP));

        // 预测新增客户数（取历史平均值）
        double avgCustomers = history.stream()
                .filter(t -> t.getNewCustomers() != null)
                .mapToLong(TrendVO::getNewCustomers)
                .average().orElse(0);
        vo.setForecastCustomers(Math.round(avgCustomers));

        // 预测新增商机数（取历史平均值）
        double avgOpps = history.stream()
                .filter(t -> t.getNewOpportunities() != null)
                .mapToLong(TrendVO::getNewOpportunities)
                .average().orElse(0);
        vo.setForecastOpportunities(Math.round(avgOpps));

        return vo;
    }

    /**
     * 高流失风险客户识别
     * <p>
     * - 高风险：超 90 天未跟进
     * - 中风险：超 60 天未跟进
     * - 低风险：超阈值天数未跟进
     */
    @Override
    public List<ChurnRiskVO> getChurnRiskCustomers(int thresholdDays) {
        List<ChurnRiskVO> list = reportMapper.getChurnRiskCustomers(
                thresholdDays, dataPermissionService.getVisibleOwnerIds());
        for (ChurnRiskVO vo : list) {
            int days = vo.getDaysSinceLastFollow() == null ? thresholdDays : vo.getDaysSinceLastFollow();
            if (days >= 90) {
                vo.setRiskLevel(3);
                vo.setRiskLevelText("高风险");
                vo.setSuggestedAction("立即安排拜访或电话沟通，必要时升级至VIP跟进");
            } else if (days >= 60) {
                vo.setRiskLevel(2);
                vo.setRiskLevelText("中风险");
                vo.setSuggestedAction("本周内主动联系客户，了解近况并安排跟进");
            } else {
                vo.setRiskLevel(1);
                vo.setRiskLevelText("低风险");
                vo.setSuggestedAction("两周内发送关怀信息或行业资讯，保持联系");
            }
        }
        return list;
    }

    /**
     * 挽留动作触发
     * <p>
     * 向客户负责人发送挽留提醒消息
     */
    @Override
    public boolean triggerRetention(Long customerId) {
        Long ownerId = reportMapper.getCustomerOwnerId(customerId);
        if (ownerId == null) {
            return false;
        }
        String title = "客户挽留提醒";
        String content = "您负责的客户（ID：" + customerId + "）已长时间未跟进，存在流失风险，请尽快安排联系。";
        return reportMapper.insertMessage(ownerId, title, content, customerId, "churn_risk") > 0;
    }

    /**
     * 渠道投入产出比（ROI）报表
     * <p>
     * 应用数据权限过滤，仅统计当前用户可见范围内的数据
     */
    @Override
    public List<Map<String, Object>> getChannelRoiStats() {
        return reportMapper.getChannelRoiStats(dataPermissionService.getVisibleOwnerIds());
    }
}
