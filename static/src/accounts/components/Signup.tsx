import * as React from "react";
import {Header} from "./Header";

declare const fetch: Function;

interface SignupProps {
}
interface SignupState {
  userName: string
  email: string
  password: string
  isValidUserName: boolean
  isValidEmail: boolean
  isValidPassword: boolean
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
          <div className="column is-half is-offset-one-quarter">
            <label className="label">UserName</label>
            <p className="control has-icon has-icon-right">
              <input className="input" type="text" placeholder="UserName input" value={this.state.userName} onChange={e => this.changeUserName(e)} />
              <i className="fa fa-warning"></i>
              {this.state.isValidUserName ? <span className="help is-success">This UserName is valid</span>: ""}
            </p>
            <label className="label">Email</label>
            <p className="control has-icon has-icon-right">
              <input className="input" type="text" placeholder="Email input" value={this.state.email} onChange={e => this.changeEmail(e)} />
              <i className="fa fa-warning"></i>
              {this.state.isValidEmail ? <span className="help is-success">This Email is valid</span>: ""}
            </p>
            <label className="label">Password</label>
            <p className="control has-icon">
              <input className="input" type="password" placeholder="Password" value={this.state.password} onChange={e => this.changePassword(e)} />
              <i className="fa fa-lock"></i>
            </p>
            <p className="control">
              <button className={
                "button is-primary " + (this.state.isValidUserName && this.state.isValidEmail && this.state.isValidPassword ? "" : "is-disabled")
              } onClick={e => this.signup(e)}>Signup</button>
            </p>
          </div>
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
  private passwordRegexp = /^[\S]{8,128}$/i;
  changePassword(e: any) {
    this.setState({
      password: e.target.value,
      isValidPassword: this.passwordRegexp.test(e.target.value)
    } as SignupState);
  }
  signup(e: any) {
    fetch('http://localhost:9091/signup', {
      method: 'POST',
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        ownedSignupOpt: {
          userName: this.state.userName,
          email: this.state.email,
          password: this.state.password,
        }
      })
    })
  }
  componentDidMount() {
    document.title = "www.onplatforms.net";
  }
}