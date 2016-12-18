import * as React from "react";
import { render } from 'react-dom'
import { Router, Route, Link, browserHistory } from 'react-router';
import {Header} from "./Header";
import {Signin} from "./Signin";
import {Signup} from "./Signup";

interface MainProps {
}
interface MainState {
  fixedHeader: boolean;
}

export class Main extends React.Component<MainProps, MainState> {
  constructor(props: MainProps) {
    super(props);
  }
  render() {
    return (
      <div>
        <Header />
      </div>
    );
  }

  componentDidMount() {
    console.log("mount main");
    document.title = "accounts.onplatforms.net";
  }
}