<template>
  <div class="dept-management">
    <div class="toolbar">
      <div>
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增
        </el-button>
      </div>
    </div>

    <el-table :data="tableData" v-loading="loading" row-key="id" :tree-props="{ children: 'children' }">
      <el-table-column prop="deptName" label="部门名称" width="200" />
      <el-table-column prop="leader" label="负责人" width="120" />
      <el-table-column prop="phone" label="联系电话" width="130" />
      <el-table-column prop="email" label="邮箱" width="180" />
      <el-table-column prop="sort" label="排序" width="80" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
            {{ row.status === 1 ? '正常' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" width="180">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button link type="primary" size="small" @click="handleAddChild(row)">新增子部门</el-button>
          <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- Add/Edit Dialog -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" @close="handleDialogClose">
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px">
        <el-form-item label="上级部门" prop="parentId">
          <el-tree-select
            v-model="form.parentId"
            :data="treeData"
            :props="{ label: 'deptName', value: 'id', children: 'children' }"
            check-strictly
            placeholder="请选择上级部门"
            style="width: 100%;"
          />
        </el-form-item>
        <el-form-item label="部门名称" prop="deptName">
          <el-input v-model="form.deptName" />
        </el-form-item>
        <el-form-item label="负责人" prop="leader">
          <el-input v-model="form.leader" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">正常</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getDeptList, createDept, updateDept, deleteDept } from '@/api/dept'

const loading = ref(false)
const tableData = ref([])
const treeData = ref([])

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const form = reactive({
  id: null, parentId: 0, deptName: '', leader: '', phone: '', email: '', sort: 0, status: 1
})
const formRules = {
  deptName: [{ required: true, message: '请输入部门名称', trigger: 'blur' }]
}
const submitLoading = ref(false)

const loadData = async () => {
  loading.value = true
  try {
    const res = await getDeptList()
    tableData.value = res.data || []
    treeData.value = [{ id: 0, deptName: '顶级部门', children: res.data || [] }]
  } catch (error) { console.error(error) }
  finally { loading.value = false }
}

const handleAdd = () => {
  dialogTitle.value = '新增部门'
  Object.assign(form, { id: null, parentId: 0, deptName: '', leader: '', phone: '', email: '', sort: 0, status: 1 })
  dialogVisible.value = true
}

const handleAddChild = (row) => {
  dialogTitle.value = '新增子部门'
  Object.assign(form, { id: null, parentId: row.id, deptName: '', leader: '', phone: '', email: '', sort: 0, status: 1 })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑部门'
  Object.assign(form, { id: row.id, parentId: row.parentId || 0, deptName: row.deptName, leader: row.leader || '', phone: row.phone || '', email: row.email || '', sort: row.sort || 0, status: row.status })
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该部门吗？', '提示', { type: 'warning' })
    await deleteDept(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) { if (e !== 'cancel') console.error(e) }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate()
  submitLoading.value = true
  try {
    if (form.id) { await updateDept(form.id, form); ElMessage.success('更新成功') }
    else { await createDept(form); ElMessage.success('创建成功') }
    dialogVisible.value = false
    loadData()
  } catch (error) { console.error(error) }
  finally { submitLoading.value = false }
}

const handleDialogClose = () => { formRef.value?.resetFields() }

onMounted(() => { loadData() })
</script>