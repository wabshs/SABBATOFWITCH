<script setup>

import {ChatDotSquare, Delete, Refresh, Search} from "@element-plus/icons-vue";
import {onMounted, reactive, ref} from "vue";
import request from "../request/request.js";
import {ElMessage} from "element-plus";

onMounted(() => {
  userPageInfo()
})

//分页查询相关
const tableData = ref([])
const pageParams = reactive({
  pageNum: 1,
  pageSize: 10,
  userName: '',
  nickName: ''
})
const total = ref(0)

function userPageInfo() {
  request.get('/admin/userInfoPage', {
    params: pageParams
  }).then(res => {
    if (res.code === 200) {
      tableData.value = res.data.rows
      total.value = res.data.total
    }
  })
}

function search() {
  userPageInfo()
}

function refresh() {
  pageParams.pageNum = 1
  pageParams.pageSize = 10
  pageParams.userName = ''
  pageParams.nickName = ''
  userPageInfo()
}

//评论分页
function handlePageChange(newPage) {
  pageParams.pageNum = newPage
  userPageInfo()
}

//用户状态相关
function switchStatus(row) {
  request.put('/admin/updateStatus', {
    id: row.id,
    isDeleted: row.isDeleted
  }).then(res => {
    if (res.code === 200) {
      ElMessage.success('修改成功！')
      userPageInfo()
    } else {
      ElMessage.error(res.msg)
    }
  })
}



</script>

<template>
  <div>
    <div style="margin-left: 40px;margin-top: 20px">
      <el-input :suffix-icon="Search" v-model="pageParams.nickName" placeholder="请输入昵称"
                style="width: 220px"></el-input>
      <el-input :suffix-icon="Search" v-model="pageParams.userName" placeholder="请输入用户名"
                style="width: 220px;margin-left: 10px"></el-input>
      <el-button :icon="Search" type="primary" style="margin-left: 10px" @click="search">搜索</el-button>
      <el-button :icon="Refresh" type="info" @click="refresh">重置</el-button>
    </div>
    <el-table :data="tableData" style="width: 100%;min-height: 500px;margin-left: 50px">
      <el-table-column prop="id" label="用户id"/>
      <el-table-column prop="userName" label="用户名"/>
      <el-table-column prop="nickName" label="昵称"/>
      <el-table-column prop="email" label="邮箱"/>
      <el-table-column prop="isDeleted" label="用户状态">
        <template #default="scope">
          <el-switch
              v-model="scope.row.isDeleted"
              active-text="启用"
              inactive-text="禁用"
              @change = switchStatus(scope.row)
              :active-value=1
              :inactive-value=0
              style="--el-switch-on-color: #13ce66; --el-switch-off-color: #ff4949"
          />
        </template>
      </el-table-column>
      <el-table-column prop="action" label="操作">
        <template #default="scope">
          <el-button type="danger" :icon="Delete">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div>
      <el-pagination
          :default-page-size="10"
          v-model:current-page="pageParams.pageNum"
          @current-change="handlePageChange"
          background layout="prev, pager, next" :total="total" style="margin-left: 700px"/>
    </div>
  </div>
</template>

<style scoped>

</style>