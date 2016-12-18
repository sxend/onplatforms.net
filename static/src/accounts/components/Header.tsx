import * as React from "react";
import { Router, Route, Link, browserHistory } from 'react-router';

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
                  <Link to="/test/accounts/signup" className={"button is-dark is-inverted" + (
                    this.state.isMenuToggleOn ? " on-header--item__large" : "on-header--item"
                  )}>Signup</Link>
                </span>
                <span></span>
              </div>
            </div>
          </header>
        </div>
      </section>
    );
  }
  componentDidMount() {
    console.log("mount header");
  }
}
