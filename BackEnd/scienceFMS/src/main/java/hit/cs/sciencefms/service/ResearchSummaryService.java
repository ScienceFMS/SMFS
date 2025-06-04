package hit.cs.sciencefms.service;

public interface ResearchSummaryService {
    /**
     * 调用大模型API生成科研成果摘要
     * @param text 科研成果详情文本
     * @return 生成的摘要
     */
    String generateSummary(String text);
    
    /**
     * 根据教师ID生成科研成果摘要
     * 1. 首先收集教师的所有科研成果信息
     * 2. 然后调用大模型API生成摘要
     * @param teacherId 教师ID
     * @return 生成的摘要
     */
    String generateSummaryByTeacherId(String teacherId);
    
    /**
     * 根据教师ID获取其科研成果数据文本
     * 包括项目、知识产权、获奖、出访等信息
     * @param teacherId 教师ID
     * @return 科研数据文本
     */
    String getTeacherResearchText(Long teacherId);
} 