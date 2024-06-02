<template>
  <div>
    <!--    左中右布局-->
    <el-row>
      <!--      左  个人名片？-->
      <el-col :span="4">
        <div class="ziliaoka">
          <el-card :body-style="{ padding: '0px'}" shadow="hover">
            <img
                :src="userProfile.avatarUrl"
                class="image"
                alt="avatar"
                style="background-size: cover"
            />
            <div style="padding: 14px">
              <!--          顶部-->
              <div class="top" style="text-align: center">
                <h3 style="font-weight: bold;color: cornflowerblue">{{userProfile.nickName}}</h3>
                <div>{{userProfile.sign}}</div>
              </div>
              <!--              底部-->
            </div>
          </el-card>
        </div>
      </el-col>
      <!--      中-->
      <el-col :span="15">
        <el-card v-if="temp===0" class="markdown" shadow="hover" :body-style="{'min-height':'800px'}">
          <!--          标题-->
          <div style="text-align: center">
            <h1>关于我</h1>
          </div>
          <!--            markdown部分-->
          <MdPreview :editorId="id" :modelValue="content"/>
        </el-card>
        <!--        收藏-->
        <el-card v-if="temp===1" class="markdown" shadow="hover" :body-style="{'min-height':'800px'}">
          <!--          标题-->
          <div style="text-align: center">
            <h1>我的收藏</h1>
          </div>
          <el-divider/>
          <div style="min-height: 700px">
            <el-row>
              <el-col :span="18">
                <el-link @click="changeToAboutMe" type="primary">< 返回关于我</el-link>
              </el-col>
              <el-col :span="6">
                <span style="color: slategray;font-family: 得意黑,serif;margin-left: 5px">最近更新于 {{
                    articleCollection[0].createTime
                  }} ·
              {{ total }} 个内容
            </span>
              </el-col>
            </el-row>

            <div style="margin-top: 5px">
              <el-card shadow="never" v-for="article in articleCollection">
                <el-row>
                  <el-col :span="20" style="align-content: center">
                    <h3 style="font-family: 得意黑,serif">{{ article.articleHeader }}</h3>
                  </el-col>
                  <el-col :span="4" style="align-content: center">
                    <p style="font-family: 得意黑,serif">收藏于 {{ article.createTime }}</p>
                  </el-col>
                </el-row>
              </el-card>
            </div>
          </div>
          <div style="margin-left: 450px">
            <el-pagination background
                           v-model:current-page="articleCollectionParams.pageNum"
                           @current-change="handlePageChange"
                           layout="prev, pager, next" :total="total"/>
          </div>
        </el-card>
      </el-col>
      <!--      右-->
      <el-col :span="4" class="edit_menu">
        <el-card shadow="hover">
          <template #header>
            <div>
              <span style="color: cadetblue;font-weight: bold">
                快捷导航
              </span>
            </div>
          </template>
          <!--          导航栏-->
          <div style="font-family: 得意黑,serif" v-if="userId===currentUserId">
            <el-row :gutter="10">
              <el-col :span="6">
                <div style="text-align: center;width: 65px;font-weight: bold">
                  <el-button @click="router.push({path:'/createArticle/'+userId})" type="success" :icon="EditPen"
                             circle/>
                  <p class="dao_hang">发布帖子</p>
                </div>
              </el-col>

              <el-col :span="6">
                <div style="text-align: center;width: 65px;font-weight: bold">
                  <a-badge :count=unReadMsgNum>
                    <el-button type="info" @click="router.push({path:'/chat/'})" :icon="Message" circle/>
                  </a-badge>
                  <p class="dao_hang">我的私信</p>
                </div>
              </el-col>

              <el-col :span="6">
                <div style="text-align: center;width: 65px;font-weight: bold">
                  <el-button @click="router.push({path:'/edit/'+userId})" type="primary" :icon="Tools" circle/>
                  <p class="dao_hang">我的资料</p>
                </div>
              </el-col>

              <el-col :span="6">
                <div style="text-align: center;width: 65px;font-weight: bold">
                  <el-button @click="changeToArticleCollection" type="warning" :icon="Star" circle/>
                  <p class="dao_hang">我的收藏</p>
                </div>
              </el-col>

              <el-col :span="6">
                <div style="text-align: center;width: 65px;font-weight: bold">
                  <el-button type="danger" :icon="Close" circle/>
                  <p class="dao_hang">退出登录</p>
                </div>
              </el-col>

            </el-row>
          </div>
          <div style="font-family: 得意黑,serif" v-if="userId!==currentUserId">
            <el-row :gutter="10">
              <el-col :span="6">
                <div style="text-align: center;width: 65px;font-weight: bold">
                  <el-button @click="sendMsg(userId,currentUserId)" type="primary" :icon="Promotion"
                             circle/>
                  <p class="dao_hang">发送私信</p>
                </div>
              </el-col>
            </el-row>
          </div>

        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import {onMounted, reactive, ref} from 'vue';
import {MdPreview} from 'md-editor-v3';
import 'md-editor-v3/lib/preview.css';
import request from "../../apis/request.ts";
import {Close, EditPen, Message, Promotion, Star, Tools} from "@element-plus/icons-vue";
import router from "../../router/router.ts";
import {useRoute} from "vue-router";
import {ElMessage} from "element-plus";


const temp = ref(0)

function changeToArticleCollection() {
  temp.value = 1
}

function changeToAboutMe() {
  temp.value = 0
}

const articleCollection = ref([
  {
    articleHeader: "",
    createTime: '',
    articleId: ""
  }
])


const id = 'preview-only';
const route = useRoute()
const userId = route.params.id
//当前登录用户的id
const currentUserId = localStorage.getItem("userId")
//未读消息
const unReadMsgNum = ref(0)
const articleCollectionParams = reactive({
  pageNum: 1,
  pageSize: 10,
  userId: userId
})
const total = ref(0)
const userProfile = ref({
  nickName: '',
  avatarUrl: '',
  sign: ''
})

function getArticleCollection() {
  request.get('/article/articleCollection', {
    params: articleCollectionParams
  }).then(res => {
    if (res.code === 200) {
      articleCollection.value = res.data.rows
      total.value = res.data.total
    }
  })
}

function getUserProfile() {
  request.get('/user/userProfile/' + userId)
      .then(res => {
        if (res.code === 200) {
          userProfile.value = res.data
        }
      })
}

let content = ref("")
onMounted(() => {
  request.get("/user/aboutMe/" + userId)
      .then(res => {
        if (res.code === 200) {
          content.value = res.data.aboutMe
        } else {
          ElMessage.error(res.msg)
        }
      })
  getUnReadMsgNum()
  getArticleCollection()
  getUserProfile()
});

function handlePageChange(newPage: number) {
  articleCollectionParams.pageNum = newPage
  getArticleCollection()
}

function sendMsg(user1: any, user2: any) {
  //先检查两个人有没有消息记录
  request.get('/chat/checkChatIsExist', {
    params: {user1: user1, user2: user2}
  }).then(res => {
    if (res.code === 200) {
      //有聊天记录直接跳转
      router.push('/chat')
    } else if (res.code === 201) {
      router.push('/chat')
      ElMessage.success('为您创建了聊天窗口~')
    } else {
      ElMessage.error(res.msg)
    }
  })
}

function getUnReadMsgNum() {
  request.get('/user/unReadMsgCount/' + userId)
      .then(res => {
        if (res.code === 200) {
          unReadMsgNum.value = res.data
        }
      })
}

</script>

<style scoped>

.dao_hang {
  color: rgb(128, 128, 128);
}

.edit_menu {
  margin-left: 25px;
  margin-top: 20px;
}

.image {
  width: 100%;
  display: block;
}

.bottom {
  margin-top: 10px;
}

.ziliaoka {
  margin-top: 20px;
  margin-left: 20px;
}

.markdown {
  margin-left: 50px;
  margin-top: 20px;
}
</style>
