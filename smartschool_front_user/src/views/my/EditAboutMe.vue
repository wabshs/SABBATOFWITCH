<script setup lang="ts">
import {onMounted, ref} from 'vue';
import {MdEditor} from 'md-editor-v3';
import 'md-editor-v3/lib/style.css';
import {useRoute} from "vue-router";
import request from "../../apis/request.ts";
import {ElInput, ElMessage, UploadInstance} from "element-plus";
import {FolderChecked} from "@element-plus/icons-vue";
import router from "../../router/router.ts";


const upload = ref<UploadInstance>()
const route = useRoute();
const userId = route.params.id;
const form = ref({
  nickName: '',
  likes: '',
  sign: '',
  aboutMe: '',
  avatarUrl: ''
})


onMounted(() => {
  getUserProfile()
})

function getUserProfile() {
  request.get('/user/userProfile/' + userId)
      .then(res => {
        if (res.code === 200) {
          form.value = res.data
        }
      })
}

function handleSuccess(response: any) {
  form.value.avatarUrl = response.data
  console.log(form.value.avatarUrl)
}

function updateUserProfile() {
  request.put('/user/updateUserProfile', form.value)
      .then(res => {
        if (res.code === 200) {
          router.push(`/my/${userId}`)
          ElMessage.success('操作成功!')
        } else {
          ElMessage.error(res.msg)
        }
      })
}


</script>

<template>
  <div style="padding: 10px">
    <h1>基础资料修改</h1>
    <div style="margin-top: 10px">
      <el-form :model="form" label-width="auto" style="max-width: 400px">
        <el-form-item label="我的昵称">
          <el-input v-model="form.nickName"/>
        </el-form-item>

        <el-form-item label="签名">
          <el-input type="textarea" v-model="form.sign"/>
        </el-form-item>

        <el-form-item label="头像">
          <el-upload
              :ref="upload"
              :limit="1"
              action="http://localhost:8081/api/file/uploadPic"
              list-type="picture"
              :on-success="handleSuccess"
          >
            <el-button type="primary">点击上传头像</el-button>
          </el-upload>
        </el-form-item>
      </el-form>
    </div>


    <div style="margin-top: 20px">
      <h1>关于我内容编辑</h1>
      <MdEditor v-model="form.aboutMe"></MdEditor>
      <el-button :icon="FolderChecked" type="primary" style="margin-top: 10px;margin-left: 20px"
                 @click="updateUserProfile">
        保存
      </el-button>
    </div>
  </div>
</template>

<style scoped>

</style>