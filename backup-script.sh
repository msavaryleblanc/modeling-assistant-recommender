#docker cp neo4j:/data ./data-dump
rm ./data-dump/neo4j/
mkdir ./data-dump/neo4j/
mv ./data-dump/neo4j/data ./data-dump/neo4j/data-old
rm -rf ./data-dump/neo4j/data
docker cp neo4j:/data/ ./data-dump/neo4j/data/
rm -rf ./data-dump/neo4j/data-old

rm ./data-dump/redis/
mkdir ./data-dump/redis/
mv ./data-dump/redis/data ./data-dump/redis/data-old
rm -rf ./data-dump/redis/data
docker exec redis redis-cli save
docker cp redis:data/ ./data-dump/redis/data/
rm -rf ./data-dump/redis/data-old
sleep 5
