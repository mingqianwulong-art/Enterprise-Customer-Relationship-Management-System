package com.crm.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crm.system.entity.SysMessage;
import com.crm.system.mapper.SysMessageMapper;
import com.crm.system.service.ISysMessageService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 系统消息服务实现
 *
 * @author CRM
 */
@Service
public class SysMessageServiceImpl extends ServiceImpl<SysMessageMapper, SysMessage> implements ISysMessageService {

    /**
     * 发送消息
     */
    @Override
    public boolean sendMessage(Long userId, String title, String content, Integer type, Long refId, String refType) {
        SysMessage message = new SysMessage();
        message.setUserId(userId);
        message.setTitle(title);
        message.setContent(content);
        message.setType(type);
        message.setRefId(refId);
        message.setRefType(refType);
        message.setIsRead(0);
        return baseMapper.insert(message) > 0;
    }

    /**
     * 查询用户未读消息列表
     */
    @Override
    public List<SysMessage> listUnread(Long userId) {
        return baseMapper.selectList(
                new LambdaQueryWrapper<SysMessage>()
                        .eq(SysMessage::getUserId, userId)
                        .eq(SysMessage::getIsRead, 0)
                        .orderByDesc(SysMessage::getCreateTime));
    }

    /**
     * 查询用户消息列表（含已读）
     */
    @Override
    public List<SysMessage> listByUserId(Long userId) {
        return baseMapper.selectList(
                new LambdaQueryWrapper<SysMessage>()
                        .eq(SysMessage::getUserId, userId)
                        .orderByDesc(SysMessage::getCreateTime));
    }

    /**
     * 标记消息为已读
     */
    @Override
    public boolean markAsRead(Long id) {
        SysMessage update = new SysMessage();
        update.setId(id);
        update.setIsRead(1);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 标记用户所有消息为已读
     */
    @Override
    public int markAllAsRead(Long userId) {
        LambdaUpdateWrapper<SysMessage> wrapper = new LambdaUpdateWrapper<SysMessage>()
                .eq(SysMessage::getUserId, userId)
                .eq(SysMessage::getIsRead, 0)
                .set(SysMessage::getIsRead, 1);
        return baseMapper.update(null, wrapper);
    }

    /**
     * 统计用户未读消息数量
     */
    @Override
    public int countUnread(Long userId) {
        return Math.toIntExact(baseMapper.selectCount(
                new LambdaQueryWrapper<SysMessage>()
                        .eq(SysMessage::getUserId, userId)
                        .eq(SysMessage::getIsRead, 0)));
    }
}
