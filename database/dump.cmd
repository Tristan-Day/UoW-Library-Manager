@REM mariadb-dump --databases LIBMAN_ACCOUNTS --user libman-client --password > ./dumps/libman-accounts.sql
@REM mariadb-dump --databases LIBMAN_INVENTORY --user libman-client --password > ./dumps/libman-inventory.sql

@REM mysqldump -u root -p LIBMAN_ACCOUNTS > ./dumps/libman-accounts.sql
@REM mysqldump -u root -p LIBMAN_INVENTORY > ./dumps/libman-inventory.sql

@echo off
set /p password="Enter MySQL root user password: "

mysqldump -u root -p%password% LIBMAN_ACCOUNTS > ./dumps/libman-accounts.sql
mysqldump -u root -p%password% LIBMAN_INVENTORY > ./dumps/libman-inventory.sql
