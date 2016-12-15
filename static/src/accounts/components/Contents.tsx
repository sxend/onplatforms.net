import * as React from "react";

interface ContentsProps {
}
interface ContentsState {
  isValidEmail: boolean
  email: string
  password: string
}
export class Contents extends React.Component<ContentsProps, ContentsState> {
  constructor(props: ContentsProps) {
    super(props);
    this.state = {} as ContentsState;
  }
  render() {
    return (
      <div className="container on-contents">
        <label className="label">Email</label>
        <p className="control has-icon has-icon-right">
          <input className="input" type="text" placeholder="Email input" value={this.state.email} onChange={e => this.changeEmail(e)} />
            <i className="fa fa-warning"></i>
          {this.state.isValidEmail ? <span className="help is-success">This email is valid</span>: ""}
        </p>
        <label className="label">Password</label>
        <p className="control has-icon">
          <input className="input" type="password" placeholder="Password" value={this.state.password} />
            <i className="fa fa-lock"></i>
        </p>
        <p className="control">
          <button className="button is-primary" onClick={e => this.login(e)}>Login</button>
        </p>
      </div>
    );
  }
  private mailRegexp = /^(([^<>()\[\]\.,;:\s@\"]+(\.[^<>()\[\]\.,;:\s@\"]+)*)|(\".+\"))@(([^<>()[\]\.,;:\s@\"]+\.)+[^<>()[\]\.,;:\s@\"]{2,})$/i;
  changeEmail(e: any) {
    this.setState({
      email: e.target.value,
      isValidEmail: this.mailRegexp.test(e.target.value)
    } as ContentsState);
  }
  login(e: any) {
  }
  componentDidMount() {
    document.title = "www.onplatforms.net";
  }
}