package com.crm.report.service;

import com.crm.report.dto.ReportQueryDTO;
import com.crm.report.vo.*;

import java.util.List;
import java.util.Map;

/**
 * 报表服务接口
 */
public interface IReportService {

    /**
     * 获取数据看板概览统计
     */
    DashboardOverviewVO getOverview();

    /**
     * 获取趋势数据
     * @param months 近N个月
     */
    List<TrendVO> getTrend(int months);

    /**
     * 获取工单状态分布
     */
    List<ServiceStatsVO> getOrderStatusStats();

    /**
     * 获取销售业绩排行
     */
    List<SalesRankingVO> getSalesRanking();

    /**
     * 获取客户行业分布
     */
    List<CustomerSourceVO> getCustomerIndustryStats();

    /**
     * 获取线索来源分布
     */
    List<CustomerSourceVO> getClueSourceStats();

    /**
     * 自定义客户报表
     */
    List<Map<String, Object>> getCustomCustomerReport(ReportQueryDTO dto);

    /**
     * 自定义销售报表
     */
    List<Map<String, Object>> getCustomSalesReport(ReportQueryDTO dto);
}
