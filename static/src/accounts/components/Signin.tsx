import * as React from "react";
import {Header} from "./Header";
import {Email} from "./Email";
import {Password} from "./Password";
import { Router } from 'react-router';
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
  static contextTypes = {
    router: React.PropTypes.object.isRequired
  };
  render() {
    return (
      <SignForm signupMode={false} onSubmit={s => this.onSubmit(s)}/>
    );
  }
  private onSubmit(s: SignFormState) {
    api.signin(s.email, s.password).then(response => {
      if (response.status == 200) {
        this.context.router.push(response.body.location);
      }
    }); // TODO: add social login
  }
  componentDidMount() {
    document.title = "Signin - accounts.onplatforms.net";
  }
}