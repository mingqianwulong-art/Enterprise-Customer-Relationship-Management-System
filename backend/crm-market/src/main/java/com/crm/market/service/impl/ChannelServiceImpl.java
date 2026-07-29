package com.crm.market.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crm.common.exception.BusinessException;
import com.crm.market.entity.Channel;
import com.crm.market.entity.Clue;
import com.crm.market.mapper.ChannelMapper;
import com.crm.market.mapper.ClueMapper;
import com.crm.market.service.IChannelService;
import com.crm.market.vo.ChannelStatsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * 渠道服务实现
 *
 * @author CRM
 */
@Service
public class ChannelServiceImpl extends ServiceImpl<ChannelMapper, Channel> implements IChannelService {

    @Autowired
    private ClueMapper clueMapper;

    /**
     * 分页查询渠道
     */
    @Override
    public IPage<Channel> page(Integer pageNum, Integer pageSize, String channelName) {
        Page<Channel> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Channel> wrapper = new LambdaQueryWrapper<Channel>()
                .like(channelName != null && !channelName.isEmpty(),
                        Channel::getChannelName, channelName)
                .orderByDesc(Channel::getCreateTime);
        return baseMapper.selectPage(page, wrapper);
    }

    /**
     * 查询所有启用的渠道（下拉用）
     */
    @Override
    public List<Channel> list() {
        return baseMapper.selectList(
                new LambdaQueryWrapper<Channel>()
                        .eq(Channel::getStatus, 1)
                        .orderByDesc(Channel::getCreateTime));
    }

    /**
     * 新增渠道
     */
    @Override
    public boolean addChannel(Channel channel) {
        return baseMapper.insert(channel) > 0;
    }

    /**
     * 修改渠道
     */
    @Override
    public boolean updateChannel(Channel channel) {
        if (channel.getId() == null) {
            throw new BusinessException("渠道ID不能为空");
        }
        return baseMapper.updateById(channel) > 0;
    }

    /**
     * 删除渠道（逻辑删除）
     */
    @Override
    public boolean deleteChannel(Long id) {
        return baseMapper.deleteById(id) > 0;
    }

    /**
     * 渠道效果统计
     */
    @Override
    public List<ChannelStatsVO> getStats() {
        // 查询所有渠道
        List<Channel> channels = baseMapper.selectList(null);
        List<ChannelStatsVO> result = new ArrayList<>();
        for (Channel channel : channels) {
            ChannelStatsVO vo = new ChannelStatsVO();
            vo.setChannelId(channel.getId());
            vo.setChannelName(channel.getChannelName());
            vo.setChannelType(channel.getChannelType());
            vo.setCost(channel.getCost());
            // 统计该渠道的线索数
            Long clueCount = clueMapper.selectCount(
                    new LambdaQueryWrapper<Clue>()
                            .eq(Clue::getChannelId, channel.getId()));
            vo.setClueCount(clueCount.intValue());
            // 统计已转化的线索数（状态为2已转化）
            Long convertedCount = clueMapper.selectCount(
                    new LambdaQueryWrapper<Clue>()
                            .eq(Clue::getChannelId, channel.getId())
                            .eq(Clue::getStatus, 2));
            // 计算转化率（百分比，保留2位小数）
            BigDecimal conversionRate = BigDecimal.ZERO;
            if (clueCount > 0) {
                conversionRate = new BigDecimal(convertedCount)
                        .divide(new BigDecimal(clueCount), 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal(100))
                        .setScale(2, RoundingMode.HALF_UP);
            }
            vo.setConversionRate(conversionRate);
            result.add(vo);
        }
        return result;
    }
}
