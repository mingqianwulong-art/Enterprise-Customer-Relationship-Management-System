package com.crm.market.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crm.common.exception.BusinessException;
import com.crm.market.entity.Clue;
import com.crm.market.mapper.ClueMapper;
import com.crm.market.service.IClueService;
import com.crm.market.vo.CluePageDTO;
import com.crm.system.service.DataPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 线索服务实现
 *
 * @author CRM
 */
@Service
public class ClueServiceImpl extends ServiceImpl<ClueMapper, Clue> implements IClueService {

    @Autowired
    private DataPermissionService dataPermissionService;

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
                .eq(dto.getLevel() != null,
                        Clue::getLevel, dto.getLevel())
                .eq(dto.getChannelId() != null,
                        Clue::getChannelId, dto.getChannelId())
                .orderByDesc(Clue::getCreateTime);
        // 数据权限过滤
        List<Long> visibleOwnerIds = dataPermissionService.getVisibleOwnerIds();
        if (visibleOwnerIds != null) {
            wrapper.in(Clue::getOwnerId, visibleOwnerIds);
        }
        return baseMapper.selectPage(page, wrapper);
    }

    /**
     * 新增线索
     */
    @Override
    public boolean addClue(Clue clue) {
        if (clue.getStatus() == null) {
            clue.setStatus(0);
        }
        // 同一公司下线索名不能重复
        Long nameCount = baseMapper.selectCount(new LambdaQueryWrapper<Clue>()
                .eq(Clue::getCompany, clue.getCompany())
                .eq(Clue::getClueName, clue.getClueName()));
        if (nameCount > 0) {
            throw new BusinessException("该公司下已存在同名线索，请勿重复新建");
        }
        // 同一电话不能对应不同公司
        if (clue.getPhone() != null && !clue.getPhone().isEmpty()) {
            Long phoneCount = baseMapper.selectCount(new LambdaQueryWrapper<Clue>()
                    .eq(Clue::getPhone, clue.getPhone())
                    .ne(Clue::getCompany, clue.getCompany()));
            if (phoneCount > 0) {
                throw new BusinessException("该电话号码已关联其他公司，请核实后再试");
            }
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
     * 使用条件更新（WHERE status=0）实现乐观锁，防止并发分配
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
        LambdaUpdateWrapper<Clue> wrapper = new LambdaUpdateWrapper<Clue>()
                .eq(Clue::getId, clueId)
                .eq(Clue::getStatus, 0)
                .set(Clue::getOwnerId, userId)
                .set(Clue::getStatus, 1);
        int rows = baseMapper.update(null, wrapper);
        if (rows == 0) {
            throw new BusinessException("线索状态已变更，请刷新后重试");
        }
        return true;
    }

    /**
     * 抢单
     * 使用条件更新（WHERE status=0）实现乐观锁，防止并发抢单
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
        LambdaUpdateWrapper<Clue> wrapper = new LambdaUpdateWrapper<Clue>()
                .eq(Clue::getId, clueId)
                .eq(Clue::getStatus, 0)
                .set(Clue::getOwnerId, userId)
                .set(Clue::getStatus, 1);
        int rows = baseMapper.update(null, wrapper);
        if (rows == 0) {
            throw new BusinessException("线索已被他人领取，请刷新后重试");
        }
        return true;
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

    /**
     * 退回线索（重置为待分配状态，清空负责人）
     * 使用条件更新（WHERE status=1）防止重复退回
     */
    @Override
    public boolean returnClue(Long clueId) {
        Clue clue = baseMapper.selectById(clueId);
        if (clue == null) {
            throw new BusinessException("线索不存在");
        }
        if (clue.getStatus() == null || clue.getStatus() != 1) {
            throw new BusinessException("仅已分配状态的线索可退回");
        }
        LambdaUpdateWrapper<Clue> wrapper = new LambdaUpdateWrapper<Clue>()
                .eq(Clue::getId, clueId)
                .eq(Clue::getStatus, 1)
                .set(Clue::getOwnerId, null)
                .set(Clue::getStatus, 0);
        int rows = baseMapper.update(null, wrapper);
        if (rows == 0) {
            throw new BusinessException("线索状态已变更，请刷新后重试");
        }
        return true;
    }
}
