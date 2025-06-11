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
    
    /**
     * 生成科研成果趋势分析、高价值成果识别和学科分布分析
     * @param startYear 开始年份
     * @param endYear 结束年份
     * @return 分析结果（包含趋势分析、高价值成果排行和学科分布）
     */
    String generateResearchAnalysis(Integer startYear, Integer endYear);
    
    /**
     * 收集指定时间范围内的所有科研成果数据文本
     * @param startYear 开始年份
     * @param endYear 结束年份
     * @return 所有科研成果数据文本
     */
    String getAllResearchDataText(Integer startYear, Integer endYear);
} 