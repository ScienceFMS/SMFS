<template>
  <div class="admin-workload-container">
    <h2 class="title">科研工作量统计</h2>

    <!-- 查询参数输入 -->
    <div class="search-parameters">
      <!-- 年份选择行 -->
      <div class="year-selection">
        <label for="startYear">起始年份:</label>
        <select id="startYear" v-model="startYear">
          <option value="" disabled selected>选择起始年份</option>
          <option v-for="year in yearsList" :key="year" :value="year">
            {{ year }}
          </option>
        </select>

        <label for="endYear">结束年份:</label>
        <select id="endYear" v-model="endYear">
          <option value="" disabled selected>选择结束年份</option>
          <option v-for="year in yearsList" :key="year" :value="year">
            {{ year }}
          </option>
        </select>
      </div>

      <!-- 权重选择行 -->
      <div class="weight-selection">
        <label for="researchProjectWeight">科研项目权重:</label>
        <select id="researchProjectWeight" v-model="researchProjectWeight">
          <option value="" disabled selected>选择科研项目权重</option>
          <option v-for="weight in weightsList" :key="weight" :value="weight">
            {{ weight }}
          </option>
        </select>

        <label for="awardWeight">奖励权重:</label>
        <select id="awardWeight" v-model="awardWeight">
          <option value="" disabled selected>选择奖励权重</option>
          <option v-for="weight in weightsList" :key="weight" :value="weight">
            {{ weight }}
          </option>
        </select>

        <label for="intellectualPropertyWeight">知识产权权重:</label>
        <select
          id="intellectualPropertyWeight"
          v-model="intellectualPropertyWeight"
        >
          <option value="" disabled selected>选择知识产权权重</option>
          <option v-for="weight in weightsList" :key="weight" :value="weight">
            {{ weight }}
          </option>
        </select>

        <label for="visitProjectWeight">出访项目权重:</label>
        <select id="visitProjectWeight" v-model="visitProjectWeight">
          <option value="" disabled selected>选择出访项目权重</option>
          <option v-for="weight in weightsList" :key="weight" :value="weight">
            {{ weight }}
          </option>
        </select>
      </div>

      <!-- 查询按钮 -->
      <!-- 导出 Excel 按钮 -->
      <div class="button-container">
        <button @click="fetchWorkload" class="btn search-btn">查询</button>

        <!-- 导出 Excel 按钮 -->

        <button @click="exportExcel" class="btn export-btn">导出 Excel</button>
      </div>
    </div>

    <!-- 查询结果表格 -->
    <div v-if="isDataLoaded" class="table-wrapper">
      <table>
        <thead>
          <tr>
            <th>教师姓名</th>
            <th>所属学院</th>
            <th>项目数量</th>
            <th>奖励数量</th>
            <th>知识产权数量</th>
            <th>出访次数</th>
            <th>总分</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="(item, index) in workloadList"
            :key="index"
            :class="{ 'odd-row': index % 2 === 1 }"
          >
            <td>{{ item.teacherName }}</td>
            <td>{{ item.department }}</td>
            <td>{{ item.researchProjectCount }}</td>
            <td>{{ item.awardCount }}</td>
            <td>{{ item.intellectualPropertyCount }}</td>
            <td>{{ item.visitRecordCount }}</td>
            <td>{{ item.totalScore }}</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 分页 -->
    <div class="pagination" v-if="isDataLoaded">
      <button @click="prevPage" :disabled="page === 1" class="btn page-btn">
        上一页
      </button>
      <span class="page-info">第 {{ page }} 页</span>
      <button @click="nextPage" :disabled="!hasNextPage" class="btn page-btn">
        下一页
      </button>
    </div>
  </div>
</template>





<script setup>
import { ref, onMounted } from "vue";
import { getWorkloadStats, exportWorkloadExcel } from "../../utils/adminApi";

// 数据和参数
const workloadList = ref([]); // 存储工作量统计数据
const page = ref(1); // 当前页
const pageSize = 10; // 每页显示的记录数
const hasNextPage = ref(false); // 是否有下一页

// 输入参数
const startYear = ref(2001); // 默认起始年份为2001
const endYear = ref(new Date().getFullYear()); // 默认结束年份为当前年份
const intellectualPropertyWeight = ref(2); // 默认知识产权权重
const awardWeight = ref(3); // 默认奖励权重
const researchProjectWeight = ref(4); // 默认科研项目权重
const visitProjectWeight = ref(1); // 默认出访项目权重

// 控制是否显示表格内容
const isDataLoaded = ref(false);

// 生成年份列表（2001年至当前年份）
const generateYears = () => {
  const years = [];
  const currentYear = new Date().getFullYear();
  for (let year = 2001; year <= currentYear; year++) {
    years.push(year);
  }
  return years;
};

const yearsList = ref(generateYears());

// 生成权重列表（从1到10，步长为1）
const generateWeights = () => {
  const weights = [];
  for (let i = 1; i <= 10; i++) {
    weights.push(i);
  }
  return weights;
};

const weightsList = ref(generateWeights());

// 获取科研工作量统计数据
const fetchWorkload = async () => {
  try {
    const params = {
      startYear: startYear.value,
      endYear: endYear.value,
      intellectualPropertyWeight: intellectualPropertyWeight.value,
      awardWeight: awardWeight.value,
      researchProjectWeight: researchProjectWeight.value,
      visitProjectWeight: visitProjectWeight.value,
      page: page.value,
      pageSize,
    };

    const res = await getWorkloadStats(params); // 调用 API 获取数据
    console.log("接口返回的数据是：", res); // <-- 关键调试行

    if (res.code === 200) {
      workloadList.value = res.data.records;
      hasNextPage.value = res.data.total > page.value * pageSize;
      isDataLoaded.value = true; // 数据加载成功，显示内容
    } else {
      alert("加载失败：" + res.message);
    }
  } catch (err) {
    console.error("获取失败", err);
  }
};

// 导出 Excel
const exportExcel = async () => {
  try {
    const params = {
      startYear: startYear.value,
      endYear: endYear.value,
      intellectualPropertyWeight: intellectualPropertyWeight.value,
      awardWeight: awardWeight.value,
      researchProjectWeight: researchProjectWeight.value,
      visitProjectWeight: visitProjectWeight.value,
    };
    const blob = await exportWorkloadExcel(params);
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = "workload.xlsx";
    a.click();
    window.URL.revokeObjectURL(url);
  } catch (err) {
    console.error("导出失败", err);
    alert("导出失败，请检查后台服务是否正常");
  }
};

// 翻到下一页
const nextPage = () => {
  if (hasNextPage.value) {
    page.value++;
    fetchWorkload();
  }
};

// 翻到上一页
const prevPage = () => {
  if (page.value > 1) {
    page.value--;
    fetchWorkload();
  }
};
</script>





<style scoped>
.admin-workload-container {
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.title {
  text-align: center;
  font-size: 28px;
  font-weight: 600;
  color: #333;
  margin-bottom: 20px;
}

.button-container {
  display: flex;
  justify-content: space-between; /* 将按钮放置到容器的两端 */
  margin-bottom: 15px;
}

.btn {
  background-color: #3b82f6;
  color: white;
  padding: 8px 16px;
  font-size: 14px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  user-select: none;
  transition: background-color 0.25s ease;
}

.btn:hover:not(:disabled) {
  background-color: #2563eb;
}

.btn:disabled {
  background-color: #a5b4fc;
  cursor: not-allowed;
}

button.search-btn {
  padding: 10px 20px;
  font-size: 16px;
  background-color: #4caf50;
  color: white;
  border: none;
  cursor: pointer;
  border-radius: 5px;
  align-self: flex-start;
}
button.export-btn {
  padding: 10px 20px;
  font-size: 16px;
  background-color: #4caf50;
  color: white;
  border: none;
  cursor: pointer;
  border-radius: 5px;
  align-self: flex-start;
}

.table-wrapper {
  overflow-x: auto;
}
.search-filters {
  background-color: #f9f9f9;
  padding: 15px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.filter-group {
  margin-bottom: 15px;
}

.filter-group h3 {
  font-size: 1rem;
  margin-bottom: 8px;
  color: #333;
}

.filter-options {
  display: flex;
  gap: 15px;
  flex-wrap: wrap;
}

.filter-option {
  display: flex;
  align-items: center;
  gap: 5px;
  cursor: pointer;
}

.filter-select,
.filter-input {
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}
table {
  width: 100%;
  border-collapse: collapse;
  min-width: 700px;
}

th,
td {
  padding: 12px 16px;
  border: 1px solid #ddd;
  text-align: center;
  font-size: 14px;
  color: #444;
}

thead tr {
  background-color: #f3f4f6;
  font-weight: 600;
  color: #1f2937;
}

tbody tr:hover {
  background-color: #e0e7ff;
}

.odd-row {
  background-color: #fafafa;
}

.pagination {
  margin-top: 25px;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 15px;
}

.page-info {
  font-weight: 500;
  color: #555;
}

.search-parameters {
  display: flex;
  flex-direction: column;
  gap: 20px;
  padding: 20px;
}

.year-selection,
.weight-selection {
  display: flex;
  gap: 10px; /* Smaller gap between elements */
  flex-wrap: nowrap; /* Prevent wrapping */
  justify-content: flex-start; /* Align items to the left */
  align-items: center; /* Vertically align elements */
  height: 30px; /* Ensure the height of each row is the same */
}

.year-selection label,
.weight-selection label {
  font-weight: bold;
  margin-right: 10px;
  width: auto;
}

select {
  padding: 4px 6px;
  font-size: 12px;
  width: 10%; /* Reduced width for more items on the same line */
  margin-bottom: 0;
}

.table-wrapper {
  margin-top: 20px;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 5px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
  gap: 20px;
}

.pagination .btn {
  padding: 5px 10px;
  font-size: 14px;
  background-color: #007bff;
  color: white;
  border: none;
  cursor: pointer;
  border-radius: 5px;
}

.pagination .btn:hover {
  background-color: #0056b3;
}

.pagination .page-info {
  display: flex;
  align-items: center;
  font-size: 14px;
}
</style>

