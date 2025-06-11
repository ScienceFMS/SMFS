package hit.cs.sciencefms.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import hit.cs.sciencefms.common.Result;
import hit.cs.sciencefms.dto.IntellectualPropertyDTO;
import hit.cs.sciencefms.entity.IntellectualProperty;
import hit.cs.sciencefms.service.IntellectualPropertyService;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 知识产权控制器
 */
@RestController
@RequestMapping("/intellectual-property")
public class IntellectualPropertyController {
    
    @Resource
    private IntellectualPropertyService intellectualPropertyService;
    
    /**
     * 分页查询知识产权
     * 
     * @param teacherId 教师ID
     * @param page 页码
     * @param pageSize 每页数量
     * @param type 类型筛选（可选，专利/著作权）
     * @param keyword 关键词搜索
     * @return 分页结果
     */
    @GetMapping("/page")
    public Result<Page<IntellectualPropertyDTO>> pageProperties(
            @RequestParam("teacherId") Long teacherId,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "keyword", required = false) String keyword) {
        
        Page<IntellectualProperty> propertyPage = intellectualPropertyService.pagePropertiesByTeacherId(
                teacherId, page, pageSize, type, keyword);
        
        // 转换为DTO
        Page<IntellectualPropertyDTO> dtoPage = new Page<>(propertyPage.getCurrent(), propertyPage.getSize(), propertyPage.getTotal());
        List<IntellectualPropertyDTO> dtoList = propertyPage.getRecords().stream().map(property -> {
            IntellectualPropertyDTO dto = new IntellectualPropertyDTO();
            BeanUtils.copyProperties(property, dto);
            return dto;
        }).collect(Collectors.toList());
        
        dtoPage.setRecords(dtoList);
        return Result.success(dtoPage);
    }
    
    /**
     * 根据ID获取知识产权
     * 
     * @param id 知识产权ID
     * @return 知识产权信息
     */
    @GetMapping("/{id}")
    public Result<IntellectualPropertyDTO> getProperty(@PathVariable("id") Long id) {
        IntellectualProperty property = intellectualPropertyService.getPropertyById(id);
        if (property == null) {
            return Result.fail("知识产权不存在");
        }
        
        IntellectualPropertyDTO dto = new IntellectualPropertyDTO();
        BeanUtils.copyProperties(property, dto);
        
        return Result.success(dto);
    }
    
    /**
     * 添加知识产权
     * 
     * @param propertyDTO 知识产权信息
     * @return 添加结果
     */
    @PostMapping
    public Result<IntellectualPropertyDTO> addProperty(@RequestBody IntellectualPropertyDTO propertyDTO) {
        IntellectualProperty property = new IntellectualProperty();
        BeanUtils.copyProperties(propertyDTO, property);
        
        boolean success = intellectualPropertyService.addProperty(property);
        if (!success) {
            return Result.fail("添加失败");
        }
        
        propertyDTO.setId(property.getId());
        return Result.success(propertyDTO);
    }
    
    /**
     * 更新知识产权
     * 
     * @param propertyDTO 知识产权信息
     * @return 更新结果
     */
    @PutMapping
    public Result<Boolean> updateProperty(@RequestBody IntellectualPropertyDTO propertyDTO) {
        IntellectualProperty property = intellectualPropertyService.getPropertyById(propertyDTO.getId());
        if (property == null) {
            return Result.fail("知识产权不存在");
        }
        
        BeanUtils.copyProperties(propertyDTO, property);
        
        boolean success = intellectualPropertyService.updateProperty(property);
        return success ? Result.success(true) : Result.fail("更新失败");
    }
    
    /**
     * 删除知识产权
     * 
     * @param id 知识产权ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteProperty(@PathVariable("id") Long id) {
        boolean success = intellectualPropertyService.deleteProperty(id);
        return success ? Result.success(true) : Result.fail("删除失败");
    }
    
    /**
     * 批量导出知识产权
     * 
     * @param teacherId 教师ID
     * @param type 类型筛选（可选）
     * @param keyword 关键词搜索（可选）
     * @return 导出文件
     */
    @GetMapping("/export")
    public ResponseEntity<InputStreamResource> exportProperties(
            @RequestParam("teacherId") Long teacherId,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "keyword", required = false) String keyword) {
        
        // 这里应该是实际的Excel生成逻辑
        // 简化版仅返回一个空的Excel文件作为示例
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            // 使用POI或其他Excel库生成文件
            // 这里省略实际生成逻辑
            
            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=intellectual_properties.xlsx");
            
            // 返回文件流
            InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(outputStream.toByteArray()));
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 