--Extend password length: 4(algorithm) + 7(iterations with extend) + 3(hashSize) + 24*4/3(salt in base64) + 18*4/3(hash in base64) + 4(':' signs)
--algorithm:iterations:hashSize:salt:hash
ALTER TABLE account ALTER COLUMN password VARCHAR(80);

--Write new passwords
UPDATE account SET password = 'sha1:64000:18:UU0OJFzmkC2clo50Tgzbldf8UoG+aYSF:fw2cKkEbPw9Kpwpj7StBUGyb' WHERE email = 'fpi@bk.ru';
UPDATE account SET password = 'sha1:64000:18:Qmea0IAjB0Ra4jy6WUb7GLiRLal0DT2F:3U9nLiCjFBI0piSGoM3gy4VQ' WHERE email = 'admin';
