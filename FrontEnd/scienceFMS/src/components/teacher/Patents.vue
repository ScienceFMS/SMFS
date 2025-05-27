<template>
  <div class="patents">
    <div class="page-header">
      <h2 class="page-title">专利著作</h2>
      <el-button type="primary" @click="handleAddProperty">
        <el-icon><Plus /></el-icon>新增知识产权
      </el-button>
    </div>
    
    <!-- 筛选条件 -->
    <el-card class="filter-card">
      <el-form :inline="true" :model="filterForm" class="filter-form">
        <el-form-item label="类型" class="type-select-item">
          <el-select v-model="filterForm.type" placeholder="全部类型" style="width: 100%" clearable>
            <el-option label="全部类型" value=""></el-option>
            <el-option label="专利" value="PATENT"></el-option>
            <el-option label="著作权" value="COPYRIGHT"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="filterForm.keyword" placeholder="请输入名称/授权号等关键词"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="resetFilter">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <!-- 知识产权列表 -->
    <el-card class="property-list-card">
      <div class="action-bar">
        <el-button type="success" @click="handleExport">
          <el-icon><Download /></el-icon>批量导出
        </el-button>
      </div>
      
      <el-table
        :data="propertyList"
        style="width: 100%"
        v-loading="loading"
        border
      >
        <el-table-column prop="title" label="名称" min-width="180"></el-table-column>
        <el-table-column label="类型" width="120">
          <template #default="{ row }">
            <el-tag :type="row.type === 'PATENT' ? 'primary' : 'success'">
              {{ row.type === 'PATENT' ? '专利' : '著作权' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="subtype" label="子类型" width="120">
          <template #default="{ row }">
            <span>{{ row.subtype }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="authNumber" label="授权号" width="160"></el-table-column>
        <el-table-column prop="applyDate" label="申请日期" width="120"></el-table-column>
        <el-table-column prop="authDate" label="授权日期" width="120"></el-table-column>
        <el-table-column prop="inventorRank" label="发明人排名" width="120" align="center">
          <template #default="{ row }">
            <span v-if="row.inventorRank">{{ row.inventorRank }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="primary" link @click="handleView(row)">查看</el-button>
            <el-popconfirm
              title="确定要删除该知识产权记录吗？"
              @confirm="handleDelete(row)"
            >
              <template #reference>
                <el-button type="danger" link>删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
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
    
    <!-- 知识产权表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="650px"
    >
      <el-form 
        ref="propertyFormRef" 
        :model="propertyForm" 
        :rules="rules" 
        label-width="100px"
        label-position="right"
        :disabled="dialogType === 'view'"
      >
        <el-form-item label="名称" prop="title">
          <el-input v-model="propertyForm.title" placeholder="请输入知识产权名称"></el-input>
        </el-form-item>
        <el-form-item label="类型" prop="type" class="form-type-select">
          <el-select v-model="propertyForm.type" placeholder="请选择类型" style="width: 100%" @change="handleTypeChange">
            <el-option label="专利" value="PATENT"></el-option>
            <el-option label="著作权" value="COPYRIGHT"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="子类型" prop="subtype" class="form-subtype-select">
          <el-select v-model="propertyForm.subtype" placeholder="请选择子类型" style="width: 100%">
            <template v-if="propertyForm.type === 'PATENT'">
              <el-option label="发明专利" value="发明专利"></el-option>
              <el-option label="实用新型专利" value="实用新型专利"></el-option>
              <el-option label="外观设计专利" value="外观设计专利"></el-option>
            </template>
            <template v-else-if="propertyForm.type === 'COPYRIGHT'">
              <el-option label="文字作品" value="文字作品"></el-option>
              <el-option label="计算机软件" value="计算机软件"></el-option>
              <el-option label="美术作品" value="美术作品"></el-option>
              <el-option label="音乐作品" value="音乐作品"></el-option>
              <el-option label="其他" value="其他"></el-option>
            </template>
          </el-select>
        </el-form-item>
        <el-form-item label="授权号" prop="authNumber">
          <el-input v-model="propertyForm.authNumber" :placeholder="propertyForm.type === 'PATENT' ? '请输入专利号' : '请输入登记号'"></el-input>
        </el-form-item>
        <el-form-item :label="propertyForm.type === 'PATENT' ? '申请日期' : '登记日期'" prop="applyDate">
          <el-date-picker 
            v-model="propertyForm.applyDate" 
            type="date" 
            :placeholder="propertyForm.type === 'PATENT' ? '选择申请日期' : '选择登记日期'"
            style="width: 100%"
          ></el-date-picker>
        </el-form-item>
        <el-form-item :label="propertyForm.type === 'PATENT' ? '授权日期' : '授权日期'" prop="authDate">
          <el-date-picker 
            v-model="propertyForm.authDate" 
            type="date" 
            :placeholder="'选择授权日期'"
            style="width: 100%"
          ></el-date-picker>
        </el-form-item>
        <el-form-item label="发明人排名" prop="inventorRank" v-if="propertyForm.type === 'PATENT'">
          <el-input-number 
            v-model="propertyForm.inventorRank" 
            :min="1"
            :max="20"
            style="width: 100%"
            placeholder="请输入发明人排名"
          ></el-input-number>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">{{ dialogType === 'view' ? '关闭' : '取消' }}</el-button>
          <el-button v-if="dialogType !== 'view'" type="primary" @click="submitForm" :loading="submitting">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Plus, Download } from '@element-plus/icons-vue';
import { 
  getIntellectualProperties, 
  getIntellectualPropertyById, 
  addIntellectualProperty, 
  updateIntellectualProperty, 
  deleteIntellectualProperty,
  exportIntellectualProperties
} from '../../utils/api';
import * as auth from '../../utils/auth';

// 分页数据
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const loading = ref(false);
const submitting = ref(false);

// 筛选条件
const filterForm = reactive({
  type: '',
  keyword: ''
});

// 知识产权列表
const propertyList = ref([]);

// 对话框控制
const dialogVisible = ref(false);
const dialogType = ref('add'); // add, edit, view
const propertyFormRef = ref(null);

// 对话框标题
const dialogTitle = computed(() => {
  if (dialogType.value === 'add') {
    return '新增知识产权';
  } else if (dialogType.value === 'edit') {
    return '编辑知识产权';
  } else {
    return '查看知识产权';
  }
});

// 表单数据
const propertyForm = reactive({
  id: null,
  title: '',
  type: 'PATENT', // 默认选择专利
  subtype: '',
  authNumber: '',
  applyDate: '',
  authDate: '',
  inventorRank: null,
  teacherId: null
});

// 表单验证规则
const rules = {
  title: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择类型', trigger: 'change' }],
  subtype: [{ required: true, message: '请选择子类型', trigger: 'change' }],
  authNumber: [{ required: true, message: '请输入授权号/登记号', trigger: 'blur' }],
  applyDate: [{ required: false, message: '请选择申请日期/登记日期', trigger: 'change' }],
  authDate: [{ required: false, message: '请选择授权日期', trigger: 'change' }],
};

// 类型变更时重置子类型
const handleTypeChange = () => {
  propertyForm.subtype = '';
};

// 查询知识产权列表
const fetchProperties = async () => {
  loading.value = true;
  try {
    const user = auth.getCurrentUser();
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value,
      teacherId: user?.userId,
      type: filterForm.type || undefined,
      keyword: filterForm.keyword || undefined
    };
    
    const res = await getIntellectualProperties(params);
    if (res.code === 200) {
      propertyList.value = res.data.records;
      total.value = res.data.total;
    } else {
      ElMessage.error(res.message || '获取知识产权列表失败');
    }
  } catch (error) {
    console.error('获取知识产权列表失败:', error);
    ElMessage.error('获取知识产权列表失败');
  } finally {
    loading.value = false;
  }
};

// 查询按钮
const handleSearch = () => {
  currentPage.value = 1;
  fetchProperties();
};

// 重置筛选
const resetFilter = () => {
  filterForm.type = '';
  filterForm.keyword = '';
  currentPage.value = 1;
  fetchProperties();
};

// 改变页大小
const handleSizeChange = (val) => {
  pageSize.value = val;
  fetchProperties();
};

// 改变页码
const handleCurrentChange = (val) => {
  currentPage.value = val;
  fetchProperties();
};

// 新增知识产权
const handleAddProperty = () => {
  dialogType.value = 'add';
  // 重置表单
  Object.assign(propertyForm, {
    id: null,
    title: '',
    type: 'PATENT',
    subtype: '',
    authNumber: '',
    applyDate: '',
    authDate: '',
    inventorRank: null,
    teacherId: auth.getCurrentUser()?.userId
  });
  
  nextTick(() => {
    if (propertyFormRef.value) {
      propertyFormRef.value.resetFields();
    }
    dialogVisible.value = true;
  });
};

// 编辑知识产权
const handleEdit = (row) => {
  dialogType.value = 'edit';
  Object.assign(propertyForm, {
    id: row.id,
    title: row.title,
    type: row.type,
    subtype: row.subtype,
    authNumber: row.authNumber,
    applyDate: row.applyDate,
    authDate: row.authDate,
    inventorRank: row.inventorRank,
    teacherId: row.teacherId
  });
  dialogVisible.value = true;
};

// 查看知识产权
const handleView = (row) => {
  dialogType.value = 'view';
  Object.assign(propertyForm, {
    id: row.id,
    title: row.title,
    type: row.type,
    subtype: row.subtype,
    authNumber: row.authNumber,
    applyDate: row.applyDate,
    authDate: row.authDate,
    inventorRank: row.inventorRank,
    teacherId: row.teacherId
  });
  dialogVisible.value = true;
};

// 删除知识产权
const handleDelete = async (row) => {
  try {
    const res = await deleteIntellectualProperty(row.id);
    if (res.code === 200) {
      ElMessage.success('删除成功');
      fetchProperties();
    } else {
      ElMessage.error(res.message || '删除失败');
    }
  } catch (error) {
    console.error('删除知识产权失败:', error);
    ElMessage.error('删除失败');
  }
};

// 提交表单
const submitForm = async () => {
  if (!propertyFormRef.value) return;
  
  await propertyFormRef.value.validate(async (valid) => {
    if (!valid) {
      return false;
    }
    
    submitting.value = true;
    try {
      // 设置教师ID
      if (!propertyForm.teacherId) {
        propertyForm.teacherId = auth.getCurrentUser()?.userId;
      }
      
      // 新增或更新
      const api = dialogType.value === 'add' ? addIntellectualProperty : updateIntellectualProperty;
      const res = await api(propertyForm);
      
      if (res.code === 200) {
        ElMessage.success(dialogType.value === 'add' ? '新增成功' : '更新成功');
        dialogVisible.value = false;
        fetchProperties();
      } else {
        ElMessage.error(res.message || (dialogType.value === 'add' ? '新增失败' : '更新失败'));
      }
    } catch (error) {
      console.error('保存知识产权失败:', error);
      ElMessage.error(dialogType.value === 'add' ? '新增失败' : '更新失败');
    } finally {
      submitting.value = false;
    }
  });
};

// 导出知识产权列表
const handleExport = async () => {
  try {
    const user = auth.getCurrentUser();
    const params = {
      teacherId: user?.userId,
      type: filterForm.type || undefined,
      keyword: filterForm.keyword || undefined
    };
    
    const res = await exportIntellectualProperties(params);
    // 处理二进制响应
    const blob = new Blob([res], { type: 'application/vnd.ms-excel' });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', '知识产权清单.xlsx');
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);
    
    ElMessage.success('导出成功');
  } catch (error) {
    console.error('导出知识产权列表失败:', error);
    ElMessage.error('导出失败');
  }
};

// 页面加载时获取数据
onMounted(() => {
  fetchProperties();
});

// 导入nextTick
import { nextTick } from 'vue';
</script>

<style scoped>
.patents {
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

/* 添加类型选择下拉框的样式 */
.type-select-item {
  min-width: 160px;
  margin-right: 15px;
}

.property-list-card {
  margin-bottom: 20px;
}

.action-bar {
  margin-bottom: 20px;
  display: flex;
  justify-content: flex-end;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

/* 表单中类型和子类型选择框样式 */
.form-type-select .el-select {
  width: 100%;
  min-width: 150px;
}

.form-subtype-select .el-select {
  width: 100%;
  min-width: 200px;
}
</style> 