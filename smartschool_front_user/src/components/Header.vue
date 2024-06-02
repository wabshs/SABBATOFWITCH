<template>
  <div>
    <el-menu
        class="el-menu"
        mode="horizontal"
        :ellipsis="false"
        :router="true"
    >
      <el-menu-item index="/index">
        <el-icon><HomeFilled /></el-icon>
        主页
      </el-menu-item>
      <div class="flex-grow"/>
      <el-menu-item index="/im"><el-icon><Comment /></el-icon>聊天室</el-menu-item>
      <el-menu-item :index="`/my/${userId}`"><el-icon><Avatar /></el-icon>个人中心</el-menu-item>
    </el-menu>
  </div>
</template>

<script setup lang="ts">
import {ref} from "vue";
import {Comment, Avatar,  HomeFilled} from "@element-plus/icons-vue";


const userId = ref(localStorage.getItem('userId'));

</script>

<style scoped>
.flex-grow {
  flex-grow: 1;
}
</style>
