package com.crm.report.controller;

import com.crm.common.api.R;
import com.crm.report.dto.ReportQueryDTO;
import com.crm.report.service.IReportService;
import com.crm.report.vo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 数据分析报表 Controller
 */
@Tag(name = "数据分析报表")
@RestController
@RequestMapping("/report")
public class ReportController {

    @Autowired
    private IReportService reportService;

    @Operation(summary = "获取数据看板概览统计")
    @GetMapping("/overview")
    public R<DashboardOverviewVO> getOverview() {
        return R.ok(reportService.getOverview());
    }

    @Operation(summary = "获取趋势数据")
    @GetMapping("/trend")
    public R<List<TrendVO>> getTrend(@RequestParam(defaultValue = "6") int months) {
        return R.ok(reportService.getTrend(months));
    }

    @Operation(summary = "获取工单状态分布")
    @GetMapping("/order-status")
    public R<List<ServiceStatsVO>> getOrderStatusStats() {
        return R.ok(reportService.getOrderStatusStats());
    }

    @Operation(summary = "获取销售业绩排行")
    @GetMapping("/sales-ranking")
    public R<List<SalesRankingVO>> getSalesRanking() {
        return R.ok(reportService.getSalesRanking());
    }

    @Operation(summary = "获取客户行业分布")
    @GetMapping("/customer-industry")
    public R<List<CustomerSourceVO>> getCustomerIndustryStats() {
        return R.ok(reportService.getCustomerIndustryStats());
    }

    @Operation(summary = "获取线索来源分布")
    @GetMapping("/clue-source")
    public R<List<CustomerSourceVO>> getClueSourceStats() {
        return R.ok(reportService.getClueSourceStats());
    }

    @Operation(summary = "自定义客户报表")
    @GetMapping("/custom/customer")
    public R<List<Map<String, Object>>> getCustomCustomerReport(ReportQueryDTO dto) {
        return R.ok(reportService.getCustomCustomerReport(dto));
    }

    @Operation(summary = "自定义销售报表")
    @GetMapping("/custom/sales")
    public R<List<Map<String, Object>>> getCustomSalesReport(ReportQueryDTO dto) {
        return R.ok(reportService.getCustomSalesReport(dto));
    }

    // ==================== 预测分析（M5-03） ====================

    /**
     * 销售趋势预测
     * <p>
     * 基于历史数据线性回归预测未来销售趋势
     *
     * @param forecastMonths 预测未来月数（默认1）
     * @param historyMonths  参考历史月数（默认6）
     */
    @Operation(summary = "销售趋势预测")
    @GetMapping("/forecast/sales")
    public R<ForecastVO> salesForecast(
            @RequestParam(defaultValue = "1") int forecastMonths,
            @RequestParam(defaultValue = "6") int historyMonths) {
        return R.ok(reportService.salesForecast(forecastMonths, historyMonths));
    }

    /**
     * 高流失风险客户识别
     * <p>
     * 识别超过阈值天数未跟进的客户，按风险等级排序
     *
     * @param thresholdDays 阈值天数（默认60天）
     */
    @Operation(summary = "高流失风险客户识别")
    @GetMapping("/forecast/churn-risk")
    public R<List<ChurnRiskVO>> getChurnRiskCustomers(
            @RequestParam(defaultValue = "60") int thresholdDays) {
        return R.ok(reportService.getChurnRiskCustomers(thresholdDays));
    }

    /**
     * 挽留动作触发
     * <p>
     * 对高流失风险客户触发挽留动作，向负责人发送提醒消息
     *
     * @param customerId 客户ID
     */
    @Operation(summary = "挽留动作触发-发送提醒")
    @PostMapping("/forecast/retain/{customerId}")
    public R triggerRetention(@PathVariable Long customerId) {
        return reportService.triggerRetention(customerId) ? R.ok("挽留提醒已发送至负责人") : R.fail("触发失败，客户不存在或无负责人");
    }
}
