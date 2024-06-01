<script setup lang="ts">
import {nextTick, onMounted, reactive, ref} from 'vue';
import {MdEditor} from 'md-editor-v3';
import 'md-editor-v3/lib/style.css';
import {useRoute} from "vue-router";
import request from "../../apis/request.ts";
import {ElInput, ElMessage} from "element-plus";
import {FolderChecked} from "@element-plus/icons-vue";

const text = ref('');
const route = useRoute();
const userId = route.params.id;
const form = reactive({
  nickName: '',
  likes: '',
  sign: '',
})
const inputValue = ref('')
const dynamicTags = ref(['Tag 1', 'Tag 2', 'Tag 3'])
const inputVisible = ref(false)
const InputRef = ref<InstanceType<typeof ElInput>>()

const handleClose = (tag: string) => {
  dynamicTags.value.splice(dynamicTags.value.indexOf(tag), 1)
}

const showInput = () => {
  inputVisible.value = true
  nextTick(() => {
    InputRef.value!.input!.focus()
  })
}

const handleInputConfirm = () => {
  if (inputValue.value) {
    dynamicTags.value.push(inputValue.value)
  }
  inputVisible.value = false
  inputValue.value = ''
}




//传给后端的参数
const formData = reactive({
  id: userId,
  aboutMe: text
})

onMounted(() => {
  request.get("/user/aboutMe/" + userId)
      .then(res => {
        text.value = res.data.aboutMe
      })
})

function updateAboutMe() {
  request.put("/user/updateAboutMe", formData)
      .then(res => {
        if (res.code === 200) {
          ElMessage({
            message: '修改成功!',
            type: 'success'
          })
        } else {
          ElMessage.error('出错了，请联系管理员')
        }
      })
}

</script>

<template>
  <div>

    <h1>基础资料修改</h1>
    <div style="margin-top: 10px">
      <el-form :model="form" label-width="auto" style="max-width: 400px">
        <el-form-item label="我的昵称">
          <el-input v-model="form.nickName"/>
        </el-form-item>

        <el-form-item label="签名">
          <el-input type="textarea" v-model="form.sign"/>
        </el-form-item>

        <el-form-item label="兴趣爱好">
          <div class="flex gap-2">
            <el-tag
                v-for="tag in dynamicTags"
                :key="tag"
                closable
                :disable-transitions="false"
                @close="handleClose(tag)"
            >
              {{ tag }}
            </el-tag>
            <el-input
                v-if="inputVisible"
                ref="InputRef"
                v-model="inputValue"
                class="w-20"
                size="small"
                @keyup.enter="handleInputConfirm"
                @blur="handleInputConfirm"
            />
            <el-button v-else class="button-new-tag" size="small" @click="showInput">
              + New Tag
            </el-button>
          </div>
        </el-form-item>
      </el-form>
    </div>


    <div style="margin-top: 20px">
      <h1>关于我内容编辑</h1>
      <MdEditor v-model="text"></MdEditor>
      <el-button :icon="FolderChecked" type="primary" style="margin-top: 10px;margin-left: 20px" @click="updateAboutMe">
        保存
      </el-button>
    </div>
  </div>
</template>

<style scoped>

</style>