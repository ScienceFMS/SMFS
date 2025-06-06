package hit.cs.sciencefms.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import hit.cs.sciencefms.common.Result;
import hit.cs.sciencefms.dto.SearchResultDTO;
import hit.cs.sciencefms.service.AdminSearchService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.io.ByteArrayInputStream;

/**
 * 管理员多维查询控制器
 */
@RestController
@RequestMapping("/admin/search")
public class AdminSearchController {
    
    @Resource
    private AdminSearchService adminSearchService;
    
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
    @GetMapping("/search")
    public Result<IPage<SearchResultDTO>> search(
            @RequestParam(value = "types", required = false) String types,
            @RequestParam(value = "teacherId", required = false) Long teacherId,
            @RequestParam(value = "startYear", required = false) Integer startYear,
            @RequestParam(value = "endYear", required = false) Integer endYear,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "sort", defaultValue = "date_desc") String sort,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        
        // 调用服务层方法进行查询
        IPage<SearchResultDTO> resultPage = adminSearchService.searchResults(
                types, teacherId, startYear, endYear, keyword, sort, page, pageSize);
        
        return Result.success(resultPage);
    }
    
    /**
     * 导出查询结果为Excel
     * 
     * @param types 成果类型列表，逗号分隔
     * @param teacherId 教师ID（可选）
     * @param startYear 起始年份（可选）
     * @param endYear 结束年份（可选）
     * @param keyword 关键词（可选）
     * @param sort 排序方式（可选）
     * @return Excel文件流
     */
    @GetMapping("/export")
    public ResponseEntity<InputStreamResource> exportSearchResults(
            @RequestParam(value = "types", required = false) String types,
            @RequestParam(value = "teacherId", required = false) Long teacherId,
            @RequestParam(value = "startYear", required = false) Integer startYear,
            @RequestParam(value = "endYear", required = false) Integer endYear,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "sort", defaultValue = "date_desc") String sort) {
        
        try {
            // 调用服务层导出方法
            byte[] excelBytes = adminSearchService.exportSearchResults(
                    types, teacherId, startYear, endYear, keyword, sort);
            
            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, 
                    "attachment; filename=science_results_export.xlsx");
            
            // 返回文件流
            InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(excelBytes));
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 