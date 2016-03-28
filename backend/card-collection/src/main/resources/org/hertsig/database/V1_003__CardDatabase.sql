CREATE TYPE color AS ENUM ('W', 'U', 'B', 'R', 'G');
CREATE TYPE "format" AS ENUM ('Commander', 'Legacy', 'Modern', 'Standard', 'Vintage');
CREATE TYPE legalityoption AS ENUM ('Legal', 'Restricted', 'Banned');

CREATE TABLE "set" (
  id SERIAL PRIMARY KEY,
  gatherercode VARCHAR(8) NOT NULL UNIQUE CHECK (gatherercode <> ''),
  code VARCHAR(8) NOT NULL UNIQUE CHECK (code <> ''),
  mcicode VARCHAR(8),
  name VARCHAR(128) NOT NULL UNIQUE CHECK (name <> ''),
  releasedate DATE,
  type VARCHAR(32) NOT NULL,
  priority INT NOT NULL,
  onlineonly BOOLEAN NOT NULL
);

CREATE TABLE card (
  id SERIAL PRIMARY KEY,
  name VARCHAR(150) NOT NULL UNIQUE CHECK (name <> ''),
  normalizedname CITEXT NOT NULL UNIQUE CHECK (normalizedname <> ''),
  fulltype VARCHAR(128),
  supertypes VARCHAR(32)[],
  types VARCHAR(32)[],
  subtypes VARCHAR(32)[],
  cost VARCHAR(128),
  cmc NUMERIC NOT NULL CHECK (cmc >= 0),
  colors color[],
  text CITEXT,
  power VARCHAR(8),
  toughness VARCHAR(8),
  loyalty NUMERIC,
  layout VARCHAR(20) NOT NULL,
  splitcardparent INT REFERENCES card,
  doublefacefront INT REFERENCES card
);

CREATE INDEX ON card (fulltype);

CREATE TABLE legality (
  id SERIAL PRIMARY KEY,
  cardid INT NOT NULL REFERENCES "card",
  "format" "format" NOT NULL,
  legality legalityoption NOT NULL
);

CREATE UNIQUE INDEX ON legality (cardid, "format");

CREATE TABLE printing (
  id SERIAL PRIMARY KEY,
  setid INT NOT NULL REFERENCES "set",
  cardid INT NOT NULL REFERENCES card,
  multiverseid INT,
  number VARCHAR(5),
  rarity VARCHAR(32),
  originaltext CITEXT,
  originaltype VARCHAR(128),
  flavortext CITEXT,
  artist VARCHAR(128),
  UNIQUE (setid, cardid, number)
);
