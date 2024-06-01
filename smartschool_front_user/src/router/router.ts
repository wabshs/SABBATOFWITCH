import {createRouter, createWebHistory, RouteRecordRaw} from "vue-router";


//配置路由
const routes: Array<RouteRecordRaw> = [
    {
        path: '/',
        name: 'Index',
        component: () => import("../views/Index.vue"),
        redirect: 'index',
        children: [
            {path: 'index', name: 'Index', component: () => import('../views/pub/Welcome.vue')},
            {path: 'my/:id', name: 'My', component: () => import('../views/my/My.vue')},
            {path: 'edit/:id', name: 'Edit', component: () => import('../views/my/EditAboutMe.vue')},
            {path: 'article/:id', name: 'Article', component: () => import('../views/pub/Article.vue')},
            {
                path: 'createArticle/:id',
                name: 'CreateArticle',
                component: () => import('../views/pub/CreateArticle.vue')
            },
            {path: 'login', name: 'Login', component: () => import('../views/pub/Login.vue')},
            {path: 'register', name: 'Register', component: () => import('../views/pub/Register.vue')},
            {path: 'test', name: 'Test', component: () =>import('../views/pub/Test.vue')},
            {path: 'im', name: 'Im', component: () =>import('../views/pub/Im.vue')},
            {path: 'chat/', name: 'Chat', component: () =>import('../views/pub/Chat.vue')},
            {path: 'articleByTag/:tag', name: 'ArticleByTag', component: () =>import('../views/pub/ArticleTag.vue')},

        ]
    }
]

//返回router示例
const router = createRouter({
    history: createWebHistory(),
    routes,
});

//导出路由
export default router
