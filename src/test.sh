#########################################################################
# File Name: test.sh
# Author: zhuyisong
# mail: zhuyisong1994@gmail.com
# Created Time: Wed 29 Apr 2015 11:17:44 HKT
#########################################################################
#!/bin/bash

function f() {
  echo "===================$1====================="
  echo " "
  echo " "
  read -s -n1
}

rm userVersion2/*.class

#loader
javac -classpath .:userVersion2/:postgresqlJDBC/ userVersion2/Loader.java

java -cp .:userVersion2/:postgresqlJDBC/:/home/eason/Downloads/postgresql-9.4-1201.jdbc41.jar userVersion2.Loader jsonTxt/2.json

#select
javac -classpath ./ processSql/Select.java
java -cp .:/home/eason/Downloads/postgresql-9.4-1201.jdbc41.jar processSql.Select 
f "select"


#update
javac -classpath ./ processSql/Update.java
java -cp .:/home/eason/Downloads/postgresql-9.4-1201.jdbc41.jar processSql.Update
f "update"

#select
java -cp .:/home/eason/Downloads/postgresql-9.4-1201.jdbc41.jar processSql.Select 
f "select"

#columnmaterializer
javac -classpath ./ storgeLayer/ColumnMaterializer.java
java -cp .:/home/eason/Downloads/postgresql-9.4-1201.jdbc41.jar storgeLayer.ColumnMaterializer

f "colmnmaterializer"

