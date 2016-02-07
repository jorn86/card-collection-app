CREATE MATERIALIZED VIEW setstatistics AS (
    WITH firstprinting AS (
        SELECT DISTINCT ON (printing.cardid) printing.id AS printingid, printing.cardid AS cardid, "set".id AS setid
      FROM printing LEFT JOIN "set" ON "set".id = printing.setid ORDER BY printing.cardid, "set".priority, "set".releasedate ASC NULLS LAST)
  SELECT s.*,
    COUNT(DISTINCT p.cardid) AS cards,
    COUNT(p.cardid) AS prints,
    SUM(CASE WHEN f.cardid IS NULL THEN 0 ELSE 1 END) AS newcards,
    SUM(CASE WHEN f.cardid IS NULL THEN 1 ELSE 0 END) AS reprints
  FROM "set" s
    LEFT JOIN printing p ON s.id = p.setid
    LEFT JOIN firstprinting f ON f.printingid = p.id AND f.setid = s.id
  GROUP BY s.id, name
);
