import 'whatwg-fetch';
import 'promise';

const API_URL = "/* @echo API_URL */";
let token: string = void 0;

export const api = {
  signin: (email: string, password: string, provider?: string) => {
    provider = provider ? '/' + provider : '';
    postJSON(v1Endpoint('/signin' + provider), {
      email: email,
      password: password
    }).then((result: any) => {
    });
  },
  signup: (userName: string, email: string, password: string) => {
    postJSON(v1Endpoint('/signup'), {
      userName: userName,
      email: email,
      password: password
    }).then((result: any) => {
    });
  },
  signout: () => {
    postJSON(v1Endpoint('/signout'), {
    }).then((result: any) => {
    });
  }

};

function v1Endpoint(path: string) {
  return "/api/v1" + path;
}

function fetchToken() {
  return fetch(API_URL + v1Endpoint('/token'), {
    method: 'POST',
    headers: {
      'Accept': 'application/json',
      'Content-Type': 'application/json'
    },
    credentials: 'include'
  }).then(updateToken, updateToken);
}

function postJSON(path: string, param: any) {
  function execute() {
    return fetch(API_URL + path, {
      method: 'POST',
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json',
        'X-CSRF-Token': token
      },
      body: JSON.stringify(param),
      credentials: 'include'
    }).then(updateToken, updateToken)
      .then((response: Response) => {
        return response.json();
      });
  }
  if (!token) {
    return fetchToken().then(execute);
  } else {
    return execute();
  }
}

function updateToken(response: any) {
  if (!response.headers || !response.headers.get('X-CSRF-Token')) {
    token = void 0;
    return response;
  }
  let tokens = (response.headers.get('X-CSRF-Token') || "").split(",").map((t: string) => t.trim());
  token = tokens[tokens.length - 1];
  return response;
}