#docker cp neo4j:/data ./data-dump
docker volume rm neo4j-data
docker volume create neo4j-data

docker volume rm redis-cache
docker volume create redis-cache