<template>
  <div class="menu-management">
    <div class="toolbar">
      <div>
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增
        </el-button>
      </div>
    </div>

    <el-table :data="tableData" v-loading="loading" row-key="id" :tree-props="{ children: 'children' }">
      <el-table-column prop="menuName" label="菜单名称" width="200" />
      <el-table-column prop="icon" label="图标" width="80">
        <template #default="{ row }">
          <el-icon v-if="row.icon"><component :is="row.icon" /></el-icon>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column prop="menuType" label="类型" width="100">
        <template #default="{ row }">
          <el-tag v-if="row.type === 0" class="menu-dir">目录</el-tag>
          <el-tag v-else-if="row.type === 1" class="menu-page">菜单</el-tag>
          <el-tag v-else class="menu-btn">按钮</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="path" label="路由路径" width="180" />
      <el-table-column prop="component" label="组件路径" width="180" />
      <el-table-column prop="permission" label="权限标识" width="150" />
      <el-table-column prop="sort" label="排序" width="80" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
            {{ row.status === 1 ? '显示' : '隐藏' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" width="180">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button link type="primary" size="small" @click="handleAddChild(row)">新增子菜单</el-button>
          <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- Add/Edit Dialog -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" @close="handleDialogClose">
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px">
        <el-form-item label="菜单类型" prop="type">
          <el-radio-group v-model="form.type">
            <el-radio :value="0">目录</el-radio>
            <el-radio :value="1">菜单</el-radio>
            <el-radio :value="2">按钮</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="上级菜单" prop="parentId">
          <el-tree-select
            v-model="form.parentId"
            :data="treeData"
            :props="{ label: 'menuName', value: 'id', children: 'children' }"
            check-strictly
            placeholder="请选择上级菜单"
            style="width: 100%;"
          />
        </el-form-item>
        <el-form-item label="菜单名称" prop="menuName">
          <el-input v-model="form.menuName" />
        </el-form-item>
        <el-form-item v-if="form.type !== 2" label="图标" prop="icon">
          <el-input v-model="form.icon" placeholder="如: User, Menu" />
        </el-form-item>
        <el-form-item v-if="form.type !== 2" label="路由路径" prop="path">
          <el-input v-model="form.path" />
        </el-form-item>
        <el-form-item v-if="form.type === 1" label="组件路径" prop="component">
          <el-input v-model="form.component" placeholder="如: system/user/index" />
        </el-form-item>
        <el-form-item v-if="form.type === 2" label="权限标识" prop="permission">
          <el-input v-model="form.permission" placeholder="如: user:add" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">显示</el-radio>
            <el-radio :value="0">隐藏</el-radio>
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
import { getMenuList, createMenu, updateMenu, deleteMenu } from '@/api/menu'

const loading = ref(false)
const tableData = ref([])
const treeData = ref([])

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const form = reactive({
  id: null, parentId: 0, menuName: '', menuCode: '', type: 1,
  path: '', component: '', icon: '', permission: '', sort: 0, status: 1
})
const formRules = {
  menuName: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }]
}
const submitLoading = ref(false)

const loadData = async () => {
  loading.value = true
  try {
    const res = await getMenuList()
    tableData.value = res.data || []
    // Build tree data for select (add root option)
    treeData.value = [{ id: 0, menuName: '顶级菜单', children: res.data || [] }]
  } catch (error) { console.error(error) }
  finally { loading.value = false }
}

const handleAdd = () => {
  dialogTitle.value = '新增菜单'
  Object.assign(form, { id: null, parentId: 0, menuName: '', menuCode: '', type: 1, path: '', component: '', icon: '', permission: '', sort: 0, status: 1 })
  dialogVisible.value = true
}

const handleAddChild = (row) => {
  dialogTitle.value = '新增子菜单'
  Object.assign(form, { id: null, parentId: row.id, menuName: '', menuCode: '', type: row.type === 2 ? 2 : 1, path: '', component: '', icon: '', permission: '', sort: 0, status: 1 })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑菜单'
  Object.assign(form, { id: row.id, parentId: row.parentId || 0, menuName: row.menuName, menuCode: row.menuCode, type: row.type, path: row.path || '', component: row.component || '', icon: row.icon || '', permission: row.permission || '', sort: row.sort || 0, status: row.status })
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该菜单吗？', '提示', { type: 'warning' })
    await deleteMenu(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) { if (e !== 'cancel') console.error(e) }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate()
  submitLoading.value = true
  try {
    // Generate menu code if empty
    if (!form.menuCode && form.menuName) {
      form.menuCode = form.menuName.toLowerCase().replace(/\s+/g, ':')
    }
    if (form.id) { await updateMenu(form.id, form); ElMessage.success('更新成功') }
    else { await createMenu(form); ElMessage.success('创建成功') }
    dialogVisible.value = false
    loadData()
  } catch (error) { console.error(error) }
  finally { submitLoading.value = false }
}

const handleDialogClose = () => { formRef.value?.resetFields() }

onMounted(() => { loadData() })
</script>