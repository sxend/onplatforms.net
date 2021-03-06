import * as React from "react";
import {Header} from "./Header";
import {Contents} from "./Contents";

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
        <Header/>
        <Contents />
      </div>
    );
  }

  componentDidMount() {
    document.title = "www.onplatforms.net";
  }
}