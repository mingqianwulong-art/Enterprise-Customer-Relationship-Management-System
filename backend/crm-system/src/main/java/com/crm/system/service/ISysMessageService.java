package com.crm.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.crm.system.entity.SysMessage;

import java.util.List;

/**
 * 系统消息服务接口
 *
 * @author CRM
 */
public interface ISysMessageService extends IService<SysMessage> {

    /**
     * 发送消息
     *
     * @param userId   接收人ID
     * @param title    消息标题
     * @param content  消息内容
     * @param type     消息类型 1跟进提醒 2商机预警 3系统通知
     * @param refId    关联业务ID
     * @param refType  关联业务类型
     * @return 是否成功
     */
    boolean sendMessage(Long userId, String title, String content, Integer type, Long refId, String refType);

    /**
     * 查询用户未读消息列表
     *
     * @param userId 用户ID
     * @return 未读消息列表
     */
    List<SysMessage> listUnread(Long userId);

    /**
     * 查询用户消息列表（含已读）
     *
     * @param userId 用户ID
     * @return 消息列表
     */
    List<SysMessage> listByUserId(Long userId);

    /**
     * 标记消息为已读
     *
     * @param id 消息ID
     * @return 是否成功
     */
    boolean markAsRead(Long id);

    /**
     * 标记用户所有消息为已读
     *
     * @param userId 用户ID
     * @return 更新的消息数量
     */
    int markAllAsRead(Long userId);

    /**
     * 统计用户未读消息数量
     *
     * @param userId 用户ID
     * @return 未读数量
     */
    int countUnread(Long userId);
}
