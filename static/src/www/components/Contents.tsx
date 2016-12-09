import * as React from "react";

interface ContentsProps {
}
interface ContentsState {
}
export class Contents extends React.Component<ContentsProps, ContentsState> {
  constructor(props: ContentsProps) {
    super(props);
    this.state = {} as ContentsState;
  }
  render() {
    return (
      <div className="container on-contents">
        <p>content</p>
      </div>
    );
  }

  componentDidMount() {
    document.title = "www.onplatforms.net";
  }
}