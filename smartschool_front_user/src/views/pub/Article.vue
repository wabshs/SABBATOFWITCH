<script setup lang="ts">
import {onMounted, reactive, ref} from "vue";
import {MdPreview, MdCatalog} from 'md-editor-v3';
import 'md-editor-v3/lib/style.css';

import {useRoute} from "vue-router";
import request from "../../apis/request.ts";
import Comment from "../../components/Comment.vue";
import {ElMessage} from "element-plus";


const id = 'content';
const scrollElement = document.documentElement
const header = ref('测试标题');
const content = ref('# 哈哈1');
const route = useRoute();
const articleId = route.params.id;
const views = ref(0);
//用户id 获取头像用
const userId = ref('')
const userAvatarUrl = ref('')
const nickName = ref('')
const createTime = ref('')
const articleCollectNum = ref(0)


//收藏帖子相关
const isCollect = ref(false)


onMounted(() => {
  console.log(localStorage.getItem('userId'))
  request.get("/article/" + articleId)
      .then(res => {
        header.value = res.data.header
        content.value = res.data.content
        views.value = res.data.views
        userId.value = res.data.userId
        createTime.value = res.data.createTime
        request.get("/user/userAvatar/" + userId.value)
            .then(res => {
              userAvatarUrl.value = res.data.avatarUrl
              nickName.value = res.data.nickName
            })
      })

  request.put("/article/updateViews/" + articleId)
      .then(res => {
        if (res.code === 200) {
        }
      })
  checkCollect()
  getArticleCollectNums()

})

const checkCollectParams = reactive({
  userId: localStorage.getItem('userId'),
  articleId: articleId
})

const collectArticleParams = reactive({
  userId: localStorage.getItem('userId'),
  articleId: articleId,
  articleHeader: header
})

function checkCollect() {
  request.get('/article/checkCollect', {
    params: checkCollectParams
  }).then(res => {
    if (res.code === 200) {
      isCollect.value = true
    }
  })
}

const deleteCollectArticleParams = reactive({
  userId: localStorage.getItem('userId'),
  articleId: articleId
})

function collectArticle() {
  request.post('/article/collectArticle', collectArticleParams)
      .then(res => {
        if (res.code === 200) {
          ElMessage.success('收藏成功!')
          isCollect.value = true
          getArticleCollectNums()
        }
      })
}

function getArticleCollectNums() {
  request.get('/article/getArticleCollectNums/' + articleId)
      .then(res => {
        if (res.code === 200) {
          articleCollectNum.value = res.data
        }
      })
}

function deleteCollectArticle() {
  request.post('/article/deleteCollectArticle', deleteCollectArticleParams)
      .then(res => {
        if (res.code === 200) {
          ElMessage.success('取消收藏成功')
          isCollect.value = false
          getArticleCollectNums()
        } else {
          ElMessage.error(res.msg)
        }
      })
}

</script>

<template>
  <div class="all">
    <!--    左中右布局-->
    <el-row>
      <el-col :span="4">
        <el-card class="mini_profile">
          <div style="align-items: center;display: flex">
            <!--          头像-->
            <el-avatar
                :src=userAvatarUrl
            />
            <!--          名字和其他-->
            <el-divider direction="vertical"/>
            <div>{{ nickName }}</div>
          </div>

        </el-card>
      </el-col>

      <!--      正文 and 评论区-->
      <el-col :span="14">
        <el-card class="markdown" shadow="hover" :body-style="{'min-height':'400px'}" style = "border-radius: 12px">
          <!--    标题-->
          <h1 style="font-weight: bold;font-size: xx-large;text-align: center">{{ header }}</h1>
          <!--    时间、浏览量、评论数-->
          <div style="color: rgb(128,128,128);text-align: center;">
            <el-icon style="vertical-align: middle;">
              <Clock/>
            </el-icon>
            于
            {{ createTime }}发布 ·
            <el-icon style="vertical-align: middle;">
              <View/>
            </el-icon>
            浏览
            {{ views }}
            ·
            <span v-if="isCollect===false" @click="collectArticle" class="collect">
              <el-icon style="vertical-align: middle;">
                <Star/>
              </el-icon>
              收藏
            </span>

            <span v-if="isCollect===true" @click="deleteCollectArticle" class="collect">
              <el-icon style="vertical-align: middle;">
                <StarFilled/>
              </el-icon>
              已收藏
            </span>
            {{articleCollectNum}}

          </div>
          <el-divider/>
          <MdPreview :editorId="id" :modelValue="content"/>
        </el-card>
        <!--        评论区-->
        <comment/>
      </el-col>

      <!--      文章导航-->
      <el-col :span="4" class="navigation">
        <el-card>
          <template #header>
            <span>文章目录✨</span>
          </template>
          <MdCatalog :editorId="id" :scrollElement="scrollElement"/>
        </el-card>

      </el-col>

    </el-row>

  </div>
</template>

<style scoped>
.mini_profile {
  margin-top: 20px;
  margin-left: 20px;
}

.navigation {
  margin-top: 20px;
  margin-left: 55px;
}

.markdown {
  margin-left: 50px;
  margin-top: 20px;
}

.collect:hover {
  cursor: pointer;
}
</style>