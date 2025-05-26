<template>
  <div class="research-projects">
    <div class="page-header">
      <h2 class="page-title">科研项目</h2>
      <el-button type="primary" @click="handleAddProject">
        <el-icon><Plus /></el-icon>新增项目
      </el-button>
    </div>
    
    <el-card class="filter-card">
      <el-form :inline="true" :model="filterForm" class="filter-form">
        <el-form-item label="项目状态">
          <el-select v-model="filterForm.status" placeholder="所有状态">
            <el-option label="所有状态" value=""></el-option>
            <el-option label="进行中" value="ongoing"></el-option>
            <el-option label="已结题" value="completed"></el-option>
            <el-option label="待立项" value="pending"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="项目类型">
          <el-select v-model="filterForm.type" placeholder="所有类型">
            <el-option label="所有类型" value=""></el-option>
            <el-option label="国家级" value="national"></el-option>
            <el-option label="省部级" value="provincial"></el-option>
            <el-option label="横向" value="horizontal"></el-option>
            <el-option label="校级" value="school"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="项目名称">
          <el-input v-model="filterForm.keyword" placeholder="请输入关键词"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="resetFilter">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <el-card class="project-list-card">
      <el-table
        :data="projectList"
        style="width: 100%"
        v-loading="loading"
        border
      >
        <el-table-column prop="name" label="项目名称" min-width="200"></el-table-column>
        <el-table-column prop="code" label="项目编号" width="180"></el-table-column>
        <el-table-column prop="type" label="项目类型" width="120"></el-table-column>
        <el-table-column prop="funds" label="经费(万元)" width="100"></el-table-column>
        <el-table-column prop="startDate" label="开始日期" width="120"></el-table-column>
        <el-table-column prop="endDate" label="结束日期" width="120"></el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">查看</el-button>
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-popconfirm
              title="确定要删除该项目吗？"
              @confirm="handleDelete(row)"
            >
              <template #reference>
                <el-button type="danger" link>删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        >
        </el-pagination>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { Plus } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';

// 分页数据
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const loading = ref(false);

// 筛选条件
const filterForm = reactive({
  status: '',
  type: '',
  keyword: ''
});

// 项目列表（模拟数据）
const projectList = ref([
  {
    id: '1',
    name: '基于深度学习的智能图像识别技术研究',
    code: 'NSFC-2022A01',
    type: '国家级',
    funds: 80,
    startDate: '2022-01-01',
    endDate: '2024-12-31',
    status: '进行中'
  },
  {
    id: '2',
    name: '区块链在供应链金融中的应用研究',
    code: 'PNSFC-2021B03',
    type: '省部级',
    funds: 30,
    startDate: '2021-07-01',
    endDate: '2023-06-30',
    status: '已结题'
  },
  {
    id: '3',
    name: '智慧校园建设关键技术研究',
    code: 'SCHOOL-2022C05',
    type: '校级',
    funds: 5,
    startDate: '2022-03-01',
    endDate: '2023-02-28',
    status: '进行中'
  },
  {
    id: '4',
    name: '企业智能决策支持系统开发',
    code: 'HZ-2022D01',
    type: '横向',
    funds: 45,
    startDate: '2022-05-15',
    endDate: '2023-05-14',
    status: '进行中'
  },
  {
    id: '5',
    name: '5G网络安全防护技术研究',
    code: 'NSFC-2020A23',
    type: '国家级',
    funds: 90,
    startDate: '2020-01-01',
    endDate: '2022-12-31',
    status: '已结题'
  }
]);

// 根据项目状态获取标签类型
const getStatusType = (status) => {
  const typeMap = {
    '进行中': 'success',
    '已结题': 'info',
    '待立项': 'warning'
  };
  return typeMap[status] || 'info';
};

// 处理新增项目
const handleAddProject = () => {
  // 实现新增项目的逻辑
  ElMessage.info('新增项目功能正在开发中');
};

// 查询
const handleSearch = () => {
  // 实际应用中，此处应调用后端API进行查询
  ElMessage.success('查询成功');
};

// 重置筛选条件
const resetFilter = () => {
  filterForm.status = '';
  filterForm.type = '';
  filterForm.keyword = '';
  // 重置后重新加载数据
  loadProjectData();
};

// 查看项目详情
const handleView = (row) => {
  ElMessage.info(`查看项目：${row.name}`);
};

// 编辑项目
const handleEdit = (row) => {
  ElMessage.info(`编辑项目：${row.name}`);
};

// 删除项目
const handleDelete = (row) => {
  ElMessage.success(`已删除项目：${row.name}`);
  // 实际应用中，应调用API删除数据，然后重新加载
  projectList.value = projectList.value.filter(item => item.id !== row.id);
};

// 处理每页显示数量变化
const handleSizeChange = (size) => {
  pageSize.value = size;
  loadProjectData();
};

// 处理页码变化
const handleCurrentChange = (page) => {
  currentPage.value = page;
  loadProjectData();
};

// 加载项目数据
const loadProjectData = () => {
  loading.value = true;
  
  // 模拟异步请求
  setTimeout(() => {
    // 实际应用中，此处应调用后端API
    // 模拟的总数据量
    total.value = 28;
    
    loading.value = false;
  }, 300);
};

onMounted(() => {
  loadProjectData();
});
</script>

<style scoped>
.research-projects {
  padding: 20px 0;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-title {
  margin: 0;
  font-weight: 500;
  color: #303133;
}

.filter-card {
  margin-bottom: 20px;
}

.filter-form {
  display: flex;
  flex-wrap: wrap;
}

.project-list-card {
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style> 