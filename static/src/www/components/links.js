
import template from '../templates/links.html';

export default function(platform) {
  const component = platform.Vue.component("pf-links", {
    template: template
  });
}