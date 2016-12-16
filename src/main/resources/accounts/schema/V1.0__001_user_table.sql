CREATE TABLE users(
  id VARCHAR(64),
  PRIMARY KEY (id)
);

CREATE TABLE authentication_provider(
  id VARCHAR (32),
  PRIMARY KEY (id)
);

CREATE TABLE owned_users(
  email VARCHAR (64),
  password_hash VARCHAR(128) NOT NULL,
  user_name VARCHAR (32) NOT NULL,
  user_id VARCHAR (64) NOT NULL UNIQUE,
  PRIMARY KEY (email)
);

CREATE TABLE multi_provider_authenticated(
  user_id VARCHAR (64),
  provider_id VARCHAR (32),
  PRIMARY KEY (user_id, provider_id)
);


CREATE TABLE twitter_users(
  screen_name VARCHAR(32),
  user_id VARCHAR (64) NOT NULL UNIQUE ,
  oauth_token VARCHAR (64) NOT NULL ,
  oauth_token_secret VARCHAR (64) NOT NULL ,
  PRIMARY KEY (screen_name)
);


