package hit.cs.sciencefms.service.impl;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import hit.cs.sciencefms.entity.Award;
import hit.cs.sciencefms.entity.IntellectualProperty;
import hit.cs.sciencefms.entity.ResearchProject;
import hit.cs.sciencefms.entity.Teacher;
import hit.cs.sciencefms.entity.VisitRecord;
import hit.cs.sciencefms.mapper.AwardMapper;
import hit.cs.sciencefms.mapper.IntellectualPropertyMapper;
import hit.cs.sciencefms.mapper.ResearchProjectMapper;
import hit.cs.sciencefms.mapper.TeacherMapper;
import hit.cs.sciencefms.mapper.VisitRecordMapper;
import hit.cs.sciencefms.service.ResearchSummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ResearchSummaryServiceImpl implements ResearchSummaryService {

    @Autowired
    @Qualifier("qianfanChatLanguageModel")
    private ChatLanguageModel chatLanguageModel;
    
    @Autowired
    private TeacherMapper teacherMapper;
    
    @Autowired
    private ResearchProjectMapper researchProjectMapper;
    
    @Autowired
    private IntellectualPropertyMapper intellectualPropertyMapper;
    
    @Autowired
    private AwardMapper awardMapper;
    
    @Autowired
    private VisitRecordMapper visitRecordMapper;

    
    @Override
    public String generateSummaryByTeacherId(String teacherId) {
        Long teacherIdLong;
        try {
            teacherIdLong = Long.parseLong(teacherId);
        } catch (NumberFormatException e) {
            throw new RuntimeException("教师ID格式错误");
        }
        
        // 获取教师的科研成果文本
        String researchText = getTeacherResearchText(teacherIdLong);
        
        // 调用大模型API生成摘要
        return generateSummary(researchText);
    }
        
    @Override
    public String generateSummary(String text) {
        // 创建提示模板
        String templateString = """
            你是一位科研助手，请根据以下科研成果详情，生成一段300字以内的中文摘要，语言要求简洁、专业，适合展示在成果展示平台上。
            
            成果详情：
            {{text}}
            
            请输出摘要：
            """;
        
        PromptTemplate promptTemplate = PromptTemplate.from(templateString);
        Map<String, Object> variables = new HashMap<>();
        variables.put("text", text);
        
        Prompt prompt = promptTemplate.apply(variables);
        
        // 调用大模型生成摘要
        return chatLanguageModel.generate(prompt.text());
    }
    
    
    
    @Override
    public String getTeacherResearchText(Long teacherId) {
        // 获取教师信息
        Teacher teacher = teacherMapper.selectById(teacherId);
        if (teacher == null) {
            throw new RuntimeException("未找到该教师信息");
        }
        
        // 获取科研项目
        List<ResearchProject> projects = researchProjectMapper.findByTeacherId(teacherId);
        
        // 获取知识产权
        List<IntellectualProperty> intellectualProperties = intellectualPropertyMapper.findByTeacherId(teacherId);
        
        // 获取获奖信息
        List<Award> awards = awardMapper.findByTeacherId(teacherId);
        
        // 获取出访记录
        List<VisitRecord> visitRecords = visitRecordMapper.findByTeacherId(teacherId);
        
        // 构建科研成果文本
        StringBuilder content = new StringBuilder();
        content.append("教师姓名：").append(teacher.getRealName()).append("\n");
        content.append("职称：").append(teacher.getTitle()).append("\n");
        content.append("研究方向：").append(teacher.getResearchArea()).append("\n\n");
        
        // 添加科研项目信息
        content.append("一、科研项目：\n");
        if (projects != null && !projects.isEmpty()) {
            for (int i = 0; i < projects.size(); i++) {
                ResearchProject project = projects.get(i);
                content.append(i + 1).append(". 项目名称：").append(project.getProjectName()).append("\n");
                content.append("   项目来源：").append(project.getSource()).append("\n");
                content.append("   项目经费：").append(project.getFunding()).append("元\n");
                content.append("   项目时间：").append(project.getStartDate()).append(" 至 ").append(project.getEndDate()).append("\n");
                content.append("   项目角色：").append(project.getRole()).append("\n\n");
            }
        } else {
            content.append("暂无科研项目记录\n\n");
        }
        
        // 添加知识产权信息
        content.append("二、知识产权：\n");
        if (intellectualProperties != null && !intellectualProperties.isEmpty()) {
            for (int i = 0; i < intellectualProperties.size(); i++) {
                IntellectualProperty ip = intellectualProperties.get(i);
                content.append(i + 1).append(". 名称：").append(ip.getTitle()).append("\n");
                content.append("   类型：").append(ip.getType()).append(" - ").append(ip.getSubtype()).append("\n");
                content.append("   授权号：").append(ip.getAuthNumber()).append("\n");
                content.append("   授权日期：").append(ip.getAuthDate()).append("\n\n");
            }
        } else {
            content.append("暂无知识产权记录\n\n");
        }
        
        // 添加获奖信息
        content.append("三、获奖情况：\n");
        if (awards != null && !awards.isEmpty()) {
            for (int i = 0; i < awards.size(); i++) {
                Award award = awards.get(i);
                content.append(i + 1).append(". 奖项名称：").append(award.getAwardName()).append("\n");
                content.append("   获奖等级：").append(award.getAwardLevel()).append("\n");
                content.append("   颁发单位：").append(award.getIssuingUnit()).append("\n");
                content.append("   获奖日期：").append(award.getAwardDate()).append("\n\n");
            }
        } else {
            content.append("暂无获奖记录\n\n");
        }
        
        // 添加出访记录
        content.append("四、学术交流：\n");
        if (visitRecords != null && !visitRecords.isEmpty()) {
            for (int i = 0; i < visitRecords.size(); i++) {
                VisitRecord visit = visitRecords.get(i);
                content.append(i + 1).append(". 出访目的：").append(visit.getPurpose()).append("\n");
                content.append("   出访地点：").append(visit.getLocation()).append("\n");
                content.append("   出访时间：").append(visit.getStartDate()).append(" 至 ").append(visit.getEndDate()).append("\n");
                content.append("   出访成果：").append(visit.getResult()).append("\n\n");
            }
        } else {
            content.append("暂无出访记录\n\n");
        }
        
        return content.toString();
    }
} 