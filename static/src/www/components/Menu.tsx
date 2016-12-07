import * as React from "react";

interface MenuState {
  isMenuToggleOn: boolean;
}
interface MenuProps {
}
export class Menu extends React.Component<MenuProps, MenuState> {
  constructor(props: MenuProps) {
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
      <section className="hero is-dark is-medium">
        <div className="hero-head">
          <header className="nav">
            <div className="container">
              <div className="nav-left">
                <span></span>
                <p className="nav-item title">ON</p>
              </div>
              <span className={"nav-toggle" + (this.state.isMenuToggleOn ? " is-active" : "")} onClick={this.onMenuClick}>
              <span></span><span></span><span></span>
              </span>
              <div className={"nav-right nav-menu" + (this.state.isMenuToggleOn ? " is-active" : "")}>
                <span className="nav-item">
                  <a className="is-inverted is-active">Home</a>
                </span>
                <span className="nav-item">
                  <a className="is-inverted">Signin</a>
                </span>
                <span className="nav-item">
                  <a className="button is-inverted">foo</a>
                </span>
                <span></span>
              </div>
            </div>
          </header>
        </div>
      </section>
    );
  }
}

class MenuItems extends React.Component<{}, {}> {
  render() {
    return (
      <span>item</span>
    )
  }
}