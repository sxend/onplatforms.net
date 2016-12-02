import "./styles/index.scss";

import * as React from "react";
import * as ReactDOM from "react-dom";

import {Hello} from "./components/Hello";

ReactDOM.render(
    <Hello/>,
    document.getElementById("main-contents")
);

export const platform = {
    Hello,
};
