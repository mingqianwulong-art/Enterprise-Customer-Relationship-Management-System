package com.crm.market.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crm.common.exception.BusinessException;
import com.crm.market.entity.Clue;
import com.crm.market.mapper.ClueMapper;
import com.crm.market.service.IClueService;
import com.crm.market.vo.CluePageDTO;
import org.springframework.stereotype.Service;

/**
 * 线索服务实现
 *
 * @author CRM
 */
@Service
public class ClueServiceImpl extends ServiceImpl<ClueMapper, Clue> implements IClueService {

    /**
     * 分页查询线索
     */
    @Override
    public IPage<Clue> page(CluePageDTO dto) {
        Page<Clue> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        LambdaQueryWrapper<Clue> wrapper = new LambdaQueryWrapper<Clue>()
                .like(dto.getClueName() != null && !dto.getClueName().isEmpty(),
                        Clue::getClueName, dto.getClueName())
                .eq(dto.getSource() != null && !dto.getSource().isEmpty(),
                        Clue::getSource, dto.getSource())
                .eq(dto.getIndustry() != null && !dto.getIndustry().isEmpty(),
                        Clue::getIndustry, dto.getIndustry())
                .eq(dto.getRegion() != null && !dto.getRegion().isEmpty(),
                        Clue::getRegion, dto.getRegion())
                .eq(dto.getStatus() != null,
                        Clue::getStatus, dto.getStatus())
                .eq(dto.getChannelId() != null,
                        Clue::getChannelId, dto.getChannelId())
                .orderByDesc(Clue::getCreateTime);
        return baseMapper.selectPage(page, wrapper);
    }

    /**
     * 新增线索
     */
    @Override
    public boolean addClue(Clue clue) {
        // 新增线索默认待分配
        if (clue.getStatus() == null) {
            clue.setStatus(0);
        }
        return baseMapper.insert(clue) > 0;
    }

    /**
     * 修改线索
     */
    @Override
    public boolean updateClue(Clue clue) {
        if (clue.getId() == null) {
            throw new BusinessException("线索ID不能为空");
        }
        return baseMapper.updateById(clue) > 0;
    }

    /**
     * 删除线索（逻辑删除）
     */
    @Override
    public boolean deleteClue(Long id) {
        return baseMapper.deleteById(id) > 0;
    }

    /**
     * 分配线索给销售
     */
    @Override
    public boolean assignClue(Long clueId, Long userId) {
        Clue clue = baseMapper.selectById(clueId);
        if (clue == null) {
            throw new BusinessException("线索不存在");
        }
        if (clue.getStatus() != null && clue.getStatus() != 0) {
            throw new BusinessException("该线索已被分配，无法重复分配");
        }
        Clue update = new Clue();
        update.setId(clueId);
        update.setOwnerId(userId);
        update.setStatus(1);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 抢单
     */
    @Override
    public boolean claimClue(Long clueId, Long userId) {
        Clue clue = baseMapper.selectById(clueId);
        if (clue == null) {
            throw new BusinessException("线索不存在");
        }
        if (clue.getStatus() != null && clue.getStatus() != 0) {
            throw new BusinessException("该线索已被领取，无法重复抢单");
        }
        Clue update = new Clue();
        update.setId(clueId);
        update.setOwnerId(userId);
        update.setStatus(1);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 转化为客户
     */
    @Override
    public boolean convertClue(Long clueId, Long customerId) {
        Clue clue = baseMapper.selectById(clueId);
        if (clue == null) {
            throw new BusinessException("线索不存在");
        }
        Clue update = new Clue();
        update.setId(clueId);
        update.setCustomerId(customerId);
        update.setStatus(2);
        return baseMapper.updateById(update) > 0;
    }
}
