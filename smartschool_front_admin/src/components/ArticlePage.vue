<script setup>
import {onMounted, reactive, ref} from "vue";
import {ChatDotSquare, Delete, InfoFilled, Refresh, Search} from "@element-plus/icons-vue";
import {ElMessage} from "element-plus";
import request from "../request/request.js";

const tableData = ref([])
onMounted(() => {
  articlePage()
})

//分页查询
const pageParams = reactive({
  pageNum: 1,
  pageSize: 10,
  header: '',
  sortBy: ''
})

function refresh() {
  pageParams.sortBy = ''
  pageParams.header = ''
  pageParams.pageNum = 1
  pageParams.pageSize = 10
  articlePage()
}

const total = ref(0)

//分页
function handlePageChange(newPage) {
  pageParams.pageNum = newPage
  articlePage()
}

function articlePage() {
  request.get('/admin/articlePage', {
    params: pageParams
  }).then(res => {
    if (res.code === 200) {
      tableData.value = res.data.rows
      total.value = res.data.total
    }
  })
}

//文章操作
const dialogVisible = ref(false)
const deleteArticleId = ref('')

function openDialog(id) {
  deleteArticleId.value = id
  dialogVisible.value = true
}

function deleteArticleById() {
  request.delete('/admin/deleteArticle/' + deleteArticleId.value).then(res => {
    if (res.code === 200) {
      dialogVisible.value = false
      ElMessage.success('删除成功!')
    } else {
      ElMessage.error(res.msg)
    }
  })
}

//评论管理
const commentsTotal = ref(0)
const CommentDialogVisible = ref(false)
const commentsData = ref([])
const currentArticleId = ref('')
const CommentsPageParams = reactive({
  pageNum: '1',
  pageSize: '5',
  id: ''
})

function getArticleComments(id) {
  CommentsPageParams.id = id
  request.get('/admin/getCommentsById', {
    params: CommentsPageParams
  }).then(res => {
    if (res.code === 200) {
      commentsData.value = res.data.rows
      commentsTotal.value = res.data.total
    }
  })
}

function openCommentsDialog(id) {
  currentArticleId.value = id
  getArticleComments(currentArticleId)
  CommentDialogVisible.value = true
}

//评论分页
function commentHandlePageChange(newPage) {
  CommentsPageParams.pageNum = newPage
  getArticleComments(currentArticleId)
}

//删除评论
function deleteCommentById(id) {
  request.delete('/admin/deleteCommentById/' + id)
      .then(res => {
        if(res.code === 200) {
          ElMessage.success('删除成功!')
          getArticleComments(currentArticleId)
        } else {
          ElMessage.error('操作失败')
        }
      })
}

</script>

<template>
  <div style="margin-top: 20px">
    <div style="margin-top: 20px;margin-left: 10px">
      <el-input
          v-model="pageParams.header"
          placeholder="请输入文章标题"
          style="width: 240px;"
          :suffix-icon="Search">
      </el-input>
      <el-radio-group style="margin-left: 15px" @change="articlePage" v-model="pageParams.sortBy">
        <el-radio :value="0">按时间排序</el-radio>
        <el-radio :value="1">按热度排序</el-radio>
      </el-radio-group>
      <el-button style="margin-left: 15px" type="primary" :icon="Search" @click="articlePage">搜索</el-button>
      <el-button type="warning" :icon="Refresh" @click="refresh">重置</el-button>
    </div>
    <el-table :data="tableData" style="width: 100%;min-height: 500px;margin-left: 50px">
      <el-table-column prop="id" label="文章id"/>
      <el-table-column prop="header" label="标题"/>
      <el-table-column prop="createTime" label="发布时间"/>
      <el-table-column prop="views" label="浏览量"/>
      <el-table-column prop="action" label="操作">
        <template #default="scope">
          <el-button type="warning" :icon="ChatDotSquare" @click="openCommentsDialog(scope.row.id)">评论管理</el-button>
          <el-button type="danger" :icon="Delete" @click="openDialog(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div>
      <el-pagination
          :default-page-size="10"
          v-model:current-page="pageParams.pageNum"
          @current-change="handlePageChange"
          background layout="prev, pager, next" :total="total" style="margin-left: 700px"/>
    </div>

    <!--    删除确认弹窗-->
    <el-dialog
        v-model="dialogVisible"
        title="确认"
        width="500"
    >
      <span>确定删除此文章？</span>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="deleteArticleById">
            确认
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!--    评论管理弹窗-->
    <el-dialog
        v-model="CommentDialogVisible"
        title="此文章下的评论"
        width="1000"
    >
      <el-table :data="commentsData">
        <el-table-column prop="content" label="内容"/>
        <el-table-column prop="userName" label="评论人"/>
        <el-table-column prop="createTime" label="发布时间"/>
        <el-table-column prop="action" label="操作">
          <template #default="scope">
            <el-popconfirm
                confirm-button-text="是"
                cancel-button-text="否"
                :icon="InfoFilled"
                icon-color="#626AEF"
                title="确定要删除此条评论？"
                @confirm="deleteCommentById(scope.row.id)"
            >
              <template #reference>
                <el-button type="danger" :icon="Delete">删 除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
          :default-page-size="5"
          v-model:current-page="pageParams.pageNum"
          @current-change="commentHandlePageChange"
          background layout="prev, pager, next" :total="commentsTotal" style="margin-left: 400px;margin-top: 10px"/>
    </el-dialog>
  </div>
</template>

<style scoped>

</style>