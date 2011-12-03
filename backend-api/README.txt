Howto do database changes.

$> mvn clean process-classes
$> mvn openjpa:sql
$> less ./target/database.sql

if ok

$> cp ./target/database.sql src/main/sql/mysql/

