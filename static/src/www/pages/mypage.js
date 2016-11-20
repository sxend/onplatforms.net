
import template from "../templates/mypage.html";

export default function(platform) {
  return {
    template: template,
    data: function() {
      return {
        userId: "",
        userName: "",
        pageArea: "mypage"
      };
    }
  }
}