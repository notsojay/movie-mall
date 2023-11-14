ALTER TABLE movies ADD COLUMN price DECIMAL(5, 2);
UPDATE movies SET price = ROUND(9 + (RAND() * (50 - 9)), 0);
