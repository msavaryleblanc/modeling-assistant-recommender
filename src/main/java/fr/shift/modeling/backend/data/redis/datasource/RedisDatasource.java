package fr.shift.modeling.backend.data.redis.datasource;
/*
 * This file is part of the Modeling Assistant Recommender. Author: Maxime Savary-Leblanc
 * The Modeling Assistant Recommender is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * The Modeling Assistant Recommender is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with The Modeling Assistant Recommender. If not, see <https://www.gnu.org/licenses/>.
 */

import fr.shift.modeling.backend.data.redis.RedisConnector;
import fr.shift.modeling.backend.data.redis.entity.KeyEntity;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Function;

/**
 * The datasource for the Redis Cache instance, which acts like DAO. It performs the adapted query on the
 * {@link RedisConnector} to retrieve the data for the needs of the RemoteDataRepository objects.
 */
@Repository
public class RedisDatasource {

    private RedisConnector redisConnector;

    public RedisDatasource(RedisConnector redisConnector) {
        this.redisConnector = redisConnector;
    }

    public void updateOrCreateKey(String name, KeyEntity.Type type) {
        String key = createKey(name, type);

        redisConnector.find(key)
                .map(new Function<KeyEntity, KeyEntity>() {
                    @Override
                    public KeyEntity apply(KeyEntity keyEntity) {
                        keyEntity.setValue(keyEntity.getValue() + 1);
                        return keyEntity;
                    }
                })
                .switchIfEmpty(Mono.just(new KeyEntity(name, type)))
                .flatMap(new Function<KeyEntity, Mono<Boolean>>() {
                    @Override
                    public Mono<Boolean> apply(KeyEntity keyEntity) {
                        return redisConnector.save(keyEntity, createKey(keyEntity));
                    }
                }).subscribe();

    }

    public Mono<List<KeyEntity>> getAttributeTotalOccurrence(List<String> attributeNameList, KeyEntity.Type type){
        List<String> keys = attributeNameList.stream().map((s) -> createKey(s, type)).toList();
        return redisConnector.findAll(keys).collectList();
    }
    private String createKey(KeyEntity keyEntity) {
        return createKey(keyEntity.getName(), keyEntity.getType());
    }

    private String createKey(String name, KeyEntity.Type type) {
        return name + "::" + type.toString();
    }
}
