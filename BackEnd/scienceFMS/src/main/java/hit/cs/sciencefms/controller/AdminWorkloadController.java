package hit.cs.sciencefms.controller;

import ch.qos.logback.classic.Logger;
import com.baomidou.mybatisplus.core.metadata.IPage;
import hit.cs.sciencefms.common.Result;
import hit.cs.sciencefms.dto.WorkloadResultDTO;
import hit.cs.sciencefms.service.AdminWorkloadService;
import jakarta.annotation.Resource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;

/**
 * 管理员科研工作量统计控制器
 */
@RestController
@RequestMapping("/admin/workload")
public class AdminWorkloadController {

  @Resource
  private AdminWorkloadService adminWorkloadService;

  /**
   * 查询科研工作量统计信息（分页）
   *
   * @param startYear 起始年份（可选，默认所有年份）
   * @param endYear 结束年份（可选，默认所有年份）
   * @param intellectualPropertyWeight 知识产权的积分权重（可选，默认1.0）
   * @param awardWeight 奖项的积分权重（可选，默认1.5）
   * @param researchProjectWeight 科研项目的积分权重（可选，默认2.0）
   * @param page 页码（默认1）
   * @param pageSize 每页大小（默认10）
   * @return 查询结果（分页后的科研工作量统计信息）
   */
  @GetMapping("/query")
  public Result<IPage<WorkloadResultDTO>> queryWorkload(
      @RequestParam(value = "startYear", required = false) Integer startYear,
      @RequestParam(value = "endYear", required = false) Integer endYear,
      @RequestParam(value = "intellectualPropertyWeight", required = false) Double intellectualPropertyWeight,
      @RequestParam(value = "awardWeight", required = false) Double awardWeight,
      @RequestParam(value = "researchProjectWeight", required = false) Double researchProjectWeight,
      @RequestParam(value = "visitProjectWeight", required = false) Double visitProjectWeight,
      @RequestParam(value = "page", defaultValue = "1") Integer page,
      @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

    // 调用修改后的 getWorkload 方法
    IPage<WorkloadResultDTO> resultPage = adminWorkloadService.getWorkload(
        startYear, endYear, intellectualPropertyWeight, awardWeight, researchProjectWeight,visitProjectWeight, page, pageSize
    );

    return Result.success(resultPage);
  }


  /**
   * 查询科研工作量统计信息（分页）
   *
   * @param startYear 起始年份（可选，默认所有年份）
   * @param endYear 结束年份（可选，默认所有年份）
   * @param intellectualPropertyWeight 知识产权的积分权重（可选，默认1.0）
   * @param awardWeight 奖项的积分权重（可选，默认1.5）
   * @param researchProjectWeight 科研项目的积分权重（可选，默认2.0）
   * @param page 页码（默认1）
   * @param pageSize 每页大小（默认10）
   * @return 查询结果（分页后的科研工作量统计信息）
   */
  @GetMapping("/export")
  public ResponseEntity<InputStreamResource> exportWorkload(
      @RequestParam(value = "startYear", required = false) Integer startYear,
      @RequestParam(value = "endYear", required = false) Integer endYear,
      @RequestParam(value = "intellectualPropertyWeight", required = false) Double intellectualPropertyWeight,
      @RequestParam(value = "awardWeight", required = false) Double awardWeight,
      @RequestParam(value = "researchProjectWeight", required = false) Double researchProjectWeight,
      @RequestParam(value = "visitProjectWeight", required = false) Double visitProjectWeight,
      @RequestParam(value = "page", defaultValue = "1") Integer page,
      @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
    try {
      byte[] excelBytes = adminWorkloadService.exportWorkload( startYear, endYear, intellectualPropertyWeight, awardWeight, researchProjectWeight,visitProjectWeight, page, pageSize
      );

      HttpHeaders headers = new HttpHeaders();
      headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=workload_export.xlsx");

      InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(excelBytes));

      return ResponseEntity.ok()
          .headers(headers)
          .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
          .body(resource);

    } catch (Exception e) {
      Logger log = null;
      log.error("导出科研工作量统计失败: ", e);
      return ResponseEntity.badRequest().build();
    }
  }
}
