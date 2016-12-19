import * as React from "react";
import 'whatwg-fetch';
import 'promise';
import {Header} from "./Header";

interface EmailProps {
  onChange: (email: string, isValid: boolean) => void
}
interface EmailState {
  email: string
  isValid: boolean
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
        <p className="control has-icon">
          <input className="input" type="text" placeholder="Email input" value={this.state.email} onChange={e => this.onChange(e)} />
          <i className={"fa " + (this.state.isValid ? "fa-check" : "fa-envelope")}></i>
          {this.state.isValid ? <span className="help is-success">This Email is valid</span>: ""}
        </p>
      </div>
    )
  }
  private emailRegexp = /^(([^<>()\[\]\.,;:\s@\"]+(\.[^<>()\[\]\.,;:\s@\"]+)*)|(\".+\"))@(([^<>()[\]\.,;:\s@\"]+\.)+[^<>()[\]\.,;:\s@\"]{2,})$/i;
  onChange(e: any) {
    const email = e.target.value;
    const isValid = this.emailRegexp.test(email);
    this.props.onChange(email, isValid);
    this.setState({
      email: email,
      isValid: isValid
    } as EmailState);
  }
}