package hit.cs.sciencefms.service;

import hit.cs.sciencefms.dto.SystemOverviewDTO;

/**
 * 管理员仪表盘服务接口
 */
public interface AdminDashboardService {
    
    /**
     * 获取系统概览数据
     * 
     * @return 系统概览数据
     */
    SystemOverviewDTO getSystemOverview();
} 