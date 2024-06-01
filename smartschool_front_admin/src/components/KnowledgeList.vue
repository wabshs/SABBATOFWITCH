<script setup>
import {onMounted, reactive, ref} from "vue";
import {Delete, Download, InfoFilled} from "@element-plus/icons-vue";
import request from "../request/request.js";
import {ElMessage} from "element-plus";

const tableData = ref([])
onMounted(() => {
  getKnowledgeList()
})

function getKnowledgeList() {
  request.get('/ai/getKnowledgeList')
      .then(res => {
        if (res.code === 200) {
          tableData.value = res.data.map(item => ({data: item}));
        } else {
          ElMessage.error("获取知识库失败~")
        }
      })
}

function downloadFile(fileName) {
  window.open('http://localhost:8081/api/ai/downloadFile?fileName=' + fileName);
}

function deleteKnowledgeDoc(fileName) {
  request.post('/ai/deleteKnowledgeDoc?fileName='+fileName).then(res => {
    if (res.code === 200) {
      ElMessage.success('删除成功!')
      getKnowledgeList()
    }
  })
}


</script>

<template>
  <div>
    <el-table :data="tableData" style="width: 100%;min-height: 500px;margin-left: 50px;margin-top: 50px">
      <el-table-column prop="data" label="知识库文件名称"/>
      <el-table-column prop="action" label="操作">
        <template #default="scope">
          <el-button type="primary" :icon="Download" @click="downloadFile(scope.row.data)">下载知识库文件</el-button>
          <el-popconfirm
              confirm-button-text="是"
              cancel-button-text="否"
              :icon="InfoFilled"
              icon-color="#626AEF"
              title="确定删除此知识库?(慎重操作!)"
              @confirm="deleteKnowledgeDoc(scope.row.data)"
          >
            <template #reference>
              <el-button type="danger" :icon="Delete">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<style scoped>

</style>