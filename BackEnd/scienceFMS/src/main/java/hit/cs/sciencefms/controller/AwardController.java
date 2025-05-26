package hit.cs.sciencefms.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import hit.cs.sciencefms.common.Result;
import hit.cs.sciencefms.dto.AwardDTO;
import hit.cs.sciencefms.entity.Award;
import hit.cs.sciencefms.service.AwardService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 获奖信息控制器
 */
@RestController
@RequestMapping("/awards")
public class AwardController {
    
    @Resource
    private AwardService awardService;
    
    /**
     * 根据教师ID获取所有获奖信息
     *
     * @param teacherId 教师ID
     * @return 获奖信息列表
     */
    @GetMapping("/teacher/{teacherId}")
    public Result<List<AwardDTO>> getAwardsByTeacherId(@PathVariable("teacherId") Long teacherId) {
        List<Award> awardList = awardService.getAwardsByTeacherId(teacherId);
        List<AwardDTO> dtoList = awardList.stream().map(award -> {
            AwardDTO dto = new AwardDTO();
            BeanUtils.copyProperties(award, dto);
            return dto;
        }).collect(Collectors.toList());
        
        return Result.success(dtoList);
    }
    
    /**
     * 分页查询教师的获奖信息
     *
     * @param teacherId 教师ID
     * @param page 页码
     * @param pageSize 每页数量
     * @param year 年份筛选（可选）
     * @param keyword 关键词搜索
     * @return 分页结果
     */
    @GetMapping("/page")
    public Result<Page<AwardDTO>> pageAwards(
            @RequestParam("teacherId") Long teacherId,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "keyword", required = false) String keyword) {
        
        Page<Award> awardPage = awardService.pageAwardsByTeacherId(teacherId, page, pageSize, year, keyword);
        
        // 转换为DTO
        Page<AwardDTO> dtoPage = new Page<>(awardPage.getCurrent(), awardPage.getSize(), awardPage.getTotal());
        List<AwardDTO> dtoList = awardPage.getRecords().stream().map(award -> {
            AwardDTO dto = new AwardDTO();
            BeanUtils.copyProperties(award, dto);
            return dto;
        }).collect(Collectors.toList());
        
        dtoPage.setRecords(dtoList);
        return Result.success(dtoPage);
    }
    
    /**
     * 根据ID获取获奖信息
     *
     * @param id 获奖信息ID
     * @return 获奖信息
     */
    @GetMapping("/{id}")
    public Result<AwardDTO> getAward(@PathVariable("id") Long id) {
        Award award = awardService.getAwardById(id);
        if (award == null) {
            return Result.fail("获奖信息不存在");
        }
        
        AwardDTO dto = new AwardDTO();
        BeanUtils.copyProperties(award, dto);
        
        return Result.success(dto);
    }
    
    /**
     * 添加获奖信息
     *
     * @param awardDTO 获奖信息
     * @return 添加结果
     */
    @PostMapping
    public Result<AwardDTO> addAward(@RequestBody AwardDTO awardDTO) {
        Award award = new Award();
        BeanUtils.copyProperties(awardDTO, award);
        
        boolean success = awardService.addAward(award);
        if (!success) {
            return Result.fail("添加失败");
        }
        
        awardDTO.setId(award.getId());
        return Result.success(awardDTO);
    }
    
    /**
     * 更新获奖信息
     *
     * @param awardDTO 获奖信息
     * @return 更新结果
     */
    @PutMapping
    public Result<Boolean> updateAward(@RequestBody AwardDTO awardDTO) {
        Award award = awardService.getAwardById(awardDTO.getId());
        if (award == null) {
            return Result.fail("获奖信息不存在");
        }
        
        BeanUtils.copyProperties(awardDTO, award);
        
        boolean success = awardService.updateAward(award);
        return success ? Result.success(true) : Result.fail("更新失败");
    }
    
    /**
     * 删除获奖信息
     *
     * @param id 获奖信息ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteAward(@PathVariable("id") Long id) {
        boolean success = awardService.deleteAward(id);
        return success ? Result.success(true) : Result.fail("删除失败");
    }
    
    /**
     * 导出获奖信息为PDF
     *
     * @param id 获奖信息ID
     * @return PDF文件下载链接
     */
    @GetMapping("/export/{id}")
    public Result<String> exportToPdf(@PathVariable("id") Long id) {
        Award award = awardService.getAwardById(id);
        if (award == null) {
            return Result.fail("获奖信息不存在");
        }
        
        // 这里实现PDF导出功能，返回下载链接
        // 实现逻辑需要根据系统实际PDF生成服务来完成
        String downloadUrl = "/download/award-" + id + ".pdf";
        
        return Result.success(downloadUrl);
    }
} 