<script setup lang="ts">
import {reactive, ref} from "vue";
import request from "../../apis/request.ts";
import {ElMessage} from "element-plus";
import router from "../../router/router.ts";


const form = reactive({
  userName: '',
  password: '',
  nickName: '',
  email: '',
  authCode: ''
})

const countdown = ref(0)


function startCountdown() {
  //发送验证码
  request.post("/mail/sendAuthCode?to=" + form.email)
      .then(res => {
        if (res.code === 200) {
          ElMessage.success("发送成功")
        } else {
          ElMessage.error(res.msg)
        }
      })
  countdown.value = 60; // 设置倒计时初始值为60秒
  const timer = setInterval(() => {
    if (countdown.value > 0) {
      countdown.value--; // 每秒减少1
    } else {
      clearInterval(timer); // 倒计时结束，清除定时器
    }
  }, 1000);
}

//注册
function userRegister() {
  request.post("/user/userRegister", form)
      .then(res => {
        if (res.code === 200) {
          ElMessage.success("注册成功!")
          router.push("/login")
        } else {
          ElMessage.error(res.msg)
        }
      })
}
</script>

<template>
  <div class="all">
    <div class="box">
      <h1>欢迎注册</h1>
      <el-form label-width="auto" style="max-width: 360px">
        <el-form-item label="用户名">
          <el-input placeholder="您登录的凭证" v-model="form.userName"/>
        </el-form-item>
        <el-form-item label="昵称">
          <el-input placeholder="您在本网站展示的名字" v-model="form.nickName"/>
        </el-form-item>
        <el-form-item label="密码">
          <el-input placeholder="请输入密码" type="password" show-password v-model="form.password"/>
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input placeholder="请输入邮箱" v-model="form.email">
          </el-input>
        </el-form-item>
        <el-form-item label="验证码">
          <el-input placeholder="60s获取一次" style="width: calc(60% - 30px);" v-model="form.authCode"/>
          <el-button type="primary" style="margin-left: 10px" :disabled="countdown > 0" @click="startCountdown">
            {{ countdown > 0 ? countdown + 's 后重新获取' : '获取验证码' }}
          </el-button>
        </el-form-item>
        <el-button style="margin-left: 300px" type="success" @click="userRegister">注册</el-button>
      </el-form>
    </div>
  </div>
</template>

<style scoped>
.all {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: url("/src/assets/background-f671693c.jpg");
  background-size: cover;
}

.box {
  padding: 30px;
  border-radius: 20px;
  width: 430px;
  height: 360px;
  background: white;
  text-align: center;
}
</style>