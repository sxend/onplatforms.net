import * as React from "react";
import 'whatwg-fetch';
import 'promise';
import {Header} from "./Header";
import {UserName} from "./UserName";
import {Email} from "./Email";
import {Password} from "./Password";
import ReactInstance = React.ReactInstance;
import {api} from "../api";

interface SignFormProps {
  signupMode: boolean
  onSubmit: (s: SignFormState) => void
}
export interface SignFormState {
  userName: string
  email: string
  password: string
  isValidUserName: boolean
  isValidEmail: boolean
  isValidPassword: boolean
}
export class SignForm extends React.Component<SignFormProps, SignFormState> {
  constructor(props: SignFormProps) {
    super(props);
    this.state = {} as SignFormState;
  }
  render() {
    return (
      <div>
        <Header />
        <div className="container on-contents">
          <div className="column is-half is-offset-one-quarter">
            {this.props.signupMode ? (
                <UserName ref="userName"
                          onChange={(value, valid) => this.onChangeUserName(value, valid)}/>
              ) : ""}
            <Email ref="email"
                   onChange={(value, valid) => this.onChangeEmail(value, valid)}/>
            <Password ref="password"
                      onChange={(value, valid) => this.onChangePassword(value, valid)}/>
            <p className="control">
              <button className={
                "button is-primary " + (this.submitEnabled())
              } onClick={e => this.submit(e)} value="owned">{this.props.signupMode ? "Signup" : "Signin"}</button>
            </p>
          </div>
        </div>
      </div>
    );
  }
  private onChangeUserName(userName: string, valid: boolean) {
    this.setState({
     userName,
     isValidUserName: valid
    } as SignFormState)
  }
  private onChangeEmail(email: string, valid: boolean) {
    this.setState({
      email,
      isValidEmail: valid
    } as SignFormState)
  }
  private onChangePassword(password: string, valid: boolean) {
    this.setState({
      password,
      isValidPassword: valid
    } as SignFormState)
  }
  private submitEnabled() {
    let submitEnabledClass = "is-disabled";
    if(this.props.signupMode) {
      if (this.state.isValidUserName && this.state.isValidEmail && this.state.isValidPassword) {
        submitEnabledClass = "";
      }
    } else {
      if (this.state.isValidEmail && this.state.isValidPassword) {
        submitEnabledClass = "";
      }
    }
    return submitEnabledClass;
  }
  private submit(e: any) {
    this.props.onSubmit(this.state);
  }
  componentDidMount() {
  }
}