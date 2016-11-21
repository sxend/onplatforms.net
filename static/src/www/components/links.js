
import template from '../templates/links.html';

export default function(platform) {
  const component = platform.Vue.component("pf-links", {
    template: template,
    data: function () {
      return {
        param: JSON.stringify({
          returnTo: location.href
        })
      }
    },
    methods: {
      signup: function() {
        let form = document.forms.namedItem("signup");
        form.setAttribute("action", form.getAttribute("action") + "?returnTo=" + encodeURIComponent(location.href));
        form.submit();
      }
    }
  });
}