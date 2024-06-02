<script setup lang="ts">
import {onMounted, reactive, ref} from "vue";
import request from "../apis/request.ts";
import {useRoute} from "vue-router";
import {ElMessage} from "element-plus";

const route = useRoute();
const userId = ref(localStorage.getItem('userId'));
const comments = ref([])
const articleId = route.params.id;
//分页参数
const pageParam = reactive({
  pageNum: 1,
  pageSize: 5,
  articleId: articleId
})
//提交评论参数
const commentForm = reactive({
  articleId: articleId,
  content: '',
  createBy: userId
})
//提交回复参数
const commentReplyForm = reactive({
  articleId: articleId,
  content: '',
  createBy: userId,
  rootId: '',
  toCommentId: '',
  toCommentUserId: '',
})
const total = ref(0)
const dialogVisible = ref(false)

const noticeParams = reactive({
  userId: userId,
  toUserId: '',
  articleId: articleId
})

onMounted(() => {
  getCommentList()
})

function getCommentList() {
  request.get("/comment/commentList", {
    params: pageParam
  }).then(res => {
    comments.value = res.data.rows
    total.value = res.data.total
  })
}

// 提交评论
function submitComment() {
  if (commentForm.content === '') {
    ElMessage.error('请输入内容！')
  } else {
    request.post("/comment", commentForm)
        .then(res => {
          if (res.code === 200) {
            ElMessage.success("评论成功~")
            //清除评论框
            commentForm.content = ''
            //加载
            getCommentList()
            //location.reload()
          } else {
            ElMessage.error(res.msg)
          }
        })
  }

}

//通知回复
function noticeUser() {
  request.post('/comment/noticeUser',noticeParams)
      .then(res=>{
        if(res.code === 200) {
          console.log('邮件通知成功!')
        }
      })
}


// 提交回复
function initSubmitReplyForm(rootId: string, toCommentId: string, toCommentUserId: string) {
  dialogVisible.value = true
  commentReplyForm.toCommentId = toCommentId
  commentReplyForm.toCommentUserId = toCommentUserId
  commentReplyForm.rootId = rootId
  noticeParams.toUserId = toCommentUserId
}

function submitReplyForm() {
  request.post("/comment", commentReplyForm)
      .then(res => {
        if (res.code === 200) {
          ElMessage.success("回复成功~")
          //清除评论框
          dialogVisible.value = false
          commentReplyForm.content = ''
          //加载
          getCommentList()
          noticeUser()

        } else {
          ElMessage.error(res.msg)
        }
      })
}

//取消回复
function cancelReply() {
  dialogVisible.value = false
  commentReplyForm.content = ''
}

//切换页数
function handlePageChange(newPage: number) {
  pageParam.pageNum = newPage
  getCommentList()
}
</script>

<!-- 留言评论模块 -->
<template>
  <div style="margin: 30px 0">
    <div style="margin: 10px 0">
      <div style="border-bottom: 1px solid orangered;padding: 10px 0;font-size: 20px">评论 | 共{{ total }}条</div>
      <div style="padding: 10px 0">
        <el-input size="small" type="textarea" v-model="commentForm.content"></el-input>
      </div>
      <div style="text-align: right">
        <el-button type="primary" @click="submitComment">评论</el-button>
      </div>
    </div>

    <!--    评论列表-->
    <div style="font-family: 得意黑,serif;font-size: 16px">
      <div v-for="item in comments" :key="item.id" style="border-bottom: 1px solid #ccc;padding: 10px 0">
        <div style="display: flex">
          <div style="width: 100px; text-align: center">
            <a :href="'http://127.0.0.1:5173/my/'+item.createBy">
              <el-image style="width: 50px;height: 50px;border-radius: 50%" :src="item.avatarUrl"></el-image>
            </a>
          </div> <!-- 头像-->
          <div style="flex: 1;font-size: 14px;padding: 5px 0;line-height: 25px">
            <b>{{ item.nickName }} ：</b>
            <span style="color: rgb(128,128,128)">{{ item.content }}</span>
            <div style="display: flex;line-height: 20px;margin-top: 8px">
              <div style="width: 200px">
                <span style="color: rgb(128,128,128)">{{ item.createTime }}</span>
              </div>
              <div style="text-align: right;flex: 1;">
                <el-button style="margin-left: 5px" text @click="initSubmitReplyForm(item.id,item.id,item.createBy)">回复
                </el-button>
                <el-button style="color: red" text type="danger" v-if="userId===item.createBy">删除</el-button>
              </div>
            </div>
          </div> <!-- 内容-->
        </div>
        <div v-if="item.children.length" style="padding-left: 50px">
          <div v-for="subItem in item.children" :key="subItem.id" style="background-color: #f0f0f0">
            <div style="display: flex">
              <div style="width: 100px; text-align: center">
                <a :href="'http://127.0.0.1:5173/my/'+subItem.createBy">
                  <el-image style="width: 50px;height: 50px;border-radius: 50%" :src="subItem.avatarUrl"></el-image>
                </a>
              </div> <!-- 头像-->
              <div style="flex: 1;font-size: 14px;padding: 5px 0;line-height: 25px">
                <b>{{ subItem.nickName }} ：</b>
                <b v-if="subItem.toCommentUserName">回复 @{{ subItem.toCommentUserName }} </b>
                <span style="color: rgb(128,128,128);margin-left: 10px">{{ subItem.content }}</span>
                <div style="display: flex;line-height: 20px;margin-top: 8px">
                  <div style="width: 200px">
                    <span style="color: rgb(128,128,128)">{{ subItem.createTime }}</span>
                  </div>
                  <div style="text-align: right;flex: 1;">
                    <el-button style="margin-left: 5px" text
                               @click="initSubmitReplyForm(item.id,subItem.id,subItem.createBy)">回复
                    </el-button>
                    <el-button style="color: red" text type="danger" v-if="userId===subItem.createBy">删除</el-button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div style="margin-left: 450px;margin-top: 10px">
      <el-pagination
          :default-page-size="5"
          v-model:current-page="pageParam.pageNum"
          @current-change = handlePageChange
          background layout="prev, pager, next" :total=total></el-pagination>
    </div>

    <el-dialog v-model="dialogVisible" width="50%">
      <el-form label-width="80px" size="small">
        <el-form-item label="回复内容">
          <el-input type="textarea" v-model="commentReplyForm.content" placeholder="请输入回复"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" style="text-align: right">
        <el-button @click="cancelReply">取消</el-button>
        <el-button type="primary" @click="submitReplyForm">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<style scoped>

</style>