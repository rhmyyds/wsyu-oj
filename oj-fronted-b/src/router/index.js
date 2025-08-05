import { createRouter, createWebHistory } from 'vue-router'
import { getToken } from '@/utils/cookie'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/oj/login',  // url访问路径
      name: 'login',
      component: () => import('@/views/Login.vue')  // 访问到的组件路径
    },
    {
      path: '/',  
      redirect:'/oj/login'
    },
    {
      path: '/oj/layout',
      name: 'layout',
      redirect: '/oj/layout/cuser',
      component: () => import('@/views/Layout.vue'),  
      children: [
        {
          path: 'cuser',
          name: 'cuser',
          component: () => import('@/views/Cuser.vue')  
        },
        {
          path: 'exam',
          name: 'exam',
          component: () => import('@/views/Exam.vue')  
        },
        {
          path: 'question',
          name: 'question',
          component: () => import('@/views/Question.vue')
        },
        {
          path: 'updateExam',
          name: 'updateExam',
          component: () => import('@/views/UpdateExam.vue')
        }
      ]
    },

  ],
})

router.beforeEach((to, from, next) => {
  if (getToken()) {
    /* token存在 */
    if (to.path === '/oj/login') {
      next({ path: '/oj/layout/cuser' })
    } else {
      next()
    }
  } else {
    if (to.path !== '/oj/login') {
      next({
        path: '/oj/login'
      })
    } else {
      next()
    }
  }
})

export default router
