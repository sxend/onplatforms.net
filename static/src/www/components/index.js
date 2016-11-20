
import links from './links';
import header from './header';

const components = [
  links, header
];

export default {
  components: components,
  init: function(platform) {
    components.forEach(component => component(platform));
  }
}