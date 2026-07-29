package com.crm.market.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.crm.market.entity.Channel;
import com.crm.market.vo.ChannelStatsVO;

import java.util.List;

/**
 * 渠道服务接口
 *
 * @author CRM
 */
public interface IChannelService extends IService<Channel> {

    /**
     * 分页查询渠道
     *
     * @param pageNum     当前页
     * @param pageSize    每页条数
     * @param channelName 渠道名称（模糊查询）
     * @return 分页结果
     */
    IPage<Channel> page(Integer pageNum, Integer pageSize, String channelName);

    /**
     * 查询所有启用的渠道（下拉用）
     *
     * @return 启用渠道列表
     */
    List<Channel> list();

    /**
     * 新增渠道
     *
     * @param channel 渠道信息
     * @return 是否成功
     */
    boolean addChannel(Channel channel);

    /**
     * 修改渠道
     *
     * @param channel 渠道信息
     * @return 是否成功
     */
    boolean updateChannel(Channel channel);

    /**
     * 删除渠道
     *
     * @param id 渠道ID
     * @return 是否成功
     */
    boolean deleteChannel(Long id);

    /**
     * 渠道效果统计
     *
     * @return 渠道统计列表
     */
    List<ChannelStatsVO> getStats();
}
