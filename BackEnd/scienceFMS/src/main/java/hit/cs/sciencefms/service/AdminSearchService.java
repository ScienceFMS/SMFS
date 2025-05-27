package hit.cs.sciencefms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import hit.cs.sciencefms.dto.SearchResultDTO;

/**
 * 管理员多维查询服务接口
 */
public interface AdminSearchService {
    
    /**
     * 多维查询科研成果
     * 
     * @param types 成果类型列表，逗号分隔，如：intellectualProperty,award,researchProject
     * @param teacherId 教师ID（可选）
     * @param startYear 起始年份（可选）
     * @param endYear 结束年份（可选）
     * @param keyword 关键词（可选）
     * @param sort 排序方式（可选，默认按时间降序）
     * @param page 页码（默认1）
     * @param pageSize 每页大小（默认10）
     * @return 查询结果
     */
    IPage<SearchResultDTO> searchResults(String types, Long teacherId, Integer startYear, Integer endYear, 
            String keyword, String sort, Integer page, Integer pageSize);
    
    /**
     * 导出查询结果为Excel
     * 
     * @param types 成果类型列表，逗号分隔
     * @param teacherId 教师ID（可选）
     * @param startYear 起始年份（可选）
     * @param endYear 结束年份（可选）
     * @param keyword 关键词（可选）
     * @param sort 排序方式（可选）
     * @return Excel文件的字节数组
     */
    byte[] exportSearchResults(String types, Long teacherId, Integer startYear, Integer endYear, 
            String keyword, String sort) throws Exception;
} 