<template>
  <div>
    <el-menu
        class="el-menu"
        mode="horizontal"
        :ellipsis="false"
        :router="true"
    >
      <el-menu-item index="/index">
        <img src="/src/assets/vue.svg"
             style="width: 100px"
             alt="logo"
        />
      </el-menu-item>
      <div class="flex-grow"/>
      <el-menu-item index="1">xxx</el-menu-item>
      <el-menu-item :index="`/my/${userId}`">个人中心</el-menu-item>
    </el-menu>
  </div>
</template>

<script setup lang="ts">
import {ref} from "vue";

const userId = ref(localStorage.getItem('userId'));

</script>

<style scoped>
.flex-grow {
  flex-grow: 1;
}
</style>
