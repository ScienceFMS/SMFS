package hit.cs.sciencefms.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hit.cs.sciencefms.dto.SystemOverviewDTO;
import hit.cs.sciencefms.dto.SystemOverviewDTO.SystemStatsDTO;
import hit.cs.sciencefms.mapper.AwardMapper;
import hit.cs.sciencefms.mapper.IntellectualPropertyMapper;
import hit.cs.sciencefms.mapper.ResearchProjectMapper;
import hit.cs.sciencefms.mapper.UserMapper;
import hit.cs.sciencefms.mapper.VisitRecordMapper;
import hit.cs.sciencefms.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;

/**
 * 管理员仪表盘服务实现类
 */
@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final UserMapper userMapper;
    private final IntellectualPropertyMapper intellectualPropertyMapper;
    private final AwardMapper awardMapper;
    private final ResearchProjectMapper researchProjectMapper;
    private final VisitRecordMapper visitRecordMapper;

    /**
     * 获取系统概览数据
     * 
     * @return 系统概览数据
     */
    @Override
    @Transactional(readOnly = true)
    public SystemOverviewDTO getSystemOverview() {
        // 获取各项统计数据
        Long userCount = userMapper.selectCount(null);
        Long intellectualPropertyCount = intellectualPropertyMapper.selectCount(null);
        Long awardCount = awardMapper.selectCount(null);
        Long projectCount = researchProjectMapper.selectCount(null);
        Long visitCount = visitRecordMapper.selectCount(null);

        // 构建统计数据对象
        SystemStatsDTO stats = SystemStatsDTO.builder()
                .userCount(userCount.intValue())
                .intellectualPropertyCount(intellectualPropertyCount.intValue())
                .awardCount(awardCount.intValue())
                .projectCount(projectCount.intValue())
                .visitCount(visitCount.intValue())
                .build();

        // 构建并返回系统概览数据对象
        return SystemOverviewDTO.builder()
                .stats(stats)
                .build();
    }
} 