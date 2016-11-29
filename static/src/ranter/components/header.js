import template from '../templates/header.html';

export default function(platform) {
  const component = platform.Vue.component("pf-header", {
    data: function () {
      return {
        pageArea: this.$parent.$data.pageArea ?  " - " + this.$parent.$data.pageArea : ""
      }
    },
    methods: {
      gotoTop: function() {
        platform.router.push("/");
      }
    },
    template: template
  });
}