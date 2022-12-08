package fr.shift.modeling.backend.data.redis;
/*
 * This file is part of the Modeling Assistant Recommender. Author: Maxime Savary-Leblanc
 * The Modeling Assistant Recommender is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * The Modeling Assistant Recommender is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with The Modeling Assistant Recommender. If not, see <https://www.gnu.org/licenses/>.
 */
import com.google.gson.Gson;
import fr.shift.modeling.backend.data.redis.entity.KeyEntity;
import org.reactivestreams.Publisher;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.ReactiveRedisConnection;
import org.springframework.data.redis.core.ReactiveRedisCallback;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.function.Function;

/**
 * Provides a set of utilities to communicate with a Redis Cache instance in a Reactive way.
 * It is customized to {@link KeyEntity} objects, as it is the only object stored in the cache.
 */
@Repository
public class RedisConnector {

    private final ReactiveRedisTemplate<String, String> template;
    private final Gson gson;

    public RedisConnector(ReactiveRedisTemplate<String, String> template, Gson gson) {
        this.template = template;
        this.gson = gson;
    }

    /**
     * Save an entity with a specific key in the cache
     * @param entity the entity to store
     * @param key the key under which the entity should be stored
     * @return true if success, else false
     */
    public Mono<Boolean> save(KeyEntity entity, String key) {

        return template.createMono(new ReactiveRedisCallback<Boolean>() {
            @Override
            public Publisher<Boolean> doInRedis(ReactiveRedisConnection connection) throws DataAccessException {
                ByteBuffer keyBuffer = ByteBuffer.wrap(key.getBytes());
                ByteBuffer valueBuffer = ByteBuffer.wrap(gson.toJson(entity).getBytes());
                return connection.stringCommands().set(keyBuffer, valueBuffer);
            }
        });

    }

    /**
     * Finds an entity with the key in parameter
     * @param key the key to search in the cache
     * @return the entity if found, else the mono is empty
     */
    public Mono<KeyEntity> find(String key) {

        return template.createMono(new ReactiveRedisCallback<KeyEntity>() {
            @Override
            public Publisher<KeyEntity> doInRedis(ReactiveRedisConnection connection) throws DataAccessException {
                ByteBuffer keyBuffer = ByteBuffer.wrap(key.getBytes());
                return connection.stringCommands().get(keyBuffer).map(new Function<>() {
                    @Override
                    public KeyEntity apply(ByteBuffer byteBuffer) {
                        String result = new String(byteBuffer.array());
                        return gson.fromJson(result, KeyEntity.class);
                    }
                });
            }
        });

    }


    /**
     * Finds a series of entities whose keys are in the list in parameter
     * @param keys the list of keys to search in the cache
     * @return a flux streaming the found entities, it might emit nothing if no entity is found
     */
    public Flux<KeyEntity> findAll(List<String> keys) {
        return template.createFlux(new ReactiveRedisCallback<KeyEntity>() {
            @Override
            public Publisher<KeyEntity> doInRedis(ReactiveRedisConnection connection) throws DataAccessException {
                return connection.stringCommands().get(
                                Flux.fromIterable(keys).map((key) ->
                                        new ReactiveRedisConnection.KeyCommand(ByteBuffer.wrap(key.getBytes()))))
                        .filter((response) -> response.getOutput() != null)
                        .map((response) -> new String(response.getOutput().array()))
                        .filter((json) -> !json.isEmpty())
                        .map(json -> gson.fromJson(json, KeyEntity.class));
            }
        });

    }
}
