package com.crm.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crm.system.entity.SysLog;
import com.crm.system.mapper.SysLogMapper;
import com.crm.system.service.ISysLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 操作日志服务实现
 *
 * @author CRM
 */
@Slf4j
@Service
public class SysLogServiceImpl extends ServiceImpl<SysLogMapper, SysLog> implements ISysLogService {

    /**
     * 异步保存操作日志
     */
    @Async
    @Override
    public void asyncSaveLog(SysLog sysLog) {
        try {
            baseMapper.insert(sysLog);
        } catch (Exception e) {
            log.error("保存操作日志失败：{}", e.getMessage(), e);
        }
    }
}
