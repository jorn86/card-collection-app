CREATE TYPE color AS ENUM ('W', 'U', 'B', 'R', 'G');

CREATE TABLE "set" (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v1mc(),
  gatherercode CHAR(4) NOT NULL UNIQUE CHECK (gatherercode <> ''),
  code CHAR(4) NOT NULL UNIQUE CHECK (code <> ''),
  name VARCHAR(128) NOT NULL UNIQUE CHECK (name <> '')
);

CREATE TABLE card (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v1mc(),
  name VARCHAR(150) NOT NULL CHECK (name <> ''),
  fulltype VARCHAR(128),
  supertypes VARCHAR(128)[],
  types VARCHAR(128)[],
  subtypes VARCHAR(128)[],
  cost VARCHAR(128) NOT NULL,
  cmc NUMERIC NOT NULL CHECK (cmc >= 0),
--   colors color[] NOT NULL CHECK (colors = uniq(colors)),
  text VARCHAR,
  power NUMERIC,
  toughness NUMERIC,
  loyalty NUMERIC,
  layout VARCHAR(20) NOT NULL,
  splitcardparent UUID REFERENCES card
);

CREATE TABLE printing (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v1mc(),
  setid UUID NOT NULL REFERENCES "set",
  cardid UUID NOT NULL REFERENCES card,
  multiverseid NUMERIC,
  number CHAR(5),
  rarity VARCHAR(32),
  originaltext VARCHAR,
  originaltype VARCHAR,
  flavortext VARCHAR
);
