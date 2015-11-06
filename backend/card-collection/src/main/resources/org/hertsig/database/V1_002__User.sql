CREATE TABLE "user" (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v1mc(),
  name VARCHAR(512) NOT NULL,
  email VARCHAR(512) NOT NULL
);

CREATE TABLE authenticationoption (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v1mc(),
  externalid VARCHAR(512) NOT NULL,
  type VARCHAR(16) NOT NULL,
  userid UUID NOT NULL REFERENCES "user" ON DELETE CASCADE,
  UNIQUE (userid, type)
);
