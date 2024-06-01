import {createRouter, createWebHistory, createWebHashHistory, RouteRecordRaw} from "vue-router";


const routes: Array<RouteRecordRaw> = [
    {
        path: '/',
        name: 'Index',
        component: () => import("../Index/Index.vue"),
        children: [
            {path: '/articlePage', name: 'ArticlePage', component: () => import('../components/ArticlePage.vue')},
            {path: '/userPage', name: 'User', component: () => import('../components/UserPage.vue')},
            {path: '/knowledgeList', name: 'KnowledgeList', component: () => import('../components/KnowledgeList.vue')},
            {path: '/knowledgeList', name: 'KnowledgeList', component: () => import('../components/KnowledgeList.vue')},
            {path: '/addKnowledgeFiles', name: 'AddKnowledgeFiles', component: () => import('../components/AddKnowledgeFile.vue')},
        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes,
});

export default router