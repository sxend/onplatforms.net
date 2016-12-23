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
  }
  static contextTypes = {
    router: React.PropTypes.object.isRequired
  };
  onMenuClick(e: any) {
    this.setState((prev) => ({
      isMenuToggleOn: !prev.isMenuToggleOn
    }));
  }
  toggleOff() {
    this.setState({
      isMenuToggleOn: false
    } as HeaderState)
  }
  render() {
    return (
      <section className="on-header hero is-dark is-medium">
        <div className="hero-head">
          <header className="nav">
            <div className="container">
              <div className="nav-left">
                <span></span>
                <Link to="/" className="nav-item title" onClick={e => this.toggleOff()} ><p>ON</p></Link>
              </div>
              <span className={"nav-toggle" + (this.state.isMenuToggleOn ? " is-active" : "")}
                    onClick={e => this.onMenuClick(e)}>
              <span></span><span></span><span></span>
              </span>
              <div className={"nav-right nav-menu" + (this.state.isMenuToggleOn ? " is-active" : "")}>
                <span className="nav-item">
                  <Link to="/signin" className={"button is-dark is-inverted" + (
                    this.state.isMenuToggleOn ? " on-header--item__large" : "on-header--item"
                  )} onClick={e => this.toggleOff()}>Signin</Link>
                </span>
                <span className="nav-item">
                  <Link to="/signup" className={"button is-dark is-primary" + (
                    this.state.isMenuToggleOn ? " on-header--item__large" : "on-header--item"
                  )} onClick={e => this.toggleOff()}>Signup</Link>
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
