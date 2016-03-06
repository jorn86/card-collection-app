CREATE MATERIALIZED VIEW setstatistics AS (
    WITH firstprinting AS (
        SELECT DISTINCT ON (printing.cardid) cardid, setid
        FROM printing LEFT JOIN "set" ON "set".id = printing.setid
        ORDER BY printing.cardid, "set".priority, "set".releasedate ASC NULLS LAST),
    cards AS (SELECT COUNT(printing.id) AS count, cardid, setid FROM printing LEFT JOIN card ON cardid = card.id
        WHERE layout != 'token' GROUP BY cardid, setid),
    prints AS (SELECT c.*, EXISTS(SELECT 1 FROM firstprinting f WHERE f.cardid = c.cardid AND f.setid = c.setid) AS isnew FROM cards c)
  SELECT s.*,
    SUM(p.count) AS prints,
    COUNT(p.cardid) AS cards,
    SUM(CASE WHEN p.isnew THEN 0 ELSE 1 END) AS reprints,
    SUM(CASE WHEN p.isnew THEN 1 ELSE 0 END) AS newcards
  FROM "set" s LEFT JOIN prints p ON s.id = p.setid
  GROUP BY s.id, s.name
);
