#docker cp neo4j:/data ./data-dump
docker volume rm neo4j-data
docker volume create neo4j-data
docker create -v neo4j-data:/data-content --name temporary_container -it debian
docker start temporary_container
docker cp ./data-dump/neo4j/data/. temporary_container:/data-content/
docker stop temporary_container
docker rm temporary_container
sleep 5
docker volume rm redis-cache
docker volume create redis-cache
docker create -v redis-cache:/data-content --name temporary_container2 -it debian
docker start temporary_container2
docker cp ./data-dump/redis/data/. temporary_container2:/data-content/
docker stop temporary_container2
docker rm temporary_container2