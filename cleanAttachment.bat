@echo off
set MYSQL_USER=user
set MYSQL_PASSWORD=password
set MYSQL_DATABASE=codedthoughts
set MYSQL_CONTAINER_NAME=dockerfiles-db-1


set QUERY1=delete from ctblogatt where blog_id is null

docker exec %MYSQL_CONTAINER_NAME% mysql -u%MYSQL_USER% -p%MYSQL_PASSWORD% -D%MYSQL_DATABASE% -e "%QUERY1%"