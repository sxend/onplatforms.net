import template from '../templates/header.html';

export default function(platform) {
  const component = platform.Vue.component("pf-header", {
    data: function () {
      return Object.assign(platform.rootData, {
        pageArea: platform.rootData.pageArea ?  " - " + platform.rootData.pageArea : ""
      })
    },
    template: template
  });
}