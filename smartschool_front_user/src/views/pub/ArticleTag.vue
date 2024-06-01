<script setup lang="ts">

import router from "../../router/router.ts";
import {onMounted, reactive, ref} from "vue";
import request from "../../apis/request.ts";
import {useRoute} from "vue-router";

const route = useRoute();
const tags = ref(route.params.tag);
const articles = ref([
  {
    id: '',
    header: '无网络',
    avatarUrl: '无网络',
    description: '无网络',
    coverUrl: '',
    articleTags: []
  },
])
//分页参数
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(5)
const pageParam = reactive({
  pageNum: pageNum,
  pageSize: pageSize,
  tags: tags
})
const tagParam = reactive({
  id: tags
})


//标题
const tagName = ref('')

onMounted(() => {
  getTagById()
  getArticleByTagsPage()
})

function getArticleByTagsPage() {
  request.get('/article/getArticleByTagsPage', {
    params: pageParam
  }).then(res => {
    if (res.code === 200) {
      articles.value = res.data.rows
      total.value = res.data.total
    }
  })
}

function getTagById() {
  request.get('/article/getTagsById',{
    params: tagParam
  }).then(res=>{
    if(res.code === 200) {
      tagName.value = res.data.name
    }
  })
}

function handlePageChange(newPage: number) {
  pageParam.pageNum = newPage
  getArticleByTagsPage()
}
</script>

<!--根据标签查询贴子-->
<template>
  <div>
    <el-container>
      <el-header>
        <div style="margin-top: 10px;font-size: 25px;font-family: 得意黑,serif;">
          您现在浏览的是 <span style="color: cornflowerblue">{{tagName}}</span> 标签下的帖子
        </div>
      </el-header>
      <el-main>
        <el-row>
          <el-col :span="4">
            <el-card style="max-width: 480px">
              <template #header>帖子标签</template>
            </el-card>
          </el-col>
          <el-col :span="14" style="margin-left: 20px">
            <el-card>
              <div class="middle" v-for="(article, index) in articles" :key="index" style="align-content: center">
                <a-card hoverable style="width: 850px;margin-top: 15px" @click="router.push('/article/'+article.id)">
                  <template #cover>
                    <div style="height: 200px;">
                      <img
                          style="height: 100%;width: 100%;object-fit: cover"
                          alt="example"
                          :src=article.coverUrl
                      />
                    </div>
                  </template>
                  <a-card-meta :title=article.header>
                  </a-card-meta>
                </a-card>
              </div>
              <!--      分页-->
              <div style="margin-top: 40px;margin-left: 350px">
                <el-pagination
                    background layout="prev, pager, next"
                    :total=total
                    :default-page-size="5"
                    v-model:current-page="pageParam.pageNum"
                    @current-change="handlePageChange"></el-pagination>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </el-main>
    </el-container>
  </div>
</template>

<style scoped>

</style>