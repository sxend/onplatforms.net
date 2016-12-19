import * as React from "react";
import 'whatwg-fetch';
import 'promise';
import {Header} from "./Header";

interface UserNameProps {
  onChange: (userName: string, isValid: boolean) => void
}
interface UserNameState {
  userName: string
  isValid: boolean
}

export class UserName extends React.Component<UserNameProps, UserNameState> {
  constructor(props: UserNameProps) {
    super(props);
    this.state = {} as UserNameState;
  }
  render() {
    return (
      <div className="column is-half is-offset-one-quarter">
        <label className="label">UserName</label>
        <p className="control has-icon">
          <input className="input" type="text" placeholder="UserName input" value={this.state.userName} onChange={e => this.onChange(e)} />
          <i className={"fa " + (this.state.isValid ? "fa-check" : "fa-address-card-o")}></i>
          {this.state.isValid ? <span className="help is-success">This UserName is valid</span>: ""}
        </p>
      </div>
    )
  }
  private userNameRegexp = /^[\S]{4,32}$/i;
  onChange(e: any) {
    const userName = e.target.value;
    const isValid = this.userNameRegexp.test(userName);
    this.props.onChange(userName, isValid);
    this.setState({
      userName: userName,
      isValid: isValid
    } as UserNameState);
  }
}