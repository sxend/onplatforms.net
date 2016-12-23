import * as React from "react";
import { Router, Route, Link, browserHistory } from 'react-router';
import {api} from "../api";

export interface HeaderState {
  isMenuToggleOn: boolean;
}
export interface HeaderProps {
}
export class Header extends React.Component<HeaderProps, HeaderState> {
  constructor(props: HeaderProps) {
    super(props);
    this.state = {isMenuToggleOn: false};
    this.onMenuClick = this.onMenuClick.bind(this);
  }
  onMenuClick() {
    this.setState((prev) => ({
      isMenuToggleOn: !prev.isMenuToggleOn
    }));
  }
  render() {
    return (
      <section className="on-header hero is-dark is-medium">
        <div className="hero-head">
          <header className="nav">
            <div className="container">
              <div className="nav-left">
                <span></span>
                <p className="nav-item title">ON</p>
              </div>
              <span className={"nav-toggle" + (this.state.isMenuToggleOn ? " is-active" : "")}
                    onClick={this.onMenuClick}>
              <span></span><span></span><span></span>
              </span>
              <div className={"nav-right nav-menu" + (this.state.isMenuToggleOn ? " is-active" : "")}>
                <span className="nav-item">
                  <Link to="/signin" className={"button is-dark is-inverted" + (
                    this.state.isMenuToggleOn ? " on-header--item__large" : "on-header--item"
                  )}>Signin</Link>
                </span>
                <span className="nav-item">
                  <Link to="/signup" className={"button is-dark is-primary" + (
                    this.state.isMenuToggleOn ? " on-header--item__large" : "on-header--item"
                  )}>Signup</Link>
                </span>
                <span className="nav-item">
                  <a onClick={e => this.signout(e)} className={"button is-dark is-danger" + (
                    this.state.isMenuToggleOn ? " on-header--item__large" : "on-header--item"
                  )}>Signout</a>
                </span>
                <span></span>
              </div>
            </div>
          </header>
        </div>
      </section>
    );
  }
  private signout(e: any) {
    api.signout().then(response => {
      if (response.status === 200) {
        this.context.router.push(response.body.location);
      }
    });
  }
  componentDidMount() {
  }
}
