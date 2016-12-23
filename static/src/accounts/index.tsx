import "bulma/css/bulma.css";
// import "font-awesome-webpack";
import "font-awesome/css/font-awesome.css"
import "./styles/index.scss";

import * as React from "react";
import * as ReactDOM from "react-dom";
import { Router, Route, Link, browserHistory } from 'react-router';
import * as utils from '../lib/utils';

import {Main} from "./components/Main";
import {Signin} from "./components/Signin";
import {Signup} from "./components/Signup";
import {Home} from "./components/Home";

utils.setViewport('width=device-width,initial-scale=1.0');
const PREFIX= "";

const router = (
  <Router history={browserHistory}>
    <Route path={PREFIX + "/"} component={Home} />
    <Route path={PREFIX + "/signin"} component={Signin} />
    <Route path={PREFIX + "/signup"} component={Signup} />
    <Route path={PREFIX + "/home"} component={Home} />
  </Router>
);

ReactDOM.render(router, document.getElementById("main-contents"));

export const platform = {
  Main,
};
