<script setup lang="ts">
import {onMounted, reactive, ref} from "vue";
import request from "../../apis/request.ts";
import {ElMessage} from "element-plus";


//读取浏览器存储的用户ID
const userId = localStorage.getItem("userId")

const messages = ref([])
const getOneChatDetail = reactive({
  fromId: userId,
  toId: ''
})
//添加私信参数
const addChat = reactive({
  fromId: userId,
  toId: '1775449778696589313',
  content: ''
})
//私信对象的nickname
const userNickName = ref('')


//当前选中的人的id
const selectedChatUserId = ref('')


//左边消息列表
const LeftChatList = ref([

])


onMounted(() => {
  getLeftChatLists()
})

function getOneChatDetails() {
  request.get('/chat/oneChat', {
    params: getOneChatDetail
  }).then(res => {
    if (res.code === 200) {
      messages.value = res.data
      console.log(messages.value)
    }
  })
}

function sendChat() {
  request.post('/chat/sendChat', addChat)
      .then(res => {
        if (res.code === 200) {
          console.log('已发送')
          //刷新并清空
          addChat.content = ''
          getOneChatDetails()
        }
      })
}

//获取左边的对话列表
function getLeftChatLists() {
  request.get('chat/getLeftInfo', {
    params: {
      userId: userId
    }
  }).then(res => {
    if (res.code === 200) {
      LeftChatList.value = res.data
      selectedChatUserId.value = LeftChatList.value[0].userId
      getOneChatDetail.toId = LeftChatList.value[0].userId
      addChat.toId = LeftChatList.value[0].userId
      userNickName.value = LeftChatList.value[0].userNickName
      getOneChatDetails()
      LeftChatList.value.forEach(chat => {
        getUnReadMsgNumCount(chat.userId)
      })
    } else {
      ElMessage.error(res.msg)
    }
  })
}

//切换聊天对象
function changeChatUser(chat: any) {
  selectedChatUserId.value = chat.userId
  getOneChatDetail.toId = chat.userId
  userNickName.value = chat.userNickName
  addChat.toId = chat.userId
  getOneChatDetails()
}

const unReadMsgNum = ref<{ [key: string]: number }>({})

function getUnReadMsgNumCount(fromId: string) {
  request.get('/user/unReadMsgCountOne', {
    params: {toId: userId, fromId: fromId}
  }).then(res => {
    unReadMsgNum.value[fromId] = res.data
  })
}

//已读信息
function readMsg(fromId: string) {
  console.log(fromId)
  request.put('/chat/readMsg', {
    fromId: fromId,
    toId: userId
  }).then(() => {
    //刷新
    getUnReadMsgNumCount(fromId)
  })
}

</script>

<template>
  <div style="font-family: 得意黑,serif">
    <el-container>
      <el-aside width="200px" style="margin-top: 30px">
        <span style="color: cornflowerblue;margin-left: 4px">近期消息</span>
        <a-card :bordered="false"
                :class="{ 'selected': selectedChatUserId === chat.userId }"
                v-for="chat in LeftChatList" :key="chat.userId" style="width: 200px"
                @click="changeChatUser(chat);readMsg(chat.userId)">
          <div style="height: 50px">
            <div style="padding: 10px;display: flex">
              <el-image
                  style="width: 30px; height: 30px; border-radius: 50%;"
                  :src=chat.userAvatarUrl></el-image>
              <div style="margin-left: 6px;margin-top: 5px">{{ chat.userNickName }}</div>
              <div>
                <a-badge :count="unReadMsgNum[chat.userId]"/>
              </div>
            </div>
          </div>
        </a-card>

      </el-aside>
      <el-main>
        <el-card shadow="hover" style="margin-top: 28px">
          <div class="chat-container">
            <div class="message-list">
              <div style="margin-left: 600px;font-family: 得意黑,serif">您正在与 <span
                  style="color: darkgray;">{{ userNickName }}</span> 私信，请小心<span
                  style="color: red">上当受骗！</span></div>
              <div v-for="(message, index) in messages" :key="index"
                   :class="message.fromId === userId ? 'message you' : 'message other'">
                <div class="avatar-container">
                  <el-image
                      v-if="message.fromId === userId"
                      style="width: 30px; height: 30px; border-radius: 50%;"
                      :src="message.fromAvatarUrl">
                  </el-image>
                  <el-image
                      v-if="message.fromId !== userId"
                      style="width: 30px; height: 30px; border-radius: 50%;"
                      :src="message.fromAvatarUrl">
                  </el-image>
                </div>
                <div class="bubble">
                  {{ message.content }}
                </div>
              </div>
            </div>
            <div class="input-container">
              <el-input
                  v-model="addChat.content"
                  style="width: 1440px"
                  :autosize="{ minRows: 2, maxRows: 4 }"
                  type="textarea"
                  placeholder="请输入内容"
              />
              <el-button type="primary" @click="sendChat" style="margin-left: 20px">发送</el-button>
            </div>
          </div>
        </el-card>
      </el-main>
    </el-container>
  </div>
</template>

<style scoped>
.chat-container {
  width: 100%;
  display: flex;
  flex-direction: column;
  padding: 10px;
}

.message-list {
  display: flex;
  flex-direction: column;
  flex-wrap: nowrap;
  align-items: flex-start;
  overflow-y: auto;
  min-height: 700px;
  max-height: 700px;
}

.avatar-container {
  display: flex;
  align-items: center;
}

.message {
  display: flex;
  align-items: flex-end;
  margin-top: 15px;
  margin-bottom: 15px;
}

.bubble {
  padding: 10px;
  border-radius: 5px;
  max-width: 70%;
}

.other .avatar-container {
  margin-right: 10px;
}

.you {
  justify-content: flex-end; /* 将气泡对齐到右侧 */
  align-items: flex-end;
  margin-left: auto; /* 自动填充左边的空间，将气泡推到右边 */
}

.you .avatar-container {
  order: 2; /* 确保头像在气泡之后 */
  margin-left: 10px;
}

.you .bubble {
  background-color: #DCF8C6;
}

.other .bubble {
  background-color: #E0E0E0;
  align-self: flex-start;
}

.input-container {
  display: flex;
  padding: 10px;
}

input[type="text"] {
  flex-grow: 1;
  padding: 5px;
  margin-right: 5px;
}

.selected {
  background-color: #f0f0f0; /* 或者其他你希望的灰色 */
}
</style>