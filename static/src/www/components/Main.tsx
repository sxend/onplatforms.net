import * as React from "react";
import {Menu} from "./Menu";

export class Main extends React.Component<{}, {}> {
    render() {
        return (
          <div>
              <Menu/>
          </div>
        );
    }

    componentDidMount() {
        document.title = "www.onplatforms.net";
    }
}