<template>
  <!-- 项目列表 -->
  <table
    border="1"
    cellpadding="10"
    cellspacing="0"
    style="width: 100%; text-align: center"
  >
    <thead>
      <tr>
        <th>项目名称</th>
        <th>项目来源</th>
        <th>经费</th>
        <th>开始时间</th>
        <th>结束时间</th>
        <th>操作</th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="(item, index) in tableData" :key="item.pid">
        <td>{{ item.name }}</td>
        <td>{{ item.source }}</td>
        <td>{{ item.budget }}</td>
        <td>{{ item.startTime }}</td>
        <td>{{ item.endTime }}</td>
        <td>
          <button @click="openDialog('edit', index)">修改</button>
          <button @click="handleDelete(index)">删除</button>
        </td>
      </tr>
    </tbody>
  </table>

  <div style="margin-top: 20px; text-align: center">
    <button @click="openDialog('add')">新建</button>
  </div>

  <!-- 弹窗 -->
  <div v-if="dialogVisible" class="dialog-mask">
    <div class="dialog">
      <h3>{{ isEdit ? "修改项目" : "新建项目" }}</h3>
      <form @submit.prevent="submitForm">
        <div>
          <label>项目名称：</label>
          <input v-model="form.name" required />
        </div>
        <div>
          <label>项目来源：</label>
          <input v-model="form.source" required />
        </div>
        <div>
          <label>经费：</label>
          <input type="number" v-model.number="form.budget" required />
        </div>
        <div>
          <label>开始时间：</label>
          <input type="date" v-model="form.startTime" required />
        </div>
        <div>
          <label>结束时间：</label>
          <input type="date" v-model="form.endTime" required />
        </div>
        <div style="margin-top: 10px">
          <button type="submit">确定</button>
          <button type="button" @click="dialogVisible = false">取消</button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";

interface Project {
  pid: number;
  name: string;
  source: string;
  budget: number;
  startTime: string;
  endTime: string;
}

const tableData = ref<Project[]>([
  {
    pid: 1,
    name: "智能温室管理系统",
    source: "教育部",
    budget: 50000,
    startTime: "2024-01-01",
    endTime: "2024-12-31",
  },
  {
    pid: 2,
    name: "智能门禁系统",
    source: "科技部",
    budget: 80000,
    startTime: "2024-02-01",
    endTime: "2024-10-31",
  },
]);

const dialogVisible = ref(false);
const isEdit = ref(false);
const currentIndex = ref(-1);
const form = ref<Project>({
  pid: 0,
  name: "",
  source: "",
  budget: 0,
  startTime: "",
  endTime: "",
});

function openDialog(mode: "add" | "edit", index?: number) {
  if (mode === "edit" && typeof index === "number") {
    isEdit.value = true;
    currentIndex.value = index;
    form.value = { ...tableData.value[index] };
  } else {
    isEdit.value = false;
    currentIndex.value = -1;
    form.value = {
      pid: Date.now(),
      name: "",
      source: "",
      budget: 0,
      startTime: "",
      endTime: "",
    };
  }
  dialogVisible.value = true;
}

function submitForm() {
  if (isEdit.value && currentIndex.value !== -1) {
    tableData.value[currentIndex.value] = { ...form.value };
  } else {
    tableData.value.push({ ...form.value });
  }
  dialogVisible.value = false;
}

function handleDelete(index: number) {
  if (confirm("确定要删除吗？")) {
    tableData.value.splice(index, 1);
  }
}
</script>

<style scoped>
.dialog-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.dialog {
  background: white;
  padding: 20px;
  border-radius: 8px;
  width: 400px;
}

.dialog input {
  width: 100%;
  padding: 6px;
  margin-top: 4px;
  margin-bottom: 10px;
}
</style>
