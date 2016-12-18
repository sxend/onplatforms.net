import * as React from "react";
import 'whatwg-fetch';
import 'promise';
import {Header} from "./Header";

interface PasswordProps {}
interface PasswordState {
  password: string
  isValidPassword: boolean
}

export class Password extends React.Component<PasswordProps, PasswordState> {
  constructor(props: PasswordProps) {
    super(props);
    this.state = {} as PasswordState
  }
  render() {
    return (
      <div className="column is-half is-offset-one-quarter">
        <label className="label">Password</label>
        <p className="control has-icon">
          <input className="input" type="password" placeholder="Password" value={this.state.password} onChange={e => this.changePassword(e)} />
          <i className="fa fa-lock"></i>
        </p>
      </div>
    )
  }
  private passwordRegexp = /^[\S]{8,1024}$/i;
  changePassword(e: any) {
    this.setState({
      password: e.target.value,
      isValidPassword: this.passwordRegexp.test(e.target.value)
    } as PasswordState);
  }
}