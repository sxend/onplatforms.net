import "./styles/index.scss";

import * as React from "react";
import * as ReactDOM from "react-dom";
import * as utils from './utils';

import {Hello} from "./components/Hello";

utils.setViewport('width=device-width,initial-scale=1.0');

ReactDOM.render(
    <Hello/>,
    document.getElementById("main-contents")
);

export const platform = {
    Hello,
};
