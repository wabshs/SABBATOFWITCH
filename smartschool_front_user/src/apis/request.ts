import axios from "axios";
import {ElMessage} from "element-plus";


const request = axios.create({
    baseURL: 'http://localhost:8081/api',
    timeout: 15000
})

let user = localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : null;
//请求拦截器
request.interceptors.request.use(config => {

        //上传文件的时候删了请求头
        if (config?.url?.includes('/file/uploadPic')) {
            delete config.headers['Content-Type']
        } else {
            config.headers['Content-Type'] = 'application/json;charset=utf-8';
        }

        if (user) {
            config.headers['token'] = user.token;  // 设置请求头
        }
        return config
    }, error => {
        return Promise.reject(error)
    }
);

request.interceptors.response.use(
    response => {
        let res = response.data
        if (response.config.responseType === 'blob') {
            return res
        }
        // 兼容服务端返回的字符串数据
        if (typeof res === 'string') {
            res = res ? JSON.parse(res) : res
        }
        if (res.code === 401) {
            ElMessage.error("身份验证失败，请重新登陆")
        }
        return res;
    },
    error => {
        console.log('err' + error)
        return Promise.reject(error)
    }
)

export default request