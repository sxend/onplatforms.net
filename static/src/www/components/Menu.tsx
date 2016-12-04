import * as React from "react";
import "../../../node_modules/purecss/build/pure-min.css";

export class Menu extends React.Component<{}, {}> {
  render() {
    return (
      <div>
        <div className="menu pure-menu pure-menu-horizontal">
          <p href="#" className="pure-menu-link menu-link">ON</p>
        </div>
      </div>
    );
  }
}