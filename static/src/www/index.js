const Vue = require('../../node_modules/vue/dist/vue.min');
const VueRouter = require('../../node_modules/vue-router/dist/vue-router.min');
const Skeleton = require('../../node_modules/skeleton-css/css/skeleton.css');
const Normalize = require('../../node_modules/skeleton-css/css/normalize.css');
import {app} from './app';

Vue.use(VueRouter);



const platform = {
  Vue: Vue,
  VueRouter: VueRouter,
  Skeleton: Skeleton,
  Normalize: Normalize
};

app(platform);