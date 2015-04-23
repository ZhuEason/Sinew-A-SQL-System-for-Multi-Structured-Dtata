#!/bin/bash

function f()
{
	echo "===============$1==========================="
	echo " "
	echo " "
	read -s -n1
} 

#loader
java -cp .:/home/eason/Downloads/postgresql-9.4-1201.jdbc41.jar userLayer.Action


java -cp .:/home/eason/Downloads/postgresql-9.4-1201.jdbc41.jar userLayer.Action

java -cp .:/home/eason/Downloads/postgresql-9.4-1201.jdbc41.jar userLayer.Action

java -cp .:/home/eason/Downloads/postgresql-9.4-1201.jdbc41.jar userLayer.Action


java -cp .:/home/eason/Downloads/postgresql-9.4-1201.jdbc41.jar userLayer.Action
f "loader"

#select
java -cp .:/home/eason/Downloads/postgresql-9.4-1201.jdbc41.jar processSql.Select 
f "select"

#update
java -cp .:/home/eason/Downloads/postgresql-9.4-1201.jdbc41.jar processSql.Update
f "update"

#select
java -cp .:/home/eason/Downloads/postgresql-9.4-1201.jdbc41.jar processSql.Select 
f "select"

#columnmaterializer
java -cp .:/home/eason/Downloads/postgresql-9.4-1201.jdbc41.jar storgeLayer.ColumnMaterializer

f "colmnmaterializer"

