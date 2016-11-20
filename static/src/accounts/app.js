
export function app(platform) {
  setViewport();
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