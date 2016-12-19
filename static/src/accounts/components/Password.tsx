import * as React from "react";
import 'whatwg-fetch';
import 'promise';
import {Header} from "./Header";

interface PasswordProps {
  onChange: (password: string, valid: boolean) => void
}
interface PasswordState {
  password: string
  isValid: boolean
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
          <input className="input" type="password" placeholder="Password" value={this.state.password} onChange={e => this.onChange(e)} />
          <i className="fa fa-lock"></i>
        </p>
      </div>
    )
  }
  private passwordRegexp = /^[\S]{8,1024}$/i;
  onChange(e: any) {
    const password = e.target.value;
    const isValid = this.passwordRegexp.test(password);
    this.props.onChange(password, isValid);
    this.setState({
      password: password,
      isValid: isValid
    } as PasswordState);
  }
}