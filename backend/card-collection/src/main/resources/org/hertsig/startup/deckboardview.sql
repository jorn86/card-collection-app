CREATE OR REPLACE VIEW deckboardview AS (
  SELECT board.*, deck.userid, deck.name AS deckname, deck.inventory
  FROM board LEFT JOIN deck ON deck.id = board.deckid
);
