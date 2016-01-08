CREATE TYPE color AS ENUM ('W', 'U', 'B', 'R', 'G');

CREATE TABLE "set" (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v1mc(),
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
  id UUID PRIMARY KEY DEFAULT uuid_generate_v1mc(),
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
  splitcardparent UUID REFERENCES card,
  doublefacefront UUID REFERENCES card
);

CREATE INDEX ON card (fulltype);

CREATE TABLE printing (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v1mc(),
  setid UUID NOT NULL REFERENCES "set",
  cardid UUID NOT NULL REFERENCES card,
  multiverseid INT,
  number VARCHAR(5),
  rarity VARCHAR(32),
  originaltext CITEXT,
  originaltype VARCHAR(128),
  flavortext CITEXT,
  UNIQUE (setid, cardid, number)
);
