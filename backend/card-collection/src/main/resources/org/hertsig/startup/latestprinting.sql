CREATE OR REPLACE VIEW latestprinting AS (
  SELECT DISTINCT ON(printing.cardid) printing.*
  FROM printing LEFT JOIN "set" ON set.id = printing.setid
  ORDER BY printing.cardid, set.priority, set.releasedate DESC);
