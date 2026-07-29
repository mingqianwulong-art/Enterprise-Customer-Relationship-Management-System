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
}
