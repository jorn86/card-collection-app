CREATE TABLE "user" (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v1mc(),
  name TEXT NOT NULL,
  email CITEXT NOT NULL
);
