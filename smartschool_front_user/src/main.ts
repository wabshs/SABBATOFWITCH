import {createApp} from 'vue'
import './style.css'
import App from './App.vue'
import Antd from 'ant-design-vue';
import 'ant-design-vue/dist/reset.css';
import router from './router/router.ts'
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css'
import "./assets/font/font.css"
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import request from "./apis/request.ts";



const app = createApp(App)
import UndrawUi from 'undraw-ui'
import 'undraw-ui/dist/style.css'

app.use(Antd)
    .use(router)
    .use(ElementPlus)
    .use(UndrawUi)
    .mount('#app')

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}

app.config.globalProperties.$request = request