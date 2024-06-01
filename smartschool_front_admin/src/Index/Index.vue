<script setup>

import {Document, Menu, Notebook, Plus, User} from "@element-plus/icons-vue";
</script>

<template>
  <div>
    <el-row class="menu">
      <el-col :span="3">
        <div class="left-menu">
          <h3 style="color: black;font-family: 微软雅黑,serif">智慧校园后台管理系统</h3>
          <el-menu router
          >
            <el-sub-menu index="1">
              <template #title>
                <el-icon><Menu/></el-icon>
                <span>文章用户管理</span>
              </template>
              <el-menu-item-group>
                <el-menu-item index='/articlePage'>文章概览</el-menu-item>
                <el-menu-item index='/userPage'><el-icon><User/></el-icon>用户管理</el-menu-item>
              </el-menu-item-group>
            </el-sub-menu>
            <el-sub-menu index="3">
              <template #title>
                <el-icon><document /></el-icon>
                <span>知识库管理</span>
              </template>
              <el-menu-item-group>
                <el-menu-item index='/knowledgeList'><el-icon><Notebook/></el-icon>知识库文件概览</el-menu-item>
                <el-menu-item index='/addKnowledgeFiles'><el-icon><Plus/></el-icon>添加知识库文件</el-menu-item>
              </el-menu-item-group>
            </el-sub-menu>
          </el-menu>
        </div>
      </el-col>
      <!--      右侧-->
      <el-col :span="20">
        <router-view/>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
.left-menu {
  height: 100vh;
  border-right: #2c3e50 1px solid;
}
</style>