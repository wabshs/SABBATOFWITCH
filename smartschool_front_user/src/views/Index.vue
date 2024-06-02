<template>
  <div>
    <el-container>
      <el-affix>
        <el-header class="header">
          <Header/>
        </el-header>
      </el-affix>

      <el-main style="padding: 0;min-height: 860px">
        <router-view/>
      </el-main>

      <!--      页脚-->
      <el-footer class="footer">
        智慧校园论坛系统
      </el-footer>
    </el-container>
    <el-backtop :right="100" :bottom="100" />
  </div>
</template>

<script setup lang="ts">


import Header from "../components/Header.vue";

</script>

<style scoped>
.footer {
  text-align: center;
  line-height: 70px;
}
.header{
  padding: 0;
}


</style>
