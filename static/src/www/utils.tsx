export function setViewport(content: string) {
    const metalist = document.getElementsByTagName('meta');
    let hasMeta = false;
    for(var i = 0; i < metalist.length; i++) {
        let name = metalist.item(i).getAttribute('name');
        if(name && name.toLowerCase() === 'viewport') {
            metalist.item(i).setAttribute('content', content);
            hasMeta = true;
            break;
        }
    }
    if(!hasMeta) {
        let meta = document.createElement('meta');
        meta.setAttribute('name', 'viewport');
        meta.setAttribute('content', content);
        document.getElementsByTagName('head')[0].appendChild(meta);
    }
}