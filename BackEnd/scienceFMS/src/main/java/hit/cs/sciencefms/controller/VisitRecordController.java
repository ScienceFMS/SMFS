package hit.cs.sciencefms.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import hit.cs.sciencefms.common.Result;
import hit.cs.sciencefms.dto.VisitRecordDTO;
import hit.cs.sciencefms.entity.VisitRecord;
import hit.cs.sciencefms.service.VisitRecordService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 出访记录控制器
 */
@RestController
@RequestMapping("/visits")
public class VisitRecordController {
    
    @Autowired
    private VisitRecordService visitRecordService;
    
    /**
     * 根据教师ID获取所有出访记录
     *
     * @param teacherId 教师ID
     * @return 出访记录列表
     */
    @GetMapping("/teacher/{teacherId}")
    public Result<List<VisitRecordDTO>> getVisitRecordsByTeacherId(@PathVariable("teacherId") Long teacherId) {
        List<VisitRecord> visitList = visitRecordService.getVisitRecordsByTeacherId(teacherId);
        List<VisitRecordDTO> dtoList = visitList.stream().map(visit -> {
            VisitRecordDTO dto = new VisitRecordDTO();
            BeanUtils.copyProperties(visit, dto);
            return dto;
        }).collect(Collectors.toList());
        
        return Result.success(dtoList);
    }
    
    /**
     * 分页查询教师的出访记录
     *
     * @param teacherId 教师ID
     * @param page 页码
     * @param pageSize 每页数量
     * @param year 年份筛选（可选）
     * @param keyword 关键词搜索
     * @return 分页结果
     */
    @GetMapping("/page")
    public Result<Page<VisitRecordDTO>> pageVisitRecords(
            @RequestParam("teacherId") Long teacherId,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "keyword", required = false) String keyword) {
        
        Page<VisitRecord> visitPage = visitRecordService.pageVisitRecordsByTeacherId(
                teacherId, page, pageSize, year, keyword);
        
        // 转换为DTO
        Page<VisitRecordDTO> dtoPage = new Page<>(visitPage.getCurrent(), visitPage.getSize(), visitPage.getTotal());
        List<VisitRecordDTO> dtoList = visitPage.getRecords().stream().map(visit -> {
            VisitRecordDTO dto = new VisitRecordDTO();
            BeanUtils.copyProperties(visit, dto);
            return dto;
        }).collect(Collectors.toList());
        
        dtoPage.setRecords(dtoList);
        return Result.success(dtoPage);
    }
    
    /**
     * 根据ID获取出访记录
     *
     * @param id 出访记录ID
     * @return 出访记录
     */
    @GetMapping("/{id}")
    public Result<VisitRecordDTO> getVisitRecord(@PathVariable("id") Long id) {
        VisitRecord visit = visitRecordService.getVisitRecordById(id);
        if (visit == null) {
            return Result.fail("出访记录不存在");
        }
        
        VisitRecordDTO dto = new VisitRecordDTO();
        BeanUtils.copyProperties(visit, dto);
        
        return Result.success(dto);
    }
    
    /**
     * 添加出访记录
     *
     * @param visitDTO 出访记录
     * @return 添加结果
     */
    @PostMapping
    public Result<VisitRecordDTO> addVisitRecord(@RequestBody VisitRecordDTO visitDTO) {
        VisitRecord visit = new VisitRecord();
        BeanUtils.copyProperties(visitDTO, visit);
        
        boolean success = visitRecordService.addVisitRecord(visit);
        if (!success) {
            return Result.fail("添加失败");
        }
        
        visitDTO.setId(visit.getId());
        return Result.success(visitDTO);
    }
    
    /**
     * 更新出访记录
     *
     * @param visitDTO 出访记录
     * @return 更新结果
     */
    @PutMapping
    public Result<Boolean> updateVisitRecord(@RequestBody VisitRecordDTO visitDTO) {
        VisitRecord visit = visitRecordService.getVisitRecordById(visitDTO.getId());
        if (visit == null) {
            return Result.fail("出访记录不存在");
        }
        
        BeanUtils.copyProperties(visitDTO, visit);
        
        boolean success = visitRecordService.updateVisitRecord(visit);
        return success ? Result.success(true) : Result.fail("更新失败");
    }
    
    /**
     * 删除出访记录
     *
     * @param id 出访记录ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteVisitRecord(@PathVariable("id") Long id) {
        boolean success = visitRecordService.deleteVisitRecord(id);
        return success ? Result.success(true) : Result.fail("删除失败");
    }
    
    /**
     * 上传行程单文件
     *
     * @param file 文件
     * @return 上传结果
     */
    @PostMapping("/upload/itinerary")
    public Result<String> uploadItineraryFile(@RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = visitRecordService.uploadItineraryFile(file);
            return Result.success(fileUrl);
        } catch (Exception e) {
            return Result.fail("文件上传失败：" + e.getMessage());
        }
    }
    
    /**
     * 上传成果报告文件
     *
     * @param file 文件
     * @return 上传结果
     */
    @PostMapping("/upload/report")
    public Result<String> uploadReportFile(@RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = visitRecordService.uploadReportFile(file);
            return Result.success(fileUrl);
        } catch (Exception e) {
            return Result.fail("文件上传失败：" + e.getMessage());
        }
    }
    
    /**
     * 生成报销模板
     *
     * @param id 出访记录ID
     * @return Excel文件
     */
    @GetMapping("/{id}/reimbursement-template")
    public ResponseEntity<Resource> generateReimbursementTemplate(@PathVariable("id") Long id) {
        try {
            Resource resource = visitRecordService.generateReimbursementTemplate(id);
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reimbursement_template.xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 统计教师年度出访次数
     *
     * @param teacherId 教师ID
     * @param year 年份，默认当前年份
     * @return 出访统计结果
     */
    @GetMapping("/stats/year")
    public Result<Map<String, Object>> getYearlyVisitStats(
            @RequestParam("teacherId") Long teacherId,
            @RequestParam(value = "year", required = false) Integer year) {
        
        // 默认使用当前年份
        int targetYear = year != null ? year : Year.now().getValue();
        
        // 查询该年度出访记录数
        int count = visitRecordService.countVisitsByYearAndTeacherId(teacherId, targetYear);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("year", targetYear);
        stats.put("visitCount", count);
        
        return Result.success(stats);
    }
} 