CREATE TABLE authenticationmethod (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v1mc(),
  externalid TEXT NOT NULL,
  type TEXT NOT NULL,
  userid UUID NOT NULL REFERENCES "user" ON DELETE CASCADE
);
