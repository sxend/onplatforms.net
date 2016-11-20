

import pages from './pages';
import components from './components';
import styles from './styles/index.scss';

export function app(platform) {
  const Vue = platform.Vue;
  const VueRouter = platform.VueRouter;
  setViewport();

  platform.rootData = {};
  components.init(platform);

  const routes = platform.routes = [
    { path: '/', component: pages.top(platform) },
    { path: '/mypage', component:  pages.mypage(platform) }
  ];
  const router = platform.router = new VueRouter({
    routes: routes
  });
  const vm = platform.rootVM = new Vue({
    router: router,
    template: '<div class="container"><router-view></router-view></div>'
  }).$mount('#main-contents');
}

function setViewport() {
  const metalist = document.getElementsByTagName('meta');
  let hasMeta = false;
  for(var i = 0; i < metalist.length; i++) {
    let name = metalist[i].getAttribute('name');
    if(name && name.toLowerCase() === 'viewport') {
      metalist[i].setAttribute('content', 'width=device-width,initial-scale=1.0');
      hasMeta = true;
      break;
    }
  }
  if(!hasMeta) {
    let meta = document.createElement('meta');
    meta.setAttribute('name', 'viewport');
    meta.setAttribute('content', 'width=device-width,initial-scale=1.0');
    document.getElementsByTagName('head')[0].appendChild(meta);
  }
}