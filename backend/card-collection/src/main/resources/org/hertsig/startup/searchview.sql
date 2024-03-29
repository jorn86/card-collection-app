CREATE MATERIALIZED VIEW searchview AS (
    WITH colors AS (
      SELECT id,
        cost LIKE '%W%' OR text LIKE '%{W}%' OR text LIKE '%{W/_}%' OR text LIKE '%{_/W}%' OR 'W' = ANY(colors) AS ci_w,
        cost LIKE '%U%' OR text LIKE '%{U}%' OR text LIKE '%{U/_}%' OR text LIKE '%{_/U}%' OR 'U' = ANY(colors) AS ci_u,
        cost LIKE '%B%' OR text LIKE '%{B}%' OR text LIKE '%{B/_}%' OR text LIKE '%{_/B}%' OR 'B' = ANY(colors) AS ci_b,
        cost LIKE '%R%' OR text LIKE '%{R}%' OR text LIKE '%{R/_}%' OR text LIKE '%{_/R}%' OR 'R' = ANY(colors) AS ci_r,
        cost LIKE '%G%' OR text LIKE '%{G}%' OR text LIKE '%{G/_}%' OR text LIKE '%{_/G}%' OR 'G' = ANY(colors) AS ci_g,
        cost LIKE '%W%' OR 'W' = ANY(colors) AS c_w,
        cost LIKE '%U%' OR 'U' = ANY(colors) AS c_u,
        cost LIKE '%B%' OR 'B' = ANY(colors) AS c_b,
        cost LIKE '%R%' OR 'R' = ANY(colors) AS c_r,
        cost LIKE '%G%' OR 'G' = ANY(colors) AS c_g
        FROM card
    ), cardcolors AS (
      SELECT card.id,
        COALESCE(c1.ci_w OR c2.ci_w OR c3.ci_w OR c4.ci_w, FALSE) AS ci_w,
        COALESCE(c1.ci_u OR c2.ci_u OR c3.ci_u OR c4.ci_u, FALSE) AS ci_u,
        COALESCE(c1.ci_b OR c2.ci_b OR c3.ci_b OR c4.ci_b, FALSE) AS ci_b,
        COALESCE(c1.ci_r OR c2.ci_r OR c3.ci_r OR c4.ci_r, FALSE) AS ci_r,
        COALESCE(c1.ci_g OR c2.ci_g OR c3.ci_g OR c4.ci_g, FALSE) AS ci_g,
        COALESCE(c1.c_w OR c3.c_w OR c4.c_w, FALSE) AS c_w,
        COALESCE(c1.c_u OR c3.c_u OR c4.c_u, FALSE) AS c_u,
        COALESCE(c1.c_b OR c3.c_b OR c4.c_b, FALSE) AS c_b,
        COALESCE(c1.c_r OR c3.c_r OR c4.c_r, FALSE) AS c_r,
        COALESCE(c1.c_g OR c3.c_g OR c4.c_g, FALSE) AS c_g
      FROM card
        LEFT JOIN colors c1 ON c1.id = card.id
        LEFT JOIN colors c2 ON c2.id = (SELECT id FROM card c2 WHERE c2.doublefacefront = card.id)
        LEFT JOIN colors c3 ON c3.id = (SELECT id FROM card c2 WHERE splitcardparent = card.id ORDER BY id ASC LIMIT 1)
        LEFT JOIN colors c4 ON c4.id = (SELECT id FROM card c2 WHERE splitcardparent = card.id ORDER BY id DESC LIMIT 1)
    ), numbers AS (
      SELECT id,
        array(SELECT array_to_string(regexp_matches(power, '-?\d+', 'g'), '')) AS power,
        array(SELECT array_to_string(regexp_matches(toughness, '-?\d+', 'g'), '')) AS toughness
      FROM card
    )
    SELECT card.name, card.normalizedname, card.fulltype, card.supertypes, card.types, card.subtypes, card.cost, card.cmc, card.loyalty, card.layout,
      cardcolors.*,
      (CASE WHEN ci_w THEN 1 ELSE 0 END + CASE WHEN ci_u THEN 1 ELSE 0 END + CASE WHEN ci_b THEN 1 ELSE 0 END + CASE WHEN ci_r THEN 1 ELSE 0 END + CASE WHEN ci_g THEN 1 ELSE 0 END) AS cia,
      (CASE WHEN c_w THEN 1 ELSE 0 END + CASE WHEN c_u THEN 1 ELSE 0 END + CASE WHEN c_b THEN 1 ELSE 0 END + CASE WHEN c_r THEN 1 ELSE 0 END + CASE WHEN c_g THEN 1 ELSE 0 END) AS ca,
      latestprinting.multiverseid, latestprinting.rarity,
      backprinting.multiverseid AS multiverseidBack,
      set.gatherercode AS setcode,
      replace(regexp_replace(card.text,'\(.+\)',''), card.name, '~') AS text,
      CASE WHEN array_length(numbers.power,1) > 0 THEN to_number(numbers.power[1], 'S99') WHEN card.power LIKE '%*%' THEN 0 ELSE NULL END AS power,
      CASE WHEN array_length(numbers.toughness,1) > 0 THEN to_number(numbers.toughness[1], 'S99') WHEN card.toughness LIKE '%*%' THEN 0 ELSE NULL END AS toughness,
      array(SELECT format FROM legality WHERE cardid = card.id)::text[] AS formats,
      array(SELECT gatherercode FROM set WHERE id IN (SELECT setid FROM printing WHERE printing.cardid = card.id)) AS setcodes
    FROM card
      LEFT JOIN cardcolors ON cardcolors.id = card.id
      LEFT JOIN numbers ON numbers.id = card.id
      LEFT JOIN latestprinting ON latestprinting.cardid = card.id
      LEFT JOIN card backcard ON card.id = backcard.doublefacefront LEFT JOIN latestprinting backprinting ON backprinting.cardid = backcard.id
      LEFT JOIN set ON latestprinting.setid = set.id
    WHERE card.doublefacefront IS NULL AND card.splitcardparent IS NULL AND card.layout != 'vanguard'
);
