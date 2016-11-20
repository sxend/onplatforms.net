

import template from "../templates/top.html";

export default function(platform) {
  return {
    template: template,
    data: function() {
      return {
        pageArea: ""
      }
    }
  };
};