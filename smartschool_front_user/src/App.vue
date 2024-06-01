<script setup lang="ts">
import {useDark} from "@vueuse/core";

useDark({
  selector: 'html',
  attribute: 'class',
  valueDark: 'dark',
  valueLight: 'light'
})


</script>


<template>
  <div id="app">
    <router-view/>
  </div>
</template>



<style scoped>
#app {
  ::-webkit-scrollbar {
    display: none;
  }
}
</style>
