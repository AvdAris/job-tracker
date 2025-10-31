import { createRouter, createWebHistory } from 'vue-router';
import Login from '../views/Login.vue';
import Applications from '../views/Applications.vue';
import Register from '../views/Register.vue'

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: Login },
  { path: '/register', component: Register },
  { path: '/applications', component: Applications },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
