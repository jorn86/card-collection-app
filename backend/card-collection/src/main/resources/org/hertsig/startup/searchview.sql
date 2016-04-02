CREATE MATERIALIZED VIEW searchview AS (
    WITH colors AS (
      SELECT id,
        cost LIKE '%W%' OR text LIKE '%{W}%' OR text LIKE '%{W/_}%' OR text LIKE '%{_/W}%' AS ci_w,
        cost LIKE '%U%' OR text LIKE '%{U}%' OR text LIKE '%{U/_}%' OR text LIKE '%{_/U}%' AS ci_u,
        cost LIKE '%B%' OR text LIKE '%{B}%' OR text LIKE '%{B/_}%' OR text LIKE '%{_/B}%' AS ci_b,
        cost LIKE '%R%' OR text LIKE '%{R}%' OR text LIKE '%{R/_}%' OR text LIKE '%{_/R}%' AS ci_r,
        cost LIKE '%G%' OR text LIKE '%{G}%' OR text LIKE '%{G/_}%' OR text LIKE '%{_/G}%' AS ci_g,
        cost LIKE '%W%' AS c_w,
        cost LIKE '%U%' AS c_u,
        cost LIKE '%B%' AS c_b,
        cost LIKE '%R%' AS c_r,
        cost LIKE '%G%' AS c_g
        FROM card
    )
    SELECT card.*,
      regexp_replace(text,'\(.+\)','') AS text_without_reminder,
      COALESCE(c1.ci_w OR c2.ci_w OR c3.ci_w OR c4.ci_w, FALSE) AS ci_w,
      COALESCE(c1.ci_u OR c2.ci_u OR c3.ci_u OR c4.ci_u, FALSE) AS ci_u,
      COALESCE(c1.ci_b OR c2.ci_b OR c3.ci_b OR c4.ci_b, FALSE) AS ci_b,
      COALESCE(c1.ci_r OR c2.ci_r OR c3.ci_r OR c4.ci_r, FALSE) AS ci_r,
      COALESCE(c1.ci_g OR c2.ci_g OR c3.ci_g OR c4.ci_g, FALSE) AS ci_g,
      COALESCE(c1.c_w OR c2.c_w OR c3.c_w OR c4.c_w, FALSE) AS c_w,
      COALESCE(c1.c_u OR c2.c_u OR c3.c_u OR c4.c_u, FALSE) AS c_u,
      COALESCE(c1.c_b OR c2.c_b OR c3.c_b OR c4.c_b, FALSE) AS c_b,
      COALESCE(c1.c_r OR c2.c_r OR c3.c_r OR c4.c_r, FALSE) AS c_r,
      COALESCE(c1.c_g OR c2.c_g OR c3.c_g OR c4.c_g, FALSE) AS c_g
    FROM card
      LEFT JOIN colors c1 ON c1.id = card.id
      LEFT JOIN colors c2 ON c2.id = (SELECT id FROM card c2 WHERE c2.doublefacefront = card.id)
      LEFT JOIN colors c3 ON c3.id = (SELECT id FROM card c2 WHERE splitcardparent = card.id ORDER BY id ASC LIMIT 1)
      LEFT JOIN colors c4 ON c4.id = (SELECT id FROM card c2 WHERE splitcardparent = card.id ORDER BY id DESC LIMIT 1)
    WHERE card.doublefacefront IS NULL AND card.splitcardparent IS NULL
);
