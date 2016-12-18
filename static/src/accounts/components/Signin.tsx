import * as React from "react";
import {Header} from "./Header";
import {Email} from "./Email";
import {Password} from "./Password";
import {Signup} from "./Signup";

interface SigninProps {
}
interface SigninState {
  emailRef: Email
  passwordRef: Password
}
export class Signin extends React.Component<SigninProps, SigninState> {
  constructor(props: SigninProps) {
    super(props);
    this.state = {} as SigninState;
  }
  render() {
    return (
      <Signup signupMode={false} />
    );
  }
  private isValidEmail() {
    return this.state.emailRef && this.state.emailRef.state.isValidEmail;
  }
  private isValidPassword() {
    return this.state.passwordRef && this.state.passwordRef.state.isValidPassword;
  }
  signin(e: any) {
  }
  componentDidMount() {
    console.log("mount signin");
    document.title = "Signin accounts.onplatforms.net";
    this.setState({
      emailRef: this.refs['email'] as Email,
      passwordRef: this.refs['password'] as Password
    } as SigninState)
  }
}