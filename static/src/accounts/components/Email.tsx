import * as React from "react";
import 'whatwg-fetch';
import 'promise';
import {Header} from "./Header";

interface EmailProps {}
interface EmailState {
  email: string
  isValidEmail: boolean
}

export class Email extends React.Component<EmailProps, EmailState> {
  constructor(props: EmailProps) {
    super(props);
    this.state = {} as EmailState;
  }
  render() {
    return (
      <div className="column is-half is-offset-one-quarter">
        <label className="label">Email</label>
        <p className="control has-icon has-icon-right">
          <input className="input" type="text" placeholder="Email input" value={this.state.email} onChange={e => this.changeEmail(e)} />
          <i className="fa fa-warning"></i>
          {this.state.isValidEmail ? <span className="help is-success">This Email is valid</span>: ""}
        </p>
      </div>
    )
  }
  private mailRegexp = /^(([^<>()\[\]\.,;:\s@\"]+(\.[^<>()\[\]\.,;:\s@\"]+)*)|(\".+\"))@(([^<>()[\]\.,;:\s@\"]+\.)+[^<>()[\]\.,;:\s@\"]{2,})$/i;
  changeEmail(e: any) {
    this.setState({
      email: e.target.value,
      isValidEmail: this.mailRegexp.test(e.target.value)
    } as EmailState);
  }
}