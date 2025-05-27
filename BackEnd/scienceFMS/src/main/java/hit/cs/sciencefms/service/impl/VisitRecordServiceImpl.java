package hit.cs.sciencefms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import hit.cs.sciencefms.entity.VisitRecord;
import hit.cs.sciencefms.mapper.VisitRecordMapper;
import hit.cs.sciencefms.service.VisitRecordService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * 出访记录服务实现类
 */
@Service
@Slf4j
public class VisitRecordServiceImpl extends ServiceImpl<VisitRecordMapper, VisitRecord> implements VisitRecordService {

    @Value("${file.upload.path:/uploads}")
    private String uploadPath;

    @Override
    public List<VisitRecord> getVisitRecordsByTeacherId(Long teacherId) {
        LambdaQueryWrapper<VisitRecord> wrapper = Wrappers.<VisitRecord>lambdaQuery()
                .eq(VisitRecord::getTeacherId, teacherId)
                .orderByDesc(VisitRecord::getStartDate);
        return list(wrapper);
    }

    @Override
    public Page<VisitRecord> pageVisitRecordsByTeacherId(Long teacherId, int page, int pageSize, Integer year, String keyword) {
        Page<VisitRecord> pageParam = new Page<>(page, pageSize);
        
        LambdaQueryWrapper<VisitRecord> wrapper = Wrappers.<VisitRecord>lambdaQuery()
                .eq(VisitRecord::getTeacherId, teacherId);
        
        // 按年份筛选
        if (year != null) {
            // 查询指定年份的记录
            wrapper.and(w ->
                    // 开始日期在这一年
                    w.apply("EXTRACT(YEAR FROM start_date) = {0}", year)
                    // 或结束日期在这一年
                    .or().apply("EXTRACT(YEAR FROM end_date) = {0}", year)
                    // 或跨越这一年(开始日期早于这一年，结束日期晚于这一年)
                    .or().and(ww ->
                            ww.apply("EXTRACT(YEAR FROM start_date) < {0}", year)
                            .apply("EXTRACT(YEAR FROM end_date) > {0}", year)
                    )
            );
        }
        
        // 关键词搜索
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(VisitRecord::getPurpose, keyword)
                    .or()
                    .like(VisitRecord::getLocation, keyword)
                    .or()
                    .like(VisitRecord::getResult, keyword));
        }
        
        wrapper.orderByDesc(VisitRecord::getStartDate);
        
        return page(pageParam, wrapper);
    }

    @Override
    public boolean addVisitRecord(VisitRecord visitRecord) {
        visitRecord.setCreateTime(LocalDateTime.now());
        visitRecord.setUpdateTime(LocalDateTime.now());
        visitRecord.setIsDeleted(0);
        return save(visitRecord);
    }

    @Override
    public boolean updateVisitRecord(VisitRecord visitRecord) {
        visitRecord.setUpdateTime(LocalDateTime.now());
        return updateById(visitRecord);
    }

    @Override
    public boolean deleteVisitRecord(Long id) {
        return removeById(id);
    }

    @Override
    public VisitRecord getVisitRecordById(Long id) {
        return getById(id);
    }

    @Override
    public String uploadItineraryFile(MultipartFile file) throws IOException {
        return uploadFile(file, "itinerary");
    }

    @Override
    public String uploadReportFile(MultipartFile file) throws IOException {
        return uploadFile(file, "report");
    }

    @Override
    public Resource generateReimbursementTemplate(Long visitRecordId) throws IOException {
        VisitRecord visitRecord = getById(visitRecordId);
        if (visitRecord == null) {
            throw new RuntimeException("出访记录不存在");
        }
        
        // 创建Excel工作簿
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("出访报销单");
        
        // 设置列宽
        sheet.setColumnWidth(0, 20 * 256);
        sheet.setColumnWidth(1, 30 * 256);
        
        // 标题样式
        CellStyle titleStyle = workbook.createCellStyle();
        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 16);
        titleStyle.setFont(titleFont);
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        
        // 头部样式
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        
        // 标题行
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("教师出访报销申请单");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 1));
        
        // 基本信息
        Row purposeRow = sheet.createRow(2);
        purposeRow.createCell(0).setCellValue("出访目的");
        purposeRow.createCell(1).setCellValue(visitRecord.getPurpose());
        
        Row locationRow = sheet.createRow(3);
        locationRow.createCell(0).setCellValue("出访地点");
        locationRow.createCell(1).setCellValue(visitRecord.getLocation());
        
        // 日期格式化
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        Row dateRow = sheet.createRow(4);
        dateRow.createCell(0).setCellValue("出访时间");
        dateRow.createCell(1).setCellValue(
                visitRecord.getStartDate().format(formatter) + " 至 " + 
                visitRecord.getEndDate().format(formatter));
        
        Row resultRow = sheet.createRow(5);
        resultRow.createCell(0).setCellValue("出访成果");
        resultRow.createCell(1).setCellValue(visitRecord.getResult());
        
        // 报销明细标题
        Row expenseHeaderRow = sheet.createRow(7);
        expenseHeaderRow.createCell(0).setCellValue("报销项目");
        expenseHeaderRow.createCell(1).setCellValue("金额（元）");
        
        // 报销明细项
        String[] expenseItems = {"交通费", "住宿费", "餐饮费", "会议注册费", "其他费用"};
        for (int i = 0; i < expenseItems.length; i++) {
            Row itemRow = sheet.createRow(8 + i);
            itemRow.createCell(0).setCellValue(expenseItems[i]);
            itemRow.createCell(1).setCellValue("");  // 留空供填写
        }
        
        // 合计行
        Row totalRow = sheet.createRow(8 + expenseItems.length);
        Cell totalLabelCell = totalRow.createCell(0);
        totalLabelCell.setCellValue("合计");
        totalLabelCell.setCellStyle(headerStyle);
        totalRow.createCell(1).setCellValue("");  // 留空供填写
        
        // 申请说明
        Row noteRow = sheet.createRow(10 + expenseItems.length);
        noteRow.createCell(0).setCellValue("申请说明");
        noteRow.createCell(1).setCellValue("");  // 留空供填写
        
        // 签字栏
        Row signatureRow = sheet.createRow(12 + expenseItems.length);
        signatureRow.createCell(0).setCellValue("申请人签字");
        signatureRow.createCell(1).setCellValue("");  // 留空供签字
        
        Row dateSignRow = sheet.createRow(13 + expenseItems.length);
        dateSignRow.createCell(0).setCellValue("日期");
        dateSignRow.createCell(1).setCellValue(LocalDate.now().format(formatter));
        
        // 转换为字节数组
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        
        return new ByteArrayResource(outputStream.toByteArray());
    }

    @Override
    public int countVisitsByYearAndTeacherId(Long teacherId, int year) {
        // 获取指定年份的起止时间
        LocalDate startOfYear = LocalDate.of(year, 1, 1);
        LocalDate endOfYear = LocalDate.of(year, 12, 31);
        
        // 查询条件：当前年份内有出访记录
        LambdaQueryWrapper<VisitRecord> wrapper = Wrappers.<VisitRecord>lambdaQuery()
                .eq(VisitRecord::getTeacherId, teacherId)
                .and(w -> 
                    // 开始日期在该年内
                    w.between(VisitRecord::getStartDate, startOfYear, endOfYear)
                    // 或结束日期在该年内
                    .or().between(VisitRecord::getEndDate, startOfYear, endOfYear)
                    // 或出访时间跨越该年(开始日期早于该年开始，结束日期晚于该年结束)
                    .or().and(ww -> 
                        ww.lt(VisitRecord::getStartDate, startOfYear)
                        .gt(VisitRecord::getEndDate, endOfYear)
                    )
                );
        
        return (int) count(wrapper);
    }
    
    /**
     * 上传文件
     * 
     * @param file 文件
     * @param fileType 文件类型
     * @return 文件URL
     * @throws IOException
     */
    private String uploadFile(MultipartFile file, String fileType) throws IOException {
        // 检查文件是否为空
        if (file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }
        
        // 检查目录是否存在，不存在则创建
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        
        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String fileExtension = StringUtils.getFilenameExtension(originalFilename);
        String newFilename = UUID.randomUUID().toString() + "." + fileExtension;
        
        // 存储路径
        String subDirectory = "visit_records/" + fileType + "/";
        File directory = new File(uploadPath + "/" + subDirectory);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        
        String filePath = uploadPath + "/" + subDirectory + newFilename;
        File dest = new File(filePath);
        
        // 保存文件
        file.transferTo(dest);
        
        // 返回相对路径，用于存储到数据库
        return "/uploads/" + subDirectory + newFilename;
    }
} 