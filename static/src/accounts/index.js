
let Vue = require('../../node_modules/vue/dist/vue.runtime.min');
let VueRouter = require('../../node_modules/vue-router/dist/vue-router.min');
let Skeleton = require('../../node_modules/skeleton-css/css/skeleton.css');
let Normalize = require('../../node_modules/skeleton-css/css/normalize.css');
import {app} from './app';

Vue.use(VueRouter);

export let platform = {
  Vue: Vue,
  Skeleton: Skeleton,
  Normalize: Normalize
};

app(platform);