SELECT
    c.id AS customer_id,
    COUNT(*) AS count
FROM creditCards cc
INNER JOIN customers c ON c.creditCardId = cc.id
WHERE cc.firstName = 'Austin'
  AND cc.lastName = 'Parker'
  AND cc.id = '4801789706541124'
  AND cc.expiration = '2009/08/01'
GROUP BY c.id;


ALTER TABLE sales ADD COLUMN quantity INT NOT NULL DEFAULT 1;