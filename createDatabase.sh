mysql -u root -e "drop database if exists bd2_grupo3;
                  create database bd2_grupo3;
                  CREATE USER IF NOT EXISTS 'bd2'@'localhost' IDENTIFIED BY 'bd2';
                  GRANT ALL PRIVILEGES ON bd2_grupo3.* TO 'bd2'@'localhost';
                  FLUSH PRIVILEGES;
                  "
