import "../../node_modules/bulma/css/bulma.css";
import "./styles/index.scss";

import * as React from "react";
import * as ReactDOM from "react-dom";
import * as utils from '../lib/utils';

import {Main} from "./components/Main";

utils.setViewport('width=device-width,initial-scale=1.0');

ReactDOM.render(
  <Main/>,
  document.getElementById("main-contents")
);

export const platform = {
  Main,
};
