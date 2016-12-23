import * as React from "react";
import {Header} from "./Header";
import {Email} from "./Email";
import {Password} from "./Password";
import {SignForm, SignFormState} from "./SignForm";
import {api} from "../api";

interface SignupProps {
}
interface SignupState {
}
export class Signup extends React.Component<SignupProps, SignupState> {
  constructor(props: SignupProps) {
    super(props);
    this.state = {} as SignupState;
  }
  static contextTypes = {
    router: React.PropTypes.object.isRequired
  };
  render() {
    return (
      <SignForm signupMode={true} onSubmit={s => this.onSubmit(s)}/>
    );
  }
  private onSubmit(s: SignFormState) {
    api.signup(s.userName, s.email, s.password).then(response => {
      if (response.status == 200) {
        this.context.router.push(response.body.location);
      }
    });
  }
  componentDidMount() {
    document.title = "Signup - accounts.onplatforms.net";
  }
}