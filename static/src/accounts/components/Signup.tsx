import * as React from "react";
import {Header} from "./Header";

interface SignupProps {
}
interface SignupState {
  userName: string
  email: string
  password: string
  isValidUserName: boolean
  isValidEmail: boolean
}
export class Signup extends React.Component<SignupProps, SignupState> {
  constructor(props: SignupProps) {
    super(props);
    this.state = {} as SignupState;
  }
  render() {
    return (
      <div>
        <Header/>
        <div className="container on-contents">
          <label className="label">UserName</label>
          <p className="control has-icon has-icon-right">
            <input className="input" type="text" placeholder="UserName input" value={this.state.userName} onChange={e => this.changeUserName(e)} />
            <i className="fa fa-warning"></i>
            {this.state.isValidEmail ? <span className="help is-success">This UserName is valid</span>: ""}
          </p>
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
            <button className="button is-primary" onClick={e => this.signup(e)}>Signup</button>
          </p>
        </div>
      </div>
    );
  }
  private userNameRegexp = /^[\S]{0,32}$/i;
  changeUserName(e: any) {
    this.setState({
      userName: e.target.value,
      isValidUserName: this.userNameRegexp.test(e.target.value)
    } as SignupState);
  }
  private mailRegexp = /^(([^<>()\[\]\.,;:\s@\"]+(\.[^<>()\[\]\.,;:\s@\"]+)*)|(\".+\"))@(([^<>()[\]\.,;:\s@\"]+\.)+[^<>()[\]\.,;:\s@\"]{2,})$/i;
  changeEmail(e: any) {
    this.setState({
      email: e.target.value,
      isValidEmail: this.mailRegexp.test(e.target.value)
    } as SignupState);
  }
  signup(e: any) {
  }
  componentDidMount() {
    document.title = "www.onplatforms.net";
  }
}