package com.crm.system.service;

import com.aliyun.dypnsapi20170525.Client;
import com.aliyun.dypnsapi20170525.models.SendSmsVerifyCodeRequest;
import com.aliyun.dypnsapi20170525.models.SendSmsVerifyCodeResponse;
import com.aliyun.teaopenapi.models.Config;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 短信验证码服务（阿里云号码认证服务）
 * <p>
 * 支持两种模式：
 * 1. 开发模式（sms.enabled=false）：仅控制台打印验证码，不真实发送
 * 2. 生产模式（sms.enabled=true）：调用阿里云号码认证API真实发送
 * <p>
 * Redis 存储验证码，60秒内不可重复发送，验证码5分钟有效
 *
 * @author CRM
 */
@Service
public class SmsService {

    private static final Logger log = LoggerFactory.getLogger(SmsService.class);

    /** 验证码 Redis key 前缀 */
    private static final String SMS_CODE_KEY = "sms:code:";

    /** 发送频率限制 key 前缀（60秒内不可重发） */
    private static final String SMS_LIMIT_KEY = "sms:limit:";

    /** 验证码有效期（分钟） */
    private static final int CODE_EXPIRE_MINUTES = 5;

    /** 发送频率限制（秒） */
    private static final int LIMIT_SECONDS = 60;

    /** 阿里云客户端（懒加载） */
    private Client aliClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Value("${sms.enabled:false}")
    private boolean enabled;

    @Value("${sms.access-key-id:}")
    private String accessKeyId;

    @Value("${sms.access-key-secret:}")
    private String accessKeySecret;

    @Value("${sms.sign-name:}")
    private String signName;

    @Value("${sms.template-code:}")
    private String templateCode;

    /**
     * 发送短信验证码
     *
     * @param phone 手机号
     */
    public void sendCode(String phone) {
        // 1. 检查发送频率限制（60秒内不可重发）
        Boolean hasKey = redisTemplate.hasKey(SMS_LIMIT_KEY + phone);
        if (Boolean.TRUE.equals(hasKey)) {
            throw new RuntimeException("验证码已发送，请60秒后再试");
        }

        // 2. 生成6位随机验证码
        String code = String.format("%06d", (int) (Math.random() * 1000000));

        // 3. 存储验证码到Redis，5分钟有效
        redisTemplate.opsForValue().set(SMS_CODE_KEY + phone, code, CODE_EXPIRE_MINUTES, TimeUnit.MINUTES);
        // 4. 设置发送频率限制，60秒
        redisTemplate.opsForValue().set(SMS_LIMIT_KEY + phone, "1", LIMIT_SECONDS, TimeUnit.SECONDS);

        // 5. 根据配置决定：真实发送 OR 开发模式
        if (enabled) {
            sendAliyunSms(phone, code);
        } else {
            // 开发模式：仅控制台打印
            log.info("[开发模式-短信验证码] 手机号：{}，验证码：{}，有效期{}分钟", phone, code, CODE_EXPIRE_MINUTES);
        }
    }

    /**
     * 调用阿里云号码认证服务发送验证码
     * 接口：SendSmsVerifyCode
     * 模板变量：${code} 验证码，${min} 有效期（分钟）
     */
    private void sendAliyunSms(String phone, String code) {
        try {
            Client client = getClient();
            // 模板参数：${code} 和 ${min}
            Map<String, String> templateParam = new HashMap<>();
            templateParam.put("code", code);
            templateParam.put("min", String.valueOf(CODE_EXPIRE_MINUTES));

            SendSmsVerifyCodeRequest request = new SendSmsVerifyCodeRequest()
                    .setPhoneNumber(phone)
                    .setSignName(signName)
                    .setTemplateCode(templateCode)
                    .setTemplateParam(objectMapper.writeValueAsString(templateParam));

            SendSmsVerifyCodeResponse response = client.sendSmsVerifyCode(request);

            if (response.body == null || !"OK".equals(response.body.code)) {
                String errCode = response.body != null ? response.body.code : "null";
                String errMsg = response.body != null ? response.body.message : "未知错误";
                log.error("[阿里云短信] 发送失败 phone={} code={} msg={}", phone, errCode, errMsg);
                throw new RuntimeException("短信发送失败：" + errMsg);
            }
            log.info("[阿里云短信] 发送成功 phone={}", phone);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("[阿里云短信] 发送异常 phone={}", phone, e);
            throw new RuntimeException("短信发送失败，请稍后重试");
        }
    }

    /**
     * 懒加载创建阿里云客户端（号码认证服务接口）
     */
    private Client getClient() throws Exception {
        if (aliClient == null) {
            Config config = new Config()
                    .setAccessKeyId(accessKeyId)
                    .setAccessKeySecret(accessKeySecret)
                    .setEndpoint("dypnsapi.aliyuncs.com");
            aliClient = new Client(config);
        }
        return aliClient;
    }

    /**
     * 验证短信验证码
     *
     * @param phone 手机号
     * @param code  验证码
     * @return 是否验证通过
     */
    public boolean verifyCode(String phone, String code) {
        String storedCode = redisTemplate.opsForValue().get(SMS_CODE_KEY + phone);
        if (storedCode == null) {
            return false;
        }
        if (storedCode.equals(code)) {
            // 验证成功后删除验证码，防止重复使用
            redisTemplate.delete(SMS_CODE_KEY + phone);
            return true;
        }
        return false;
    }
}
