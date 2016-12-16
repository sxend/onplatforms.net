import * as React from "react";

interface SigninProps {
}
interface SigninState {
  email: string
  password: string
  isValidEmail: boolean
}
export class Signin extends React.Component<SigninProps, SigninState> {
  constructor(props: SigninProps) {
    super(props);
    this.state = {} as SigninState;
  }
  render() {
    return (
      <div className="container on-contents">
        <div className="column is-half is-offset-one-quarter">
          <label className="label">Email</label>
          <p className="control has-icon has-icon-right">
            <input className="input" type="text" placeholder="Email input" value={this.state.email} onChange={e => this.changeEmail(e)} />
            <i className="fa fa-warning"></i>
            {this.state.isValidEmail ? <span className="help is-success">This Email is valid</span>: ""}
          </p>
          <label className="label">Password</label>
          <p className="control has-icon">
            <input className="input" type="password" placeholder="Password" value={this.state.password} />
            <i className="fa fa-lock"></i>
          </p>
          <p className="control">
            <button className="button is-primary" onClick={e => this.signin(e)}>Signin</button>
          </p>
        </div>
      </div>
    );
  }
  private mailRegexp = /^(([^<>()\[\]\.,;:\s@\"]+(\.[^<>()\[\]\.,;:\s@\"]+)*)|(\".+\"))@(([^<>()[\]\.,;:\s@\"]+\.)+[^<>()[\]\.,;:\s@\"]{2,})$/i;
  changeEmail(e: any) {
    this.setState({
      email: e.target.value,
      isValidEmail: this.mailRegexp.test(e.target.value)
    } as SigninState);
  }
  signin(e: any) {
  }
  componentDidMount() {
    document.title = "www.onplatforms.net";
  }
}