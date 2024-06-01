<template>
  <u-chat :user-id="userChatId" :emoji="emoji" :data="data" @submit="submit"></u-chat>
</template>
<script setup lang="ts">
import {reactive, ref} from 'vue'
import {ChatApi, ChatSubmitParam} from 'undraw-ui'

import emoji from '../../assets/emoji.ts'
import request from "../../apis/request.ts";

const userChatId = ref(1)

const data = reactive<ChatApi[]>([
  {id: 2, username: 'HBUT', avatar: '', content: '您好，智慧HBUT为您服务！请问出您感兴趣的话题！'},
])

const submit = ({clear, content}: ChatSubmitParam) => {

  data.push({id: 1, username: 'user', avatar: '', content: content})
  request.post('/ai/chat?content=' + content)
      .then(res => {
        data.push({id: 2, username: 'HBUT', avatar: '', content: res.data})
      })
  clear()
}

</script>

<style lang="scss" scoped></style>
