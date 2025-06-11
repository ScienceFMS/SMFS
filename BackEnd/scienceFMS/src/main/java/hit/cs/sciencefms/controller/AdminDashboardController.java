package hit.cs.sciencefms.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import hit.cs.sciencefms.common.Result;
import hit.cs.sciencefms.dto.SystemOverviewDTO;
import hit.cs.sciencefms.service.AdminDashboardService;
import hit.cs.sciencefms.service.ResearchSummaryService;
import lombok.RequiredArgsConstructor;

/**
 * 管理员仪表盘控制器
 */
@RestController
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;
    private final ResearchSummaryService researchSummaryService;

    /**
     * 获取系统概览数据
     * 
     * @return 系统概览数据
     */
    @GetMapping("/overview")
    public Result<SystemOverviewDTO> getSystemOverview() {
        SystemOverviewDTO overview = adminDashboardService.getSystemOverview();
        return Result.success(overview);
    }
    
    /**
     * 获取科研成果分析报告
     * 
     * @param startYear 开始年份
     * @param endYear 结束年份
     * @return 分析结果（包含趋势分析、高价值成果排行和学科分布）
     */
    @GetMapping("/research-analysis")
    public Result<String> getResearchAnalysis(@RequestParam(defaultValue = "2000") Integer startYear, 
                                              @RequestParam(required = false) Integer endYear) {
        // 如果结束年份为空，则使用当前年份
        if (endYear == null) {
            endYear = java.time.LocalDate.now().getYear();
        }
        
        String analysisResult = researchSummaryService.generateResearchAnalysis(startYear, endYear);
        return Result.success(analysisResult);
    }
} 