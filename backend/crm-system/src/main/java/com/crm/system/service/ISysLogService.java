package com.crm.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.crm.system.entity.SysLog;

/**
 * 操作日志服务接口
 *
 * @author CRM
 */
public interface ISysLogService extends IService<SysLog> {

    /**
     * 异步记录操作日志
     *
     * @param log 日志信息
     */
    void asyncSaveLog(SysLog log);
}
