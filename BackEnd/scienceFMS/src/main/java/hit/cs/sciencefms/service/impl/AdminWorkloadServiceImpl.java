package hit.cs.sciencefms.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import hit.cs.sciencefms.dto.WorkloadResultDTO;
import hit.cs.sciencefms.entity.*;
import hit.cs.sciencefms.service.*;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import org.apache.logging.log4j.LogManager;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.http.ResponseEntity;

@Service
public class AdminWorkloadServiceImpl implements AdminWorkloadService {

  @Resource
  private TeacherService teacherService;

  @Resource
  private IntellectualPropertyService intellectualPropertyService;

  @Resource
  private AwardService awardService;

  @Resource
  private ResearchProjectService researchProjectService;

  @Resource
  private VisitRecordService visitRecordService;
  @Override
  public IPage<WorkloadResultDTO> getWorkload(Integer startYear, Integer endYear,
                                              Double intellectualPropertyWeight, Double awardWeight, Double researchProjectWeight,Double visitProjectWeight,
                                              Integer page, Integer pageSize) {

    List<Teacher> teacherList = teacherService.list();
    List<WorkloadResultDTO> resultList = new ArrayList<>();

    // 设置日期范围
    LocalDate startDate = (startYear != null) ? LocalDate.of(startYear, 1, 1) : null;
    LocalDate endDate = (endYear != null) ? LocalDate.of(endYear, 12, 31) : null;

    // 遍历教师列表
    for (Teacher teacher : teacherList) {
      WorkloadResultDTO dto = new WorkloadResultDTO();
      dto.setTeacherId(teacher.getId());
      dto.setTeacherName(teacher.getRealName());
      dto.setDepartment(teacher.getDepartment());

      // 查询各项成果数量
      dto.setIntellectualPropertyCount(countIntellectualProperties(teacher.getId(), startDate, endDate));
      dto.setAwardCount(countAwards(teacher.getId(), startDate, endDate));
      dto.setResearchProjectCount(countProjects(teacher.getId(), startDate, endDate));
      dto.setVisitRecordCount(countVisitRecords(teacher.getId(), startDate, endDate));

      // 计算总分（权重可调整）
      dto.setTotalScore(
          dto.getIntellectualPropertyCount() * (intellectualPropertyWeight != null ? intellectualPropertyWeight : 2.0) +
              dto.getAwardCount() * (awardWeight != null ? awardWeight : 3.0) +
              dto.getResearchProjectCount() * (researchProjectWeight != null ? researchProjectWeight : 4.0) +
              dto.getVisitRecordCount() * (visitProjectWeight != null ? visitProjectWeight : 1.5)

      );

      resultList.add(dto);
    }

    // 分页
    int total = resultList.size();
    int fromIndex = Math.min((page - 1) * pageSize, total);
    int toIndex = Math.min(fromIndex + pageSize, total);
    List<WorkloadResultDTO> paged = resultList.subList(fromIndex, toIndex);

    Page<WorkloadResultDTO> resultPage = new Page<>(page, pageSize, total);
    resultPage.setRecords(paged);

    return resultPage;
  }

  @Override
  public byte[] exportWorkload(Integer startYear, Integer endYear,
                               Double intellectualPropertyWeight, Double awardWeight,
                               Double researchProjectWeight, Double visitProjectWeight,
                               Integer page, Integer pageSize) throws Exception {
    // 获取工作量数据
    IPage<WorkloadResultDTO> pageResult = getWorkload(
        startYear, endYear,
        intellectualPropertyWeight, awardWeight, researchProjectWeight, visitProjectWeight,
        page, pageSize
    );
    List<WorkloadResultDTO> list = pageResult.getRecords();

    try (Workbook workbook = new XSSFWorkbook()) {
      Sheet sheet = workbook.createSheet("科研工作量统计");

      // 设置居中样式
      CellStyle centerAlignStyle = workbook.createCellStyle();
      centerAlignStyle.setAlignment(HorizontalAlignment.CENTER);

      // 添加查询条件和时间范围信息
      int rowNum = 0;
      Row queryRow = sheet.createRow(rowNum++);
      queryRow.createCell(0).setCellValue("查询条件");
      queryRow.createCell(1).setCellValue("起始年份：" + (startYear != null ? startYear : "全部"));
      queryRow.createCell(2).setCellValue("结束年份：" + (endYear != null ? endYear : "全部"));
      queryRow.createCell(3).setCellValue("知识产权权重：" + (intellectualPropertyWeight != null ? intellectualPropertyWeight : "默认"));
      queryRow.createCell(4).setCellValue("获奖权重：" + (awardWeight != null ? awardWeight : "默认"));
      queryRow.createCell(5).setCellValue("科研项目权重：" + (researchProjectWeight != null ? researchProjectWeight : "默认"));
      queryRow.createCell(6).setCellValue("访问项目权重：" + (visitProjectWeight != null ? visitProjectWeight : "默认"));

      // 应用居中对齐样式到查询条件行
      for (int i = 0; i < 7; i++) {
        queryRow.getCell(i).setCellStyle(centerAlignStyle);
      }

      // 设置表头
      Row header = sheet.createRow(rowNum++);
      String[] headers = {"教师姓名", "部门", "知识产权数量", "获奖数量", "科研项目数量", "工作量得分"};
      for (int i = 0; i < headers.length; i++) {
        header.createCell(i).setCellValue(headers[i]);
      }

      // 设置表格数据
      for (WorkloadResultDTO dto : list) {
        Row dataRow = sheet.createRow(rowNum++);
        dataRow.createCell(0).setCellValue(dto.getTeacherName());
        dataRow.createCell(1).setCellValue(dto.getDepartment());
        dataRow.createCell(2).setCellValue(dto.getIntellectualPropertyCount());
        dataRow.createCell(3).setCellValue(dto.getAwardCount());
        dataRow.createCell(4).setCellValue(dto.getResearchProjectCount());
        dataRow.createCell(5).setCellValue(dto.getTotalScore());
      }

      // 调整列宽：让每列显示大约 12 个字符
      for (int i = 0; i < headers.length; i++) {
        sheet.setColumnWidth(i, 256 * 12); // 每列宽度调整为 12 个字符的宽度
      }

      // 设置样式：表头加粗
      CellStyle headerStyle = workbook.createCellStyle();
      Font headerFont = workbook.createFont();
      headerFont.setBold(true);
      headerStyle.setFont(headerFont);
      headerStyle.setAlignment(HorizontalAlignment.CENTER);

      for (int i = 0; i < headers.length; i++) {
        header.getCell(i).setCellStyle(headerStyle);
      }

      // 设置表格数据的对齐方式
      CellStyle dataStyle = workbook.createCellStyle();
      dataStyle.setAlignment(HorizontalAlignment.CENTER);

      for (int rowIndex = 1; rowIndex <= list.size(); rowIndex++) {
        Row row = sheet.getRow(rowIndex);
        for (int colIndex = 0; colIndex < headers.length; colIndex++) {
          row.getCell(colIndex).setCellStyle(dataStyle);
        }
      }

      // 输出 Excel 文件
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      workbook.write(out);
      return out.toByteArray();
    } catch (Exception e) {
      // 异常捕获：记录日志
      Logger log = LogManager.getLogger(getClass());
      log.error("导出科研工作量统计失败: ", e);
      throw new Exception("导出失败，请检查系统设置");
    }
  }






  // 以下是工作量统计相关方法
  private int countIntellectualProperties(Long teacherId, LocalDate start, LocalDate end) {
    LambdaQueryWrapper<IntellectualProperty> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(IntellectualProperty::getTeacherId, teacherId);
    if (start != null && end != null) {
      wrapper.and(w -> w.between(IntellectualProperty::getAuthDate, start, end)
          .or().between(IntellectualProperty::getApplyDate, start, end));
    }
    return (int) intellectualPropertyService.count(wrapper);
  }

  private int countAwards(Long teacherId, LocalDate start, LocalDate end) {
    LambdaQueryWrapper<Award> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(Award::getTeacherId, teacherId);
    if (start != null && end != null) {
      wrapper.between(Award::getAwardDate, start, end);
    }
    return (int) awardService.count(wrapper);
  }

  private int countProjects(Long teacherId, LocalDate start, LocalDate end) {
    LambdaQueryWrapper<ResearchProject> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(ResearchProject::getTeacherId, teacherId);
    if (start != null && end != null) {
      wrapper.and(w -> w.between(ResearchProject::getStartDate, start, end)
          .or().between(ResearchProject::getEndDate, start, end));
    }
    return (int) researchProjectService.count(wrapper);
  }

  private int countVisitRecords(Long teacherId, LocalDate start, LocalDate end) {
    LambdaQueryWrapper<VisitRecord> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(VisitRecord::getTeacherId, teacherId);

    if (start != null && end != null) {
      wrapper.and(w -> w.between(VisitRecord::getStartDate, start, end)
          .or().between(VisitRecord::getEndDate, start, end));
    }

    return (int) visitRecordService.count(wrapper);
  }

}
