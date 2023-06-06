@REM mysql -u root -p -e "DROP DATABASE LIBMAN_ACCOUNTS"
@REM mysql -u root -p -e "DROP DATABASE LIBMAN_INVENTORY"

@REM mysql -u root -p -e "CREATE DATABASE LIBMAN_ACCOUNTS"
@REM mysql -u root -p -e "CREATE DATABASE LIBMAN_INVENTORY"

@REM mysql -u root -p LIBMAN_ACCOUNTS < ./database/dumps/libman-accounts.sql
@REM mysql -u root -p LIBMAN_INVENTORY < ./database/dumps/libman-inventory.sql

@echo off
set /p password="Enter MySQL root user password: "

mysql -u root -p%password% -e "DROP DATABASE LIBMAN_ACCOUNTS"
mysql -u root -p%password% -e "DROP DATABASE LIBMAN_INVENTORY"

mysql -u root -p%password% -e "CREATE DATABASE LIBMAN_ACCOUNTS"
mysql -u root -p%password% -e "CREATE DATABASE LIBMAN_INVENTORY"

mysql -u root -p%password% LIBMAN_ACCOUNTS < ./dumps/libman-accounts.sql
mysql -u root -p%password% LIBMAN_INVENTORY < ./dumps/libman-inventory.sql