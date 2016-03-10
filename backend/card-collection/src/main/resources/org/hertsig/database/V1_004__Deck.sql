CREATE TABLE "tag" (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  name VARCHAR(128) NOT NULL,
  parentid UUID REFERENCES "tag" ON DELETE CASCADE,
  userid UUID REFERENCES "user"
);

CREATE TABLE deck (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  name VARCHAR(128) NOT NULL,
  userid UUID REFERENCES "user",
  inventory BOOLEAN DEFAULT FALSE,
  "format" "format"
);

CREATE TABLE decktag (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  deckid UUID NOT NULL REFERENCES deck ON DELETE CASCADE,
  tagid UUID NOT NULL REFERENCES "tag" ON DELETE CASCADE,
  UNIQUE (deckid, tagid)
);

CREATE TABLE board (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  deckid UUID NOT NULL REFERENCES deck ON DELETE CASCADE,
  name VARCHAR(128) NOT NULL CHECK (name <> ''),
  "order" INT NOT NULL,
  UNIQUE (deckid, name),
  UNIQUE (deckid, "order")
);

CREATE TABLE deckrow (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  boardid UUID NOT NULL REFERENCES board ON DELETE CASCADE,
  cardid INT NOT NULL REFERENCES card,
  printingid INT REFERENCES printing,
  amount INT NOT NULL CHECK (amount > 0),
  UNIQUE (boardid, cardid, printingid)
);

ALTER TABLE "user" ADD COLUMN inventoryid UUID REFERENCES deck;
