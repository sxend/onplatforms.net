
import template from "../templates/mypage.html";

export default function(platform) {
  return {
    template: template,
    data: function() {
      return Object.assign(platform.rootData, {
        userId: "",
        userName: "",
        pageArea: "mypage"
      });
    }
  }
}