import * as React from "react";
import {Header} from "./Header";
import {Email} from "./Email";
import {Password} from "./Password";
import {SignForm, SignFormState} from "./SignForm";
import {api} from "../api";

interface SigninProps {
}
interface SigninState {
}
export class Signin extends React.Component<SigninProps, SigninState> {
  constructor(props: SigninProps) {
    super(props);
    this.state = {} as SigninState;
  }
  render() {
    return (
      <SignForm signupMode={false} onSubmit={s => this.onSubmit(s)}/>
    );
  }
  private onSubmit(s: SignFormState) {
    api.signin(s.email, s.password); // TODO: add social login
  }
  componentDidMount() {
    document.title = "Signin accounts.onplatforms.net";
  }
}