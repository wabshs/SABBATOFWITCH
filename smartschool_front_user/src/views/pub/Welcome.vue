<template>
  <div>
    <u-chat :user-id="userChatId" :emoji="emoji" :data="chatData" @submit="submit"></u-chat>
    <!--    开屏大图 上方区域-->
    <div class="welcome_pic">
      <!--      里面的文字-->
      <div class="words">
        <span style="font-size: 50px;font-weight: bold">欢迎来到
          <span class="eHBUT">iHBUT</span>
          官方网站
        </span>
        <br/>
        <br/>
        <span style="font-weight: bold;font-size: 20px">专注给予湖工学子便捷体验</span>
        <br/>
        <br/>
        <div style="font-size: 40px;min-height: 50px">
          <span style="color: #fff;font-family: 'JetBrains Mono',serif">
            {{ obj.output.slice(0, 7) }}
          </span>
          <span style="font-family: 'JetBrains Mono',serif">
            {{ obj.output.slice(7, 11) }}
          </span>
          <span style="color: #ee00ff;font-family: 'JetBrains Mono',serif">
            {{ obj.output.slice(11, 18) }}
          </span>
          <span style="color: rgba(255,246,0,0.99);font-family: 'JetBrains Mono',serif">
            {{ obj.output.slice(18, 19) }}
          </span>
          <span style="color: rgba(0,255,33,0.99);font-family: 'JetBrains Mono',serif">
            {{ obj.output.slice(19, 32) }}
          </span>
          <span style="color: rgba(255,246,0,0.99);font-family: 'JetBrains Mono',serif">
            {{ obj.output.slice(32, 33) }}
          </span>
        </div>
        <br/>
        <br/>
        <el-button type="primary" size="large" @click="handleClick">快速开始~ 🚀</el-button>
      </div>
    </div>

    <!--    下方区域-->
    <div class="down">
      <!--    布局-->
      <el-row>
        <!--    左下卡片-->
        <el-col :span="5">
          <div class="left_down_card" id="left_down">
            <el-card :body-style="{ padding: '0px' }" shadow="hover" style="border-radius: 10px">
              <div style="padding: 14px">
                <el-input
                    v-model="pageParam.header"
                    style="width: 200px"
                    placeholder="输入文章标题"
                    :prefix-icon="Search"
                />
                <el-button type="primary" style="margin-top: 5px" @click="search">搜索</el-button>
                <el-button type="warning" style="margin-top: 5px" @click="refresh">重置</el-button>
              </div>
            </el-card>
          </div>
        </el-col>
        <!--      中间的文章 每页五个-->
        <el-col :span="25" style="margin-left: 30px">
          <div class="middle" v-for="(article, index) in articles" :key="index">
            <a-card hoverable style="width: 850px" @click="router.push('/article/'+article.id)">
              <template #cover>
                <div style="height: 300px;">
                  <img
                      style="height: 100%;width: 100%;object-fit: cover"
                      alt="example"
                      :src=article.coverUrl
                  />
                </div>
              </template>
              <template #actions>
                <div style="display: flex;margin-left: 8px">
                  <a-tag :bordered="false" :color=item.color v-for="item in article.articleTags">{{ item.name }}</a-tag>
                </div>
              </template>
              <a-card-meta :title=article.header :description=article.description>
                <template #avatar>
                  <a-avatar :src="article.avatarUrl"/>
                </template>
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
        </el-col>
        <!--        右边的两个card-->
        <el-col :span="5" style="margin-left: 180px;margin-top: 20px">
          <!--          热门文章-->
          <div>
            <a-card title="热门帖子✨✨" style="width: 300px" hoverable>
              <a-flex :vertical="true" gap="small">
                <a-card style="width: 250px" type="inner" v-for="article in hotArticles"
                        @click="router.push('/article/'+article.id)">
                  <template #cover>
                    <img style="height:100px;width: 100%;object-fit: cover"
                         :src=article.coverUrl
                         alt="">
                  </template>
                  <a-card-meta :title=article.header>
                    <template #description>
                      <div style="display: flex; align-items: center;">
                        <EyeOutlined style="margin-right: 3px;"/>
                        <span>{{ article.views }}</span>
                      </div>
                    </template>
                  </a-card-meta>
                </a-card>
              </a-flex>
            </a-card>
          </div>
          <!--          标签页-->
          <div>
            <a-card title="帖子标签✨✨" style="width: 300px;margin-top: 50px">
              <a-tag :color="tag.color" v-for="tag in articleTagsForm" @click="router.push('/articleByTag/'+tag.id)">{{ tag.name }}</a-tag>
            </a-card>
          </div>
        </el-col>
      </el-row>

    </div>

  </div>
</template>

<script setup lang="ts">
import {reactive, onMounted, ref} from "vue";
import {ChatApi, ChatSubmitParam} from 'undraw-ui'
import {EyeOutlined} from '@ant-design/icons-vue';

//easy-js
import EasyTyper from "easy-typer-js";
import router from "../../router/router.ts";
import request from "../../apis/request.ts";
import emoji from "../../assets/emoji.ts";
import {Search} from "@element-plus/icons-vue";


//热门帖子相关
const hotArticles = ref([{
  header: "",
  views: "",
  coverUrl: '',
  id: ''
}])

const userChatId = ref(1)
//分页参数
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(5)
const pageParam = reactive({
  pageNum: pageNum,
  pageSize: pageSize,
  header: ''
})
//文章分类
const articleTagsForm = ref([])
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

// 计算属性
const obj = reactive({
  output: '',
  isEnd: false,
  speed: 120,
  singleBack: true,
  sleep: 0,
  type: 'rollback',
  backSpeed: 0,
  sentencePause: true,
})

function search() {
  request.get("/article/selectArticlePage", {
    params: pageParam
  }).then(res => {
    if (res.code === 200) {
      articles.value = res.data.rows
      total.value = res.data.total
      //console.log('success')
    } else {
      console.log(error)
    }
  })
}

function refresh() {
  pageParam.header = ''
  getArticleList()
  getArticleNums()
}

//聊天相关
const chatData = reactive<ChatApi[]>([
  {id: 2, username: 'HBUT', avatar: '1', content: '您好，智慧HBUT为您服务！请问出您感兴趣的话题！'},
])

const submit = ({clear, content}: ChatSubmitParam) => {

  chatData.push({id: 1, username: 'user', avatar: avatarUrl.value, content: content})
  request.post('/ai/chat?content=' + content)
      .then(res => {
        chatData.push({id: 2, username: 'HBUT', avatar: '', content: res.data})
        console.log(avatarUrl.value)
      })
  clear()
}

function getHotArticles() {
  request.get('/article/getHotArticles')
      .then(res => {
        if (res.code === 200) {
          hotArticles.value = res.data
        }
      })
}

//分页
function handlePageChange(newPage: number) {
  pageParam.pageNum = newPage
  getArticleList()
}

//获取文章分类
function getArticleTags() {
  request.get('/article/getArticleTags')
      .then(res => {
        articleTagsForm.value = res.data
      })
}


const handleClick = () => {
  // 这里使用原生JS实现滚动效果
  const scrollStep = 800; // 每次滚动的距离
  const currentScrollPosition = document.documentElement.scrollTop;
  const newScrollPosition = currentScrollPosition + scrollStep;
  window.scrollTo({top: newScrollPosition, behavior: 'smooth'});
}

const userId = ref(localStorage.getItem('userId'));
//头像
const avatarUrl = ref('')
//nickName
const nickName = ref('')
//签名
const sign = ref('')

//获取文章列表
function getArticleList() {
  request.get("/article/selectArticlePage", {
    params: pageParam
  }).then(res => {
    if (res.code === 200) {
      articles.value = res.data.rows
      //console.log('success')
    } else {
      console.log(error)
    }
  })
}

// 实例化
onMounted(() => {
      new EasyTyper(obj, 'System.out.println("Hello HBUT!")');
      request.get("/user/userProfile/" + userId.value)
          .then(res => {
            avatarUrl.value = res.data.avatarUrl
            nickName.value = res.data.nickName
            sign.value = res.data.sign
          })
      getArticleNums()
      //获取文章列表
      getArticleList()
      getArticleTags()
      //获取热门帖子列表
      getHotArticles()
    },
)

function getArticleNums() {
  //获取文章数量
  request.get("/article/getArticleCount")
      .then(res => {
        total.value = res.data
      })
}
</script>

<style scoped>
.welcome_pic {
  height: 880px;
  background-image: url("../../assets/background-light-f74c7dae.jpg");
  background-size: cover;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  /*-webkit-animation: moveUpDown 6s ease-in-out infinite;*/
}

.words {
  text-align: center;
}

@-webkit-keyframes moveUpDown {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.eHBUT {
  background: linear-gradient(to right, #ff8a00, #e52e71);
  -webkit-background-clip: text;
  color: transparent;
  opacity: 0;
  -webkit-animation: fadeIn 2s ease-in-out forwards;
}

.left_down_card {
  width: 250px;
  margin-top: 20px;
  margin-left: 30px;
}

.middle {
  margin-top: 20px;
}

.top {
  text-align: center;
}

.down {
  background-color: #f0f0f0;
}

.image {
  display: block;
  width: 100%;
}
</style>
