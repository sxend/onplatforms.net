
export function app(platform) {
  setViewport();
  let btn = document.querySelector("#rant-btn");
  let content = document.querySelector("#rant-content");
  if(btn && content) {
    btn.onclick = function() {
      let xhr = new XMLHttpRequest();
      xhr.open("POST", "/rant");
      xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
      xhr.send(JSON.stringify({content: content.value}));
    }
  }
}

function setViewport() {
  const metalist = document.getElementsByTagName('meta');
  let hasMeta = false;
  for(var i = 0; i < metalist.length; i++) {
    let name = metalist[i].getAttribute('name');
    if(name && name.toLowerCase() === 'viewport') {
      metalist[i].setAttribute('content', 'width=device-width,initial-scale=1.0');
      hasMeta = true;
      break;
    }
  }
  if(!hasMeta) {
    let meta = document.createElement('meta');
    meta.setAttribute('name', 'viewport');
    meta.setAttribute('content', 'width=device-width,initial-scale=1.0');
    document.getElementsByTagName('head')[0].appendChild(meta);
  }
}