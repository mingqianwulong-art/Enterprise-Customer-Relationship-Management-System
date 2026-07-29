package com.crm.report.service.impl;

import com.crm.report.dto.ReportQueryDTO;
import com.crm.report.mapper.ReportMapper;
import com.crm.report.service.IReportService;
import com.crm.report.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

/**
 * 报表服务实现
 */
@Service
public class ReportServiceImpl implements IReportService {

    @Autowired
    private ReportMapper reportMapper;

    @Override
    public DashboardOverviewVO getOverview() {
        DashboardOverviewVO vo = new DashboardOverviewVO();

        // 客户
        vo.setTotalCustomers(reportMapper.countCustomers());
        vo.setMonthlyNewCustomers(reportMapper.countMonthlyNewCustomers());
        vo.setPoolCustomers(reportMapper.countPoolCustomers());

        // 线索
        Long totalClues = reportMapper.countClues();
        Long convertedClues = reportMapper.countConvertedClues();
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
        Long totalOpps = reportMapper.countOpportunities();
        Long wonOpps = reportMapper.countWonOpportunities();
        vo.setTotalOpportunities(totalOpps);
        vo.setActiveOpportunities(reportMapper.countActiveOpportunities());
        vo.setWonOpportunities(wonOpps);
        if (totalOpps > 0) {
            vo.setWinRate(BigDecimal.valueOf(wonOpps)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalOpps), 1, RoundingMode.HALF_UP));
        } else {
            vo.setWinRate(BigDecimal.ZERO);
        }

        // 合同
        vo.setTotalContracts(reportMapper.countContracts());
        BigDecimal contractAmount = reportMapper.sumContractAmount();
        vo.setTotalContractAmount(contractAmount);

        // 回款
        BigDecimal receivedAmount = reportMapper.sumReceivedAmount();
        vo.setTotalReceivedAmount(receivedAmount);
        if (contractAmount.compareTo(BigDecimal.ZERO) > 0) {
            vo.setPaymentRate(receivedAmount
                    .multiply(BigDecimal.valueOf(100))
                    .divide(contractAmount, 1, RoundingMode.HALF_UP));
        } else {
            vo.setPaymentRate(BigDecimal.ZERO);
        }

        // 工单
        vo.setTotalOrders(reportMapper.countOrders());
        vo.setPendingOrders(reportMapper.countPendingOrders());
        vo.setCompletedOrders(reportMapper.countCompletedOrders());
        vo.setAvgSatisfaction(reportMapper.avgSatisfaction());

        return vo;
    }

    @Override
    public List<TrendVO> getTrend(int months) {
        return reportMapper.getTrend(months);
    }

    @Override
    public List<ServiceStatsVO> getOrderStatusStats() {
        return reportMapper.getOrderStatusStats();
    }

    @Override
    public List<SalesRankingVO> getSalesRanking() {
        return reportMapper.getSalesRanking();
    }

    @Override
    public List<CustomerSourceVO> getCustomerIndustryStats() {
        return reportMapper.getCustomerIndustryStats();
    }

    @Override
    public List<CustomerSourceVO> getClueSourceStats() {
        return reportMapper.getClueSourceStats();
    }

    @Override
    public List<Map<String, Object>> getCustomCustomerReport(ReportQueryDTO dto) {
        return reportMapper.getCustomCustomerReport(dto);
    }

    @Override
    public List<Map<String, Object>> getCustomSalesReport(ReportQueryDTO dto) {
        return reportMapper.getCustomSalesReport(dto);
    }
}
