<script setup lang="ts">
import {onMounted, reactive, ref} from "vue";
import {MdEditor} from 'md-editor-v3';
import 'md-editor-v3/lib/style.css';
import {ElMessage, UploadInstance, UploadProps} from "element-plus";
import request from "../../apis/request.ts";

const tagsStr = ref('')
const form = reactive({
  header: "",
  coverUrl: "",
  content: "正文编辑器",
  userId: localStorage.getItem("userId"),
  tags: tagsStr
})


const upload = ref<UploadInstance>()
const loading = ref(false)
//添加文章标签用
const tagsData = ref([
  {
    id: 1,
    name: '交友'
  },
  {
    id: 2,
    name: '知识'
  }
]);

// 创建一个 ref 来跟踪选中的 ID 数组 ---------------选择分类用-------------
const selectedIds = ref([]);
const handleChange = (id, checked) => {
  if (checked) {
    // 如果标签被选中，将 ID 添加到数组中
    //
    selectedIds.value.push(id);
    console.log('选择了' + id)
    console.log('现在选择的是:' + selectedIds.value + '转为str:')
    tagsStr.value = selectedIds.value.toString()
    console.log(tagsStr.value)
    console.log('form.tags是:' + form.tags)
  } else {
    const index = selectedIds.value.indexOf(id);
    selectedIds.value.splice(index, 1);
    console.log('移除了' + id)
    console.log('现在选择的是:' + selectedIds.value)
    tagsStr.value = selectedIds.value.toString()
    console.log(tagsStr.value)
  }
};

//上传前的钩子函数 检测文件格式以及大小
const beforeAvatarUpload: UploadProps['beforeUpload'] = (rawFile) => {
  if (rawFile.type !== 'image/jpeg') {
    ElMessage.error('图片格式必须为JPG!')
    return false
  } else if (rawFile.size / 1024 / 1024 > 5) {
    ElMessage.error('图片大小最大为5MB!')
    return false
  }
  return true
}
//上传后的钩子 回显URL存储到数据库
const handleAvatarSuccess: UploadProps['onSuccess'] = (
    response
) => {
  //赋值给url
  loading.value = true

  form.coverUrl = response.data
  //console.log(form.coverUrl)
  request.post("/article/createArticle", form)
      .then(res => {
        if (res.code === 200) {
          ElMessage({
            message: '发布成功!',
            type: 'success'
          })
          loading.value = false
        } else {
          ElMessage.error(res.msg)
        }
      })
}

onMounted(() => {
  getArticleTags()
})

//获取文章分类
function getArticleTags() {
  request.get('/article/getArticleTags')
      .then(res => {
        tagsData.value = res.data
        console.log(tagsData.values)
      })
}

// 图片上传事件
const onUploadImg = async (files, callback) => {
  const res = await Promise.all(
      files.map((file) => {
        return new Promise((rev, rej) => {
          // 传给后端一个FormData数据，添加键值对，这里添加file
          const form = new FormData();
          form.append('file', file);
          request.post('/file/uploadPic', form)
              .then((res) => {
                console.log(res)
                rev(res)
              })
              .catch((error) => rej(error));
        });
      })
  );
  callback(res.map((item) => item.data));
};

function deleteContent() {
  form.content = ""
}

function createArticle() {

  if (selectedIds.value.length === 0) {
    ElMessage.error("请选择文章标签!")
  } else {
    //首先把图传到服务器
    upload.value!.submit();
  }


}

</script>

<template>
  <div style="padding: 20px">
    <div style="margin-left: 50px;margin-top: 30px">
      <el-form :model="form" style="max-width: 600px">
        <el-form-item label="帖子标题">
          <el-input v-model="form.header" placeholder="请输入标题">

          </el-input>
        </el-form-item>
        <el-form-item label="帖子封面">
          <!--          上传-->
          <el-upload
              ref="upload"
              :limit="1"
              :before-upload="beforeAvatarUpload"
              action="http://localhost:8081/api/file/uploadPic"
              :on-success="handleAvatarSuccess"
              :auto-upload="false"
          >
            <template #trigger>
              <el-button type="primary">上传封面</el-button>
            </template>
          </el-upload>
        </el-form-item>
        <el-form-item label="文章分类（必选）">
          <a-checkable-tag
              v-for="data in tagsData"
              :key=data.id
              :checked="selectedIds.includes(data.id)"
              @change="checked => handleChange(data.id, checked)"
          >
            {{ data.name }}
          </a-checkable-tag>
        </el-form-item>
      </el-form>
    </div>
    <!--    分割线-->
    <el-divider/>

    <!--    正文编辑器-->
    <MdEditor v-model="form.content" @onUploadImg="onUploadImg"/>
    <!--    操作按钮-->
    <div class="actions">
      <el-button v-loading.fullscreen.lock="loading" type="success" plain @click="createArticle">发布</el-button>
      <el-button type="danger" plain @click="deleteContent">清空</el-button>
    </div>
  </div>

</template>

<style scoped>
.actions {
  margin-top: 10px;
}
</style>