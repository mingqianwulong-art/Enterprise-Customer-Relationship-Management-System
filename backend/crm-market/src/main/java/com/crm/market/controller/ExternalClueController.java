package com.crm.market.controller;

import com.crm.common.api.R;
import com.crm.market.dto.ExternalClueDTO;
import com.crm.market.entity.Clue;
import com.crm.market.service.ClueAssignRuleService;
import com.crm.market.service.IClueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 外部渠道线索归集 Controller
 * <p>
 * 供抖音、微信公域、官网表单等外部渠道通过 Webhook 推送线索数据。
 * 使用 API Key 认证，无需 JWT 登录。
 *
 * @author CRM
 */
@Tag(name = "外部渠道线索归集")
@RestController
@RequestMapping("/api/external/clue")
public class ExternalClueController {

    @Autowired
    private IClueService clueService;

    @Autowired
    private ClueAssignRuleService clueAssignRuleService;

    /**
     * 外部渠道接入密钥（从配置文件读取）
     */
    @Value("${crm.external.api-key:crm-external-key-2024}")
    private String validApiKey;

    /**
     * 接收外部渠道推送的线索
     * <p>
     * 外部渠道通过 HTTP POST 推送线索数据，Header 中携带 X-API-Key 进行认证。
     * 线索创建后自动进入待分配状态（status=0），由线索智能分配规则引擎处理。
     *
     * @param apiKey API Key（Header: X-API-Key）
     * @param dto    线索数据
     */
    @Operation(summary = "接收外部渠道线索")
    @PostMapping
    public R receiveClue(
            @RequestHeader("X-API-Key") String apiKey,
            @RequestBody ExternalClueDTO dto) {

        // API Key 认证
        if (!validApiKey.equals(apiKey)) {
            return R.fail("API Key 无效");
        }

        // 转换为线索实体
        Clue clue = new Clue();
        clue.setClueName(dto.getClueName());
        clue.setCompany(dto.getCompany());
        clue.setPhone(dto.getPhone());
        clue.setEmail(dto.getEmail());
        clue.setSource(dto.getSource());
        clue.setIndustry(dto.getIndustry());
        clue.setRegion(dto.getRegion());
        clue.setLevel(dto.getLevel() != null ? dto.getLevel() : 2);
        clue.setStatus(0);  // 待分配
        clue.setDescription(dto.getDescription());

        // 保存线索
        boolean success = clueService.addClue(clue);
        if (success) {
            // 异步触发线索自动分配规则引擎（1分钟内推送通知）
            clueAssignRuleService.autoAssignAndNotify(clue);
            return R.ok(clue.getId());
        }
        return R.fail("线索接收失败");
    }

    /**
     * 健康检查接口（用于外部渠道测试连通性）
     */
    @Operation(summary = "健康检查")
    @PostMapping("/ping")
    public R ping(@RequestHeader("X-API-Key") String apiKey) {
        if (!validApiKey.equals(apiKey)) {
            return R.fail("API Key 无效");
        }
        return R.ok("pong");
    }
}
