import "./styles/index.scss";

import * as React from "react";
import * as ReactDOM from "react-dom";

import {Hello} from "./components/Hello";

function setViewport() {
    const metalist = document.getElementsByTagName('meta');
    let hasMeta = false;
    for(var i = 0; i < metalist.length; i++) {
        let name = metalist.item(i).getAttribute('name');
        if(name && name.toLowerCase() === 'viewport') {
            metalist.item(i).setAttribute('content', 'width=device-width,initial-scale=1.0');
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
setViewport();

ReactDOM.render(
    <Hello/>,
    document.getElementById("main-contents")
);

export const platform = {
    Hello,
};
