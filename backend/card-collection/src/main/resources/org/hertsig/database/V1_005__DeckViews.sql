CREATE VIEW latestprinting AS (
    WITH dates AS (SELECT card.id AS cardid, MAX("set".releasedate) AS releasedate
      FROM card JOIN printing ON card.id = printing.cardid JOIN "set" ON set.id = printing.setid
      GROUP BY card.id)
    SELECT dates.cardid, set.id AS setid, set.gatherercode, printing.id AS printingid
      FROM dates JOIN set ON set.releasedate = dates.releasedate
        LEFT JOIN printing ON set.id = printing.setid AND dates.cardid = printing.cardid
);

CREATE VIEW deckrowwithprinting AS (
    SELECT deckrow.id, deckrow.deckid, deckrow.cardid, deckrow.amount,
      (CASE WHEN deckrow.printingid IS NULL THEN latestprinting.printingid ELSE deckrow.printingid END) AS printingid
    FROM deckrow LEFT JOIN latestprinting ON deckrow.cardid = latestprinting.cardid
);
