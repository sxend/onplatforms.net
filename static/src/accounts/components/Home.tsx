import * as React from "react";
import { render } from 'react-dom'
import { Router, Route, Link, browserHistory } from 'react-router';
import {Header} from "./Header";
import {api} from "../api";

interface HomeProps {
}
interface HomeState {
  userName: string;
}

export class Home extends React.Component<HomeProps, HomeState> {
  constructor(props: HomeProps) {
    super(props);
    this.state = {} as HomeState;
  }
  static contextTypes = {
    router: React.PropTypes.object.isRequired
  };
  render() {
    return (
      <div>
        <Header />
        <div className="container  on-contents">
          <p> {this.state.userName} でログイン中</p>
        </div>
      </div>
    );
  }

  componentDidMount() {
    api.home().then(response => {
      if (response.status === 200) {
        this.setState({
          userName: response.body.userName
        });
      } else {
        this.context.router.push("/signin");
      }
    });
    document.title = "Home - accounts.onplatforms.net";
  }
}