CREATE OR REPLACE VIEW deckentryview AS (
  SELECT deckrow.*, board.deckid, board.name AS boardname, card.name, card.normalizedname, card.cost, card.cmc, card.fulltype,
    array_to_string(card.supertypes, ' ') AS supertype,
    array_to_string(card.types, ' ') AS type,
    array_to_string(card.subtypes, ' ') AS subtype,
    coalesce(printing.multiverseid, latestprinting.multiverseid) AS multiverseid,
    coalesce(printing.rarity, latestprinting.rarity) AS rarity,
    backprinting.multiverseid AS multiverseidBack,
    printing.multiverseid IS NULL AS setisfallback,
    set.gatherercode AS setcode,
    card.layout = 'split' OR card.layout = 'split-parent' AS split
  FROM deckrow
    LEFT JOIN deckboardview board ON board.id = deckrow.boardid
    LEFT JOIN card ON card.id = deckrow.cardid
    LEFT JOIN printing ON printing.id = deckrow.printingid
    LEFT JOIN latestprinting ON latestprinting.cardid = deckrow.cardid
    LEFT JOIN card backcard ON card.id = backcard.doublefacefront LEFT JOIN latestprinting backprinting ON backprinting.cardid = backcard.id
    LEFT JOIN set ON coalesce(printing.setid, latestprinting.setid) = set.id
);
