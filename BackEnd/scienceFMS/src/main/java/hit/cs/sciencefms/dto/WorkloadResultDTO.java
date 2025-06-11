package hit.cs.sciencefms.dto;
import lombok.Data;

/**
 * 教师科研工作量统计 DTO
 */
@Data
public class WorkloadResultDTO {

  private Long teacherId;

  private String teacherName;

  private String department;

  private Integer intellectualPropertyCount;

  private Integer awardCount;

  private Integer researchProjectCount;

  private Integer visitRecordCount;

  private Double totalScore;
}
