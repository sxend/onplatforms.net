import 'whatwg-fetch';
import 'promise';

const API_URL = "/* @echo API_URL */";
let token: string = void 0;

export const api = {
  signin: (email: string, password: string, provider?: string) => {
    provider = provider ? '/' + provider : '';
    return requestJSON(v1Endpoint('/signin' + provider), {
      email: email,
      password: password
    });
  },
  signup: (userName: string, email: string, password: string) => {
    return requestJSON(v1Endpoint('/signup'), {
      userName: userName,
      email: email,
      password: password
    });
  },
  signout: () => {
    return requestJSON(v1Endpoint('/signout'), {
    }, {
      redirect: 'manual'
    });
  },
  home: () => {
    return requestJSON(v1Endpoint('/home'), null, {
      method: 'GET'
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

function requestJSON(path: string, param: any, option: any = {}) {
  function execute() {
    let _option: any = Object.assign({
      method: 'POST',
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json',
        'X-CSRF-Token': token
      },
      credentials: 'include'
    }, param? {body: JSON.stringify(param)} : {}, option);
    return fetch(API_URL + path, _option).then(updateToken, updateToken)
      .then((response: Response) => {
        return response.json().then(body => {
          return {
            status: response.status,
            headers: response.headers,
            body: body
          }
        });
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