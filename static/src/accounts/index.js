
let Vue = require('../../node_modules/vue/dist/vue.runtime.min');
let Skeleton = require('../../node_modules/skeleton-css/css/skeleton.css');
let Normalize = require('../../node_modules/skeleton-css/css/normalize.css');
let app = require('./app');

export let platform = {
  Vue: Vue,
  Skeleton: Skeleton,
  Normalize
};
app(platform);