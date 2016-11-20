

import pages from './pages';
import components from './components';

export function app(platform) {
  const Vue = platform.Vue;
  const VueRouter = platform.VueRouter;
  platform.rootData = {};
  components.init(platform);

  const routes = [
    { path: '/', component: pages.top(platform) },
    { path: '/mypage', component:  pages.mypage(platform) }
  ];
  const router = new VueRouter({
    routes: routes
  });
  window.data = platform.rootData;
  const vm = new Vue({
    router: router,
    template: '<div class="container"><router-view></router-view></div>'
  }).$mount('#main-contents');

}