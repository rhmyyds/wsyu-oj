import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      redirect: "/c-oj/home/question"
    },
    {
      path: "/c-oj/home",
      name: "home",
      redirect: "/c-oj/home/question",
      component: () => import("@/views/Home.vue"),
      children: [
        {
          path: 'exam',
          name: 'exam',
          component: () => import('@/views/Exam.vue'),
          meta: {showBanner: true}  // 元数据，可以用来可以用来展示非路由页面里面的内容是否展示
        },
        {
          path: 'question',
          name: 'question',
          component: () => import('@/views/Question.vue'),
          meta: {showBanner: true}
        },
        {
          path: 'user/exam',
          name: 'userExam',
          component: () => import('@/views/UserExam.vue'),
          meta: {showBanner: false}
        },
        {
          path: 'user/message',
          name: 'userMessage',
          component: () => import('@/views/UserMessage.vue'),
          meta: {showBanner: false}
        },
        {
          path: 'user/detail',
          name: 'userDetail',
          component: () => import('@/views/UserDetail.vue'),
          meta: {showBanner: false}
        },
      ]
    },
    {
      path: "/c-oj/login",
      name: "login",
      component: () => import("@/views/Login.vue"),
    },
    {
      path: "/c-oj/answer",
      name: "answer",
      component: () => import("@/views/Answer.vue"),
    },
  ],
})

export default router
