
import index from './components/index';
import mypage from './components/mypage';

export function app(platform) {
  const Vue = platform.Vue;
  const VueRouter = platform.VueRouter;

  const routes = [
    { path: '/', component: index },
    { path: '/mypage', component:  mypage }
  ];

  const router = new VueRouter({
    routes: routes
  });
  const vm = new Vue({
    router: router,
    template: '<router-view></router-view>'
  }).$mount('#main-contents');
  router.push("/");
}