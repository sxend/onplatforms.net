import "../../node_modules/bulma/css/bulma.css";
import "font-awesome-webpack";
import "./styles/index.scss";

import * as React from "react";
import * as ReactDOM from "react-dom";
import { Router, Route, Link, browserHistory } from 'react-router';
import * as utils from '../lib/utils';

import {Main} from "./components/Main";
import {Signin} from "./components/Signin";
import {Signup} from "./components/Signup";

utils.setViewport('width=device-width,initial-scale=1.0');
const PREFIX= "";

ReactDOM.render((
  <Router history={browserHistory}>
    <Route path={PREFIX + "/"} component={Signup} />
    <Route path={PREFIX + "/signin"} component={Signin} />
    <Route path={PREFIX + "/signup"} component={Signup} />
  </Router>
), document.getElementById("main-contents"));

export const platform = {
  Main,
};
