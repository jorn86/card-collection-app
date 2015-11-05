CREATE TABLE deck (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v1mc(),
  name TEXT NOT NULL,
  userid UUID NOT NULL REFERENCES "user"
);

CREATE TABLE deckrow (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v1mc(),
  deckid UUID NOT NULL REFERENCES "deck"
);
