import * as React from "react";

export class Hello extends React.Component<{}, {}> {
    render() {
        return <p>www.onplatforms.net</p>;
    }

    componentDidMount() {
        document.title = "www.onplatforms.net";
    }
}