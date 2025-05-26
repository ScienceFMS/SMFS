package hit.cs.sciencefms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import hit.cs.sciencefms.entity.Award;
import hit.cs.sciencefms.mapper.AwardMapper;
import hit.cs.sciencefms.service.AwardService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;

/**
 * 获奖信息服务实现类
 */
@Service
public class AwardServiceImpl extends ServiceImpl<AwardMapper, Award> implements AwardService {

    @Override
    public List<Award> getAwardsByTeacherId(Long teacherId) {
        LambdaQueryWrapper<Award> wrapper = Wrappers.<Award>lambdaQuery()
                .eq(Award::getTeacherId, teacherId)
                .orderByDesc(Award::getAwardDate);
        return list(wrapper);
    }

    @Override
    public Page<Award> pageAwardsByTeacherId(Long teacherId, int page, int pageSize, Integer year, String keyword) {
        Page<Award> pageParam = new Page<>(page, pageSize);
        
        LambdaQueryWrapper<Award> wrapper = Wrappers.<Award>lambdaQuery()
                .eq(Award::getTeacherId, teacherId);
        
        // 按年份筛选
        if (year != null) {
            LocalDate startDate = LocalDate.of(year, 1, 1);
            LocalDate endDate = LocalDate.of(year, 12, 31);
            wrapper.between(Award::getAwardDate, startDate, endDate);
        }
        
        // 关键词搜索
        if (StringUtils.isNotEmpty(keyword)) {
            wrapper.and(w -> w.like(Award::getAwardName, keyword)
                    .or()
                    .like(Award::getAwardLevel, keyword)
                    .or()
                    .like(Award::getIssuingUnit, keyword));
        }
        
        wrapper.orderByDesc(Award::getAwardDate);
        
        return page(pageParam, wrapper);
    }

    @Override
    public boolean addAward(Award award) {
        award.setCreateTime(LocalDateTime.now());
        award.setUpdateTime(LocalDateTime.now());
        award.setIsDeleted(0);
        return save(award);
    }

    @Override
    public boolean updateAward(Award award) {
        award.setUpdateTime(LocalDateTime.now());
        return updateById(award);
    }

    @Override
    public boolean deleteAward(Long id) {
        return removeById(id);
    }

    @Override
    public Award getAwardById(Long id) {
        return getById(id);
    }
} 