package hit.cs.sciencefms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 系统概览数据DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemOverviewDTO {
    /**
     * 统计数据
     */
    private SystemStatsDTO stats;

    /**
     * 统计数据内部类
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SystemStatsDTO {
        /**
         * 用户数量
         */
        private Integer userCount;
        
        /**
         * 知识产权数量
         */
        private Integer intellectualPropertyCount;
        
        /**
         * 奖项数量
         */
        private Integer awardCount;
        
        /**
         * 科研项目数量
         */
        private Integer projectCount;
        
        /**
         * 出访记录数量
         */
        private Integer visitCount;
    }
} 