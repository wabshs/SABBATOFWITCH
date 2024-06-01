<script setup>

import {UploadFilled} from "@element-plus/icons-vue";
import {ElMessage} from "element-plus";
import {ref} from "vue";

function uploadSuccess() {
  loading.value = false
  ElMessage.success('上传成功！请到知识库文件页面查看~')
}
function waiting() {
  loading.value = true
}
const loading = ref(false)
</script>

<template>
  <div class="centered-container" v-loading="loading" element-loading-text="上传并向量化中请耐心等待...">
    <el-upload
        class="upload-demo"
        drag
        multiple
        name="files"
        action="http://localhost:8081/api/ai/uploadFile"
        :on-success="uploadSuccess"
        :on-progress="waiting"
    >
      <el-icon class="el-icon--upload"><upload-filled /></el-icon>
      <div class="el-upload__text">
        把文件拖到这里 <em>或者点击上传</em>
      </div>
      <template #tip>
        <div class="el-upload__tip">
          知识库文件上传
        </div>
      </template>
    </el-upload>
  </div>
</template>

<style scoped>
.centered-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh
}
</style>