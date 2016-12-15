import * as React from "react";
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
        <Header/>
        <Signup />
      </div>
    );
  }

  componentDidMount() {
    document.title = "Signin accounts.onplatforms.net";
  }
}