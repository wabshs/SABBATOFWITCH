<script setup lang="ts">
import {reactive} from "vue";
import request from "../../apis/request.ts";
import router from "../../router/router.ts";
import {ElMessage} from "element-plus";

const loginForm = reactive({
  userName: "",
  password: ""
})


function login() {
  request.post("/user/userLogin", loginForm)
      .then(res => {
        if (res.code === 200) {
          //登录成功把用户id存起来
          localStorage.setItem("userId", res.data.userId)
          localStorage.setItem("user", JSON.stringify(res.data))
          router.push("/index")
              .then(() => location.reload())
          ElMessage.success("登录成功!")
        } else {
          ElMessage.error(res.msg)
        }
      })
}
</script>

<template>
  <div class="body1">
    <div class="box">
      <h2>欢迎使用</h2>
      <div class="input-box">
        <label>用户名</label>
        <input v-model="loginForm.userName" type="text"/>
      </div>
      <div class="input-box">
        <label>密码</label>
        <input v-model="loginForm.password" type="password"/>
      </div>
      <div class="btn-box">
        <a href="#">忘记密码?</a>
        <div>
          <el-button type="primary" @click="login">登录</el-button>
          <el-button type="success">注册</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
* {
  margin: 0;
  padding: 0;
}

.body1 {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: url("src/assets/背景1.png") no-repeat;
  background-size: cover;
}

.box {
  border-radius: 20px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  width: 350px;
  height: 380px;
  border-top: 1px solid rgba(255, 255, 255, 0.5);
  border-left: 1px solid rgba(255, 255, 255, 0.5);
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
  border-right: 1px solid rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  background: rgba(50, 50, 50, 0.2);
}

.box > h2 {
  color: rgba(255, 255, 255, 0.9);
  margin-bottom: 20px;
}

.box .input-box {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: start;
  margin-bottom: 10px;
}

.box .input-box > label {
  margin-bottom: 5px;
  color: rgba(255, 255, 255, 0.9);
  font-size: 13px;
}

.box .input-box > input {
  box-sizing: border-box;
  color: rgba(255, 255, 255, 0.9);
  font-size: 14px;
  height: 35px;
  width: 250px;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 5px;
  transition: 0.2s;
  outline: none;
  padding: 0 10px;
}

.box .input-box > input:focus {
  border: 1px solid rgba(255, 255, 255, 0.8);
}

.box .btn-box {
  width: 250px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: start;
}

.box .btn-box > a {
  font-size: 13px;
  text-decoration: none;
  color: rgba(255, 255, 255, 0.9);
  transition: 0.2s;
  width: 250px;
  text-align: end;
}

.box .btn-box > a:hover {
  color: rgba(255, 255, 255, 1);
}

.box .btn-box > div {
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: start;
  margin-top: 20px;
}

.box .btn-box > div > button {
  width: 120px;
  height: 35px;
  border-radius: 5px;
  transition: 0.2s;
}


.box .btn-box > div > button:nth-of-type(2) {
  margin-left: 10px;
}


</style>