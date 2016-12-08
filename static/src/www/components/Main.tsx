import * as React from "react";
import {Header} from "./Header";
import {Contents} from "./Contents";

export class Main extends React.Component<{}, {}> {
  render() {
    return (
      <div>
        <Header/>
        <Contents />
      </div>
    );
  }

  componentDidMount() {
    document.title = "www.onplatforms.net";
  }
}