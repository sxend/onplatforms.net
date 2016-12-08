import * as React from "react";

export class Contents extends React.Component<{}, {}> {
  render() {
    return (
      <div>
      </div>
    );
  }

  componentDidMount() {
    document.title = "www.onplatforms.net";
  }
}