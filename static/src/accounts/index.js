
let Vue = require('../../node_modules/vue/dist/vue.runtime.min');
let VueRouter = require('../../node_modules/vue-router/dist/vue-router.min');
let Vuex = require('../../node_modules/vuex/dist/vuex.min');
let Skeleton = require('../../node_modules/skeleton-css/css/skeleton.css');
let Normalize = require('../../node_modules/skeleton-css/css/normalize.css');

Vue.use(VueRouter);
Vue.use(Vuex);

import {app} from './app';

export let platform = {
  Vue: Vue,
  Skeleton: Skeleton,
  Normalize: Normalize
};

app(platform);