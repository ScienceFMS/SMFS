package hit.cs.sciencefms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import hit.cs.sciencefms.entity.ResearchProject;
import hit.cs.sciencefms.mapper.ResearchProjectMapper;
import hit.cs.sciencefms.service.ResearchProjectService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 科研项目服务实现类
 */
@Service
public class ResearchProjectServiceImpl extends ServiceImpl<ResearchProjectMapper, ResearchProject> implements ResearchProjectService {

    @Override
    public List<ResearchProject> getProjectsByTeacherId(Long teacherId) {
        LambdaQueryWrapper<ResearchProject> wrapper = Wrappers.<ResearchProject>lambdaQuery()
                .eq(ResearchProject::getTeacherId, teacherId)
                .orderByDesc(ResearchProject::getCreateTime);
        return baseMapper.selectList(wrapper);
    }
    
    @Override
    public Page<ResearchProject> pageProjectsByTeacherId(Long teacherId, int page, int pageSize, String status, String type, String keyword) {
        Page<ResearchProject> pageParam = new Page<>(page, pageSize);
        
        LambdaQueryWrapper<ResearchProject> wrapper = Wrappers.<ResearchProject>lambdaQuery()
                .eq(ResearchProject::getTeacherId, teacherId)
                .eq(StringUtils.hasText(type), ResearchProject::getSource, type)          // source存储项目类型
                .and(StringUtils.hasText(keyword), w -> w
                        .like(ResearchProject::getProjectName, keyword)
                        .or()
                        .like(ResearchProject::getProjectCode, keyword)
                );

        // 根据状态筛选
        if (StringUtils.hasText(status)) {
            // 基于日期计算项目状态
            // 这里为了性能考虑，直接在SQL层面进行筛选，而不是先查出所有再过滤
            LocalDate today = LocalDate.now();
            if ("进行中".equals(status)) {
                wrapper.le(ResearchProject::getStartDate, today)
                      .ge(ResearchProject::getEndDate, today);
            } else if ("已结题".equals(status)) {
                wrapper.lt(ResearchProject::getEndDate, today);
            } else if ("未开始".equals(status)) {
                wrapper.gt(ResearchProject::getStartDate, today);
            }
        }
        
        wrapper.orderByDesc(ResearchProject::getCreateTime);
        
        return baseMapper.selectPage(pageParam, wrapper);
    }
    
    @Override
    @Transactional
    public boolean addProject(ResearchProject project) {
        project.setCreateTime(LocalDateTime.now());
        project.setUpdateTime(LocalDateTime.now());
        project.setIsDeleted(0);
        return save(project);
    }
    
    @Override
    @Transactional
    public boolean updateProject(ResearchProject project) {
        project.setUpdateTime(LocalDateTime.now());
        return updateById(project);
    }
    
    @Override
    @Transactional
    public boolean deleteProject(Long id) {
        return removeById(id);
    }
    
    @Override
    public ResearchProject getProjectById(Long id) {
        return getById(id);
    }
} 