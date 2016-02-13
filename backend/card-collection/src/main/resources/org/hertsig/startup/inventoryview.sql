CREATE OR REPLACE VIEW inventoryview AS (
  SELECT deckrow.*, deck.userid
  FROM deckrow
    LEFT JOIN board ON board.id = deckrow.boardid
    LEFT JOIN deck ON deck.id = board.deckid
  WHERE deck.inventory
);
