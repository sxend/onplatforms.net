import * as React from "react";
import 'whatwg-fetch';
import 'promise';
import {Header} from "./Header";

interface UserNameProps {}
interface UserNameState {
  userName: string
  isValidUserName: boolean
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
        <p className="control has-icon has-icon-right">
          <input className="input" type="text" placeholder="UserName input" value={this.state.userName} onChange={e => this.changeUserName(e)} />
          <i className="fa fa-warning"></i>
          {this.state.isValidUserName ? <span className="help is-success">This UserName is valid</span>: ""}
        </p>
      </div>
    )
  }
  private userNameRegexp = /^[\S]{0,32}$/i;
  changeUserName(e: any) {
    let userName: UserName = (this.refs['userName'] as UserName);
    this.setState({
      userName: e.target.value,
      isValidUserName: this.userNameRegexp.test(e.target.value)
    } as UserNameState);
  }
}