package hit.cs.sciencefms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import hit.cs.sciencefms.dto.SearchResultDTO;
import hit.cs.sciencefms.entity.Award;
import hit.cs.sciencefms.entity.IntellectualProperty;
import hit.cs.sciencefms.entity.ResearchProject;
import hit.cs.sciencefms.entity.Teacher;
import hit.cs.sciencefms.service.AdminSearchService;
import hit.cs.sciencefms.service.AwardService;
import hit.cs.sciencefms.service.IntellectualPropertyService;
import hit.cs.sciencefms.service.ResearchProjectService;
import hit.cs.sciencefms.service.TeacherService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 管理员多维查询服务实现类
 */
@Service
public class AdminSearchServiceImpl implements AdminSearchService {
    
    @Resource
    private IntellectualPropertyService intellectualPropertyService;
    
    @Resource
    private AwardService awardService;
    
    @Resource
    private ResearchProjectService researchProjectService;
    
    @Resource
    private TeacherService teacherService;
    
    @Override
    public IPage<SearchResultDTO> searchResults(String types, Long teacherId, Integer startYear, Integer endYear,
                                               String keyword, String sort, Integer page, Integer pageSize) {
        
        // 解析要查询的类型
        List<String> typeList = new ArrayList<>();
        if (StringUtils.hasText(types)) {
            typeList = Arrays.asList(types.split(","));
        } else {
            // 默认查询所有类型
            typeList = Arrays.asList("intellectualProperty", "award", "researchProject");
        }
        
        // 所有符合条件的结果
        List<SearchResultDTO> allResults = new ArrayList<>();
        
        // 获取教师信息缓存，避免重复查询
        Map<Long, Teacher> teacherMap = new HashMap<>();
        
        // 根据选择的类型进行查询
        if (typeList.contains("intellectualProperty")) {
            allResults.addAll(queryIntellectualProperties(teacherId, startYear, endYear, keyword, teacherMap));
        }
        
        if (typeList.contains("award")) {
            allResults.addAll(queryAwards(teacherId, startYear, endYear, keyword, teacherMap));
        }
        
        if (typeList.contains("researchProject")) {
            allResults.addAll(queryResearchProjects(teacherId, startYear, endYear, keyword, teacherMap));
        }
        
        // 根据排序条件排序
        sortResults(allResults, sort);
        
        // 手动分页
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, allResults.size());
        
        // 创建分页结果对象
        Page<SearchResultDTO> resultPage = new Page<>(page, pageSize, allResults.size());
        
        // 设置当前页的记录
        if (start < allResults.size()) {
            resultPage.setRecords(allResults.subList(start, end));
        } else {
            resultPage.setRecords(new ArrayList<>());
        }
        
        return resultPage;
    }
    
    @Override
    public byte[] exportSearchResults(String types, Long teacherId, Integer startYear, Integer endYear,
                                      String keyword, String sort) throws Exception {
        
        // 不分页获取所有符合条件的结果
        IPage<SearchResultDTO> resultPage = searchResults(types, teacherId, startYear, endYear, keyword, sort, 1, Integer.MAX_VALUE);
        List<SearchResultDTO> results = resultPage.getRecords();
        
        // 创建Excel工作簿
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("科研成果查询结果");
            
            // 创建表头行
            Row headerRow = sheet.createRow(0);
            String[] headers = {
                    "成果类型", "标题/名称", "教师姓名", "部门", "类型", "子类型", 
                    "日期", "编号/等级", "相关单位", "角色/排名"
            };
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }
            
            // 填充数据行
            int rowNum = 1;
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            
            for (SearchResultDTO result : results) {
                Row row = sheet.createRow(rowNum++);
                
                // 填充单元格
                row.createCell(0).setCellValue(getEntityTypeLabel(result.getEntityType()));
                row.createCell(1).setCellValue(result.getTitle());
                row.createCell(2).setCellValue(result.getTeacherName());
                row.createCell(3).setCellValue(result.getDepartment());
                row.createCell(4).setCellValue(result.getType());
                row.createCell(5).setCellValue(result.getSubtype());
                
                String dateStr = "";
                if (result.getMainDate() != null) {
                    dateStr = result.getMainDate().format(dateFormatter);
                } else if (result.getDate1() != null && result.getDate2() != null) {
                    dateStr = result.getDate1().format(dateFormatter) + " 至 " + 
                             result.getDate2().format(dateFormatter);
                } else if (result.getDate1() != null) {
                    dateStr = result.getDate1().format(dateFormatter);
                }
                row.createCell(6).setCellValue(dateStr);
                
                // 根据不同实体类型，填充不同的字段
                if ("intellectualProperty".equals(result.getEntityType())) {
                    row.createCell(7).setCellValue(result.getCode());  // 授权号
                    row.createCell(8).setCellValue("");
                    row.createCell(9).setCellValue(result.getRole());  // 发明人排名
                } else if ("award".equals(result.getEntityType())) {
                    row.createCell(7).setCellValue(result.getLevel());  // 获奖等级
                    row.createCell(8).setCellValue(result.getOrganization());  // 颁发单位
                    row.createCell(9).setCellValue("");
                } else if ("researchProject".equals(result.getEntityType())) {
                    row.createCell(7).setCellValue(result.getCode());  // 项目编号
                    row.createCell(8).setCellValue(result.getOrganization());  // 项目来源
                    row.createCell(9).setCellValue(result.getRole());  // 项目角色
                }
            }
            
            // 自动调整列宽
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // 写入字节流
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }
    
    /**
     * 查询知识产权
     */
    private List<SearchResultDTO> queryIntellectualProperties(Long teacherId, Integer startYear, Integer endYear,
                                                            String keyword, Map<Long, Teacher> teacherMap) {
        LambdaQueryWrapper<IntellectualProperty> queryWrapper = new LambdaQueryWrapper<>();
        
        // 教师条件
        if (teacherId != null) {
            queryWrapper.eq(IntellectualProperty::getTeacherId, teacherId);
        }
        
        // 年份条件（优先使用授权日期，其次使用申请日期）
        if (startYear != null || endYear != null) {
            LocalDate startDate = startYear != null ? LocalDate.of(startYear, 1, 1) : null;
            LocalDate endDate = endYear != null ? LocalDate.of(endYear, 12, 31) : null;
            
            queryWrapper.and(wrapper -> {
                if (startDate != null && endDate != null) {
                    // 授权日期在范围内，或申请日期在范围内
                    wrapper.and(w -> w.between(IntellectualProperty::getAuthDate, startDate, endDate)
                            .or()
                            .between(IntellectualProperty::getApplyDate, startDate, endDate));
                } else if (startDate != null) {
                    wrapper.and(w -> w.ge(IntellectualProperty::getAuthDate, startDate)
                            .or()
                            .ge(IntellectualProperty::getApplyDate, startDate));
                } else {
                    wrapper.and(w -> w.le(IntellectualProperty::getAuthDate, endDate)
                            .or()
                            .le(IntellectualProperty::getApplyDate, endDate));
                }
            });
        }
        
        // 关键词搜索
        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(wrapper -> wrapper.like(IntellectualProperty::getTitle, keyword)
                    .or()
                    .like(IntellectualProperty::getType, keyword)
                    .or()
                    .like(IntellectualProperty::getSubtype, keyword)
                    .or()
                    .like(IntellectualProperty::getAuthNumber, keyword));
        }
        
        // 查询所有符合条件的知识产权
        List<IntellectualProperty> properties = intellectualPropertyService.list(queryWrapper);
        
        // 转换为统一的DTO
        return properties.stream().map(property -> {
            SearchResultDTO dto = new SearchResultDTO();
            dto.setId(property.getId());
            dto.setEntityType("intellectualProperty");
            dto.setTeacherId(property.getTeacherId());
            
            // 获取教师信息
            Teacher teacher = getTeacherInfo(property.getTeacherId(), teacherMap);
            if (teacher != null) {
                dto.setTeacherName(teacher.getRealName());
                dto.setDepartment(teacher.getDepartment());
            }
            
            dto.setTitle(property.getTitle());
            // 优先使用授权日期作为主要日期
            dto.setMainDate(property.getAuthDate() != null ? property.getAuthDate() : property.getApplyDate());
            dto.setDate1(property.getApplyDate()); // 申请日期
            dto.setDate2(property.getAuthDate()); // 授权日期
            dto.setType(property.getType()); // 类型（专利/著作权）
            dto.setSubtype(property.getSubtype()); // 子类型
            dto.setCode(property.getAuthNumber()); // 授权号
            
            // 发明人排名作为角色存储
            if (property.getInventorRank() != null) {
                dto.setRole("发明人排名: " + property.getInventorRank());
            }
            
            return dto;
        }).collect(Collectors.toList());
    }
    
    /**
     * 查询获奖信息
     */
    private List<SearchResultDTO> queryAwards(Long teacherId, Integer startYear, Integer endYear,
                                           String keyword, Map<Long, Teacher> teacherMap) {
        LambdaQueryWrapper<Award> queryWrapper = new LambdaQueryWrapper<>();
        
        // 教师条件
        if (teacherId != null) {
            queryWrapper.eq(Award::getTeacherId, teacherId);
        }
        
        // 年份条件
        if (startYear != null || endYear != null) {
            LocalDate startDate = startYear != null ? LocalDate.of(startYear, 1, 1) : null;
            LocalDate endDate = endYear != null ? LocalDate.of(endYear, 12, 31) : null;
            
            if (startDate != null && endDate != null) {
                queryWrapper.between(Award::getAwardDate, startDate, endDate);
            } else if (startDate != null) {
                queryWrapper.ge(Award::getAwardDate, startDate);
            } else {
                queryWrapper.le(Award::getAwardDate, endDate);
            }
        }
        
        // 关键词搜索
        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(wrapper -> wrapper.like(Award::getAwardName, keyword)
                    .or()
                    .like(Award::getAwardLevel, keyword)
                    .or()
                    .like(Award::getIssuingUnit, keyword));
        }
        
        // 查询所有符合条件的获奖信息
        List<Award> awards = awardService.list(queryWrapper);
        
        // 转换为统一的DTO
        return awards.stream().map(award -> {
            SearchResultDTO dto = new SearchResultDTO();
            dto.setId(award.getId());
            dto.setEntityType("award");
            dto.setTeacherId(award.getTeacherId());
            
            // 获取教师信息
            Teacher teacher = getTeacherInfo(award.getTeacherId(), teacherMap);
            if (teacher != null) {
                dto.setTeacherName(teacher.getRealName());
                dto.setDepartment(teacher.getDepartment());
            }
            
            dto.setTitle(award.getAwardName());
            dto.setMainDate(award.getAwardDate());
            dto.setDate1(award.getAwardDate());
            dto.setLevel(award.getAwardLevel());
            dto.setType("奖项");
            dto.setOrganization(award.getIssuingUnit());
            
            return dto;
        }).collect(Collectors.toList());
    }
    
    /**
     * 查询科研项目
     */
    private List<SearchResultDTO> queryResearchProjects(Long teacherId, Integer startYear, Integer endYear,
                                                    String keyword, Map<Long, Teacher> teacherMap) {
        LambdaQueryWrapper<ResearchProject> queryWrapper = new LambdaQueryWrapper<>();
        
        // 教师条件
        if (teacherId != null) {
            queryWrapper.eq(ResearchProject::getTeacherId, teacherId);
        }
        
        // 年份条件（项目开始日期或结束日期在范围内）
        if (startYear != null || endYear != null) {
            LocalDate startDate = startYear != null ? LocalDate.of(startYear, 1, 1) : null;
            LocalDate endDate = endYear != null ? LocalDate.of(endYear, 12, 31) : null;
            
            queryWrapper.and(wrapper -> {
                if (startDate != null && endDate != null) {
                    // 项目开始日期或结束日期在范围内
                    wrapper.and(w -> w.between(ResearchProject::getStartDate, startDate, endDate)
                            .or()
                            .between(ResearchProject::getEndDate, startDate, endDate)
                            // 或者跨越整个范围（开始日期<startDate且结束日期>endDate）
                            .or()
                            .le(ResearchProject::getStartDate, startDate)
                            .ge(ResearchProject::getEndDate, endDate));
                } else if (startDate != null) {
                    // 结束日期 >= startDate
                    wrapper.ge(ResearchProject::getEndDate, startDate);
                } else {
                    // 开始日期 <= endDate
                    wrapper.le(ResearchProject::getStartDate, endDate);
                }
            });
        }
        
        // 关键词搜索
        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(wrapper -> wrapper.like(ResearchProject::getProjectName, keyword)
                    .or()
                    .like(ResearchProject::getProjectCode, keyword)
                    .or()
                    .like(ResearchProject::getSource, keyword)
                    .or()
                    .like(ResearchProject::getRole, keyword));
        }
        
        // 查询所有符合条件的科研项目
        List<ResearchProject> projects = researchProjectService.list(queryWrapper);
        
        // 转换为统一的DTO
        return projects.stream().map(project -> {
            SearchResultDTO dto = new SearchResultDTO();
            dto.setId(project.getId());
            dto.setEntityType("researchProject");
            dto.setTeacherId(project.getTeacherId());
            
            // 获取教师信息
            Teacher teacher = getTeacherInfo(project.getTeacherId(), teacherMap);
            if (teacher != null) {
                dto.setTeacherName(teacher.getRealName());
                dto.setDepartment(teacher.getDepartment());
            }
            
            dto.setTitle(project.getProjectName());
            dto.setMainDate(project.getStartDate()); // 使用开始日期作为主要日期
            dto.setDate1(project.getStartDate());
            dto.setDate2(project.getEndDate());
            dto.setType("科研项目");
            dto.setSubtype(project.getSource());
            dto.setAmount(project.getFunding());
            dto.setCode(project.getProjectCode());
            dto.setRole(project.getRole());
            dto.setOrganization(project.getSource()); // 项目来源也可视为组织
            
            return dto;
        }).collect(Collectors.toList());
    }
    
    /**
     * 根据排序条件对结果进行排序
     */
    private void sortResults(List<SearchResultDTO> results, String sort) {
        if ("date_desc".equals(sort)) {
            results.sort(Comparator.comparing(SearchResultDTO::getMainDate, 
                    Comparator.nullsLast(Comparator.reverseOrder())));
        } else if ("date_asc".equals(sort)) {
            results.sort(Comparator.comparing(SearchResultDTO::getMainDate, 
                    Comparator.nullsLast(Comparator.naturalOrder())));
        } else if ("name_asc".equals(sort)) {
            results.sort(Comparator.comparing(SearchResultDTO::getTitle, 
                    Comparator.nullsLast(Comparator.naturalOrder())));
        } else if ("name_desc".equals(sort)) {
            results.sort(Comparator.comparing(SearchResultDTO::getTitle, 
                    Comparator.nullsLast(Comparator.reverseOrder())));
        }
    }
    
    /**
     * 获取教师信息，先从缓存中查询，不存在则从数据库获取
     */
    private Teacher getTeacherInfo(Long teacherId, Map<Long, Teacher> teacherMap) {
        if (teacherId == null) {
            return null;
        }
        
        Teacher teacher = teacherMap.get(teacherId);
        if (teacher == null) {
            teacher = teacherService.getById(teacherId);
            if (teacher != null) {
                teacherMap.put(teacherId, teacher);
            }
        }
        return teacher;
    }
    
    /**
     * 获取实体类型的中文标签
     */
    private String getEntityTypeLabel(String entityType) {
        if ("intellectualProperty".equals(entityType)) {
            return "知识产权";
        } else if ("award".equals(entityType)) {
            return "奖项";
        } else if ("researchProject".equals(entityType)) {
            return "科研项目";
        }
        return entityType;
    }
} 