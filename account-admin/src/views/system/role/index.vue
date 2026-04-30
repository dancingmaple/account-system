<template>
  <div class="role-management">
    <!-- Toolbar -->
    <div class="toolbar">
      <div>
        <el-input
          v-model="searchQuery"
          placeholder="搜索角色名称"
          style="width: 200px; margin-right: 10px;"
          clearable
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-button type="primary" @click="handleSearch">
          <el-icon><Search /></el-icon>
          搜索
        </el-button>
      </div>
      <div>
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增
        </el-button>
      </div>
    </div>

    <!-- Table -->
    <el-table :data="tableData" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="roleCode" label="角色编码" width="150" />
      <el-table-column prop="roleName" label="角色名称" width="150" />
      <el-table-column prop="dataScope" label="数据范围" width="120">
        <template #default="{ row }">
          <el-tag v-if="row.dataScope === 1" type="success" size="small">全部数据</el-tag>
          <el-tag v-else-if="row.dataScope === 2" type="warning" size="small">部门数据</el-tag>
          <el-tag v-else-if="row.dataScope === 3" type="info" size="small">仅本人</el-tag>
          <el-tag v-else type="primary" size="small">自定义</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="sort" label="排序" width="80" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
            {{ row.status === 1 ? '正常' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" width="180" />
      <el-table-column label="操作" fixed="right" width="200">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button link type="primary" size="small" @click="handleAssignMenus(row)">分配菜单</el-button>
          <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- Pagination -->
    <div style="margin-top: 20px; display: flex; justify-content: flex-end;">
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadData"
        @current-change="loadData"
      />
    </div>

    <!-- Add/Edit Dialog -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" @close="handleDialogClose">
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px">
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="form.roleCode" :disabled="!!form.id" />
        </el-form-item>
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" />
        </el-form-item>
        <el-form-item label="数据范围" prop="dataScope">
          <el-select v-model="form.dataScope" style="width: 100%;">
            <el-option :value="1" label="全部数据" />
            <el-option :value="2" label="部门数据" />
            <el-option :value="3" label="仅本人数据" />
            <el-option :value="4" label="自定义数据" />
          </el-select>
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

    <!-- Assign Menus Dialog -->
    <el-dialog v-model="menuDialogVisible" title="分配菜单权限" width="400px">
      <el-tree
        ref="menuTreeRef"
        :data="menuList"
        :props="{ label: 'menuName', children: 'children' }"
        show-checkbox
        node-key="id"
        :default-checked-keys="selectedMenus"
      />
      <template #footer>
        <el-button @click="menuDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmitMenus">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getRoleList, createRole, updateRole, deleteRole, assignMenus } from '@/api/role'
import { getMenuList } from '@/api/menu'

const searchQuery = ref('')
const loading = ref(false)
const tableData = ref([])
const pagination = reactive({ page: 1, size: 10, total: 0 })

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const form = reactive({ id: null, roleCode: '', roleName: '', dataScope: 1, sort: 0, status: 1 })
const formRules = {
  roleCode: [{ required: true, message: '请输入角色编码', trigger: 'blur' }],
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }]
}
const submitLoading = ref(false)

const menuDialogVisible = ref(false)
const menuList = ref([])
const selectedMenus = ref([])
const currentRoleId = ref(null)
const menuTreeRef = ref(null)

const loadData = async () => {
  loading.value = true
  try {
    const res = await getRoleList({ page: pagination.page, size: pagination.size, roleName: searchQuery.value })
    tableData.value = res.data || []
    pagination.total = res.data?.length || 0
  } catch (error) {
    console.error('Load failed:', error)
  } finally {
    loading.value = false
  }
}

const loadMenus = async () => {
  try {
    const res = await getMenuList()
    menuList.value = res.data || []
  } catch (error) {
    console.error('Load menus failed:', error)
  }
}

const handleSearch = () => { pagination.page = 1; loadData() }

const handleAdd = () => {
  dialogTitle.value = '新增角色'
  Object.assign(form, { id: null, roleCode: '', roleName: '', dataScope: 1, sort: 0, status: 1 })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑角色'
  Object.assign(form, { id: row.id, roleCode: row.roleCode, roleName: row.roleName, dataScope: row.dataScope, sort: row.sort, status: row.status })
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该角色吗？', '提示', { type: 'warning' })
    await deleteRole(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) { if (e !== 'cancel') console.error(e) }
}

const handleAssignMenus = async (row) => {
  currentRoleId.value = row.id
  selectedMenus.value = []
  await loadMenus()
  menuDialogVisible.value = true
}

const handleSubmitMenus = async () => {
  const checkedKeys = menuTreeRef.value?.getCheckedKeys() || []
  try {
    await assignMenus(currentRoleId.value, checkedKeys)
    ElMessage.success('分配成功')
    menuDialogVisible.value = false
  } catch (error) { console.error(error) }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate()
  submitLoading.value = true
  try {
    if (form.id) { await updateRole(form.id, form); ElMessage.success('更新成功') }
    else { await createRole(form); ElMessage.success('创建成功') }
    dialogVisible.value = false
    loadData()
  } catch (error) { console.error(error) }
  finally { submitLoading.value = false }
}

const handleDialogClose = () => { formRef.value?.resetFields() }

onMounted(() => { loadData() })
</script>