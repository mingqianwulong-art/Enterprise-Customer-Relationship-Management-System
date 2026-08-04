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

    /**
     * 销售趋势预测
     * <p>
     * 基于历史数据线性回归预测未来 N 个月的销售趋势
     *
     * @param forecastMonths 预测未来月数
     * @param historyMonths  参考历史月数
     */
    ForecastVO salesForecast(int forecastMonths, int historyMonths);

    /**
     * 高流失风险客户识别
     * <p>
     * 识别超过阈值天数未跟进的客户，按风险等级排序
     *
     * @param thresholdDays 阈值天数（默认60天）
     */
    List<ChurnRiskVO> getChurnRiskCustomers(int thresholdDays);

    /**
     * 挽留动作触发
     * <p>
     * 对高流失风险客户触发挽留动作，向负责人发送提醒消息
     *
     * @param customerId 客户ID
     * @return 是否触发成功
     */
    boolean triggerRetention(Long customerId);

    /**
     * 渠道投入产出比（ROI）报表
     * <p>
     * 统计每个渠道的线索数、转化数、投放成本、成交金额、获客成本、ROI
     *
     * @return 渠道ROI列表
     */
    List<Map<String, Object>> getChannelRoiStats();
}
