import * as React from "react";
import 'whatwg-fetch';
import 'promise';
import {Header} from "./Header";
import {UserName} from "./UserName";
import {Email} from "./Email";
import {Password} from "./Password";
import ReactInstance = React.ReactInstance;

interface SignupProps {
  signupMode: boolean
}
interface SignupState {
  userNameRef: UserName
  emailRef: Email
  passwordRef: Password
}
export class Signup extends React.Component<SignupProps, SignupState> {
  constructor(props: SignupProps) {
    super(props);
    this.state = {} as SignupState;
  }
  render() {
    return (
      <div>
        <Header />
        <div className="container on-contents">
          <div className="column is-half is-offset-one-quarter">
            {this.props.signupMode ? <UserName ref="userName" /> : ""}
            <Email ref="email" />
            <Password ref="password" />
            <p className="control">
              <button className={
                "button is-primary"
              } onClick={e => this.signup(e)}>Signup</button>
            </p>
          </div>
        </div>
      </div>
    );
  }
  private isValidUserName() {
    return this.state.userNameRef && this.state.userNameRef.state.isValidUserName;
  }
  private isValidEmail() {
    return this.state.emailRef && this.state.emailRef.state.isValidEmail;
  }
  private isValidPassword() {
    return this.state.passwordRef && this.state.passwordRef.state.isValidPassword;
  }
  signup(e: any) {
    console.log(this);
    const param = JSON.stringify({
      userName: this.state.userNameRef.state.userName,
      email: this.state.emailRef.state.email,
      password: this.state.passwordRef.state.password,
    });
    console.log(param);
    fetch('//accounts.onplatforms.net/signup/owned', {
      method: 'POST',
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json',
      },
      body: param
    }).then((response: any) => {
      return response.json();
    }).then((user: any) => {
      if(!!user.id) {
      }
    });
  }
  componentDidMount() {
    console.log("mount signup");
    document.title = "Signup accounts.onplatforms.net";
    this.setState({
      userNameRef: this.refs['userName'] as UserName,
      emailRef: this.refs['email'] as Email,
      passwordRef: this.refs['password'] as Password
    } as SignupState)
  }
}