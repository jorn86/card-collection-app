CREATE TABLE "tag" (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v1mc(),
  name VARCHAR(128) NOT NULL,
  parentid UUID REFERENCES "tag" ON DELETE CASCADE,
  userid UUID REFERENCES "user"
);

CREATE TABLE deck (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v1mc(),
  name VARCHAR(128) NOT NULL,
  userid UUID REFERENCES "user"
);

CREATE TABLE decktag (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v1mc(),
  deckid UUID NOT NULL REFERENCES deck ON DELETE CASCADE,
  tagid UUID NOT NULL REFERENCES "tag" ON DELETE CASCADE,
  UNIQUE (deckid, tagid)
);

CREATE TABLE deckrow (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v1mc(),
  deckid UUID NOT NULL REFERENCES deck ON DELETE CASCADE,
  cardid UUID NOT NULL REFERENCES card,
  printingid UUID REFERENCES printing,
  amount INT NOT NULL CHECK (amount > 0),
  UNIQUE (deckid, cardid, printingid)
);

ALTER TABLE "user" ADD COLUMN inventoryid UUID REFERENCES deck;
