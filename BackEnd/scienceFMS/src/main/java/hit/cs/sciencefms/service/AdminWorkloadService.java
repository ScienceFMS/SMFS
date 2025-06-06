package hit.cs.sciencefms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import hit.cs.sciencefms.dto.WorkloadResultDTO;

public interface AdminWorkloadService {

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

  IPage<WorkloadResultDTO> getWorkload(Integer startYear, Integer endYear,
                                       Double intellectualPropertyWeight, Double awardWeight, Double researchProjectWeight,Double visitProjectWeight,
                                       Integer page, Integer pageSize);

  /**
   * 导出所有教师的科研工作量统计结果为 Excel 文件（字节流）
   *
   * @param year 年份（可以为 null 表示全部年份）
   * @return Excel 字节数组
   * @throws Exception IO异常等
   */
  public byte[] exportWorkload(Integer startYear, Integer endYear,
                               Double intellectualPropertyWeight, Double awardWeight,
                               Double researchProjectWeight, Double visitProjectWeight,
                               Integer page, Integer pageSize) throws Exception ;
}
