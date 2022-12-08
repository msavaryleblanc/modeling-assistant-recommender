package fr.shift.modeling.backend.data;
/*
 * This file is part of the Modeling Assistant Recommender. Author: Maxime Savary-Leblanc
 * The Modeling Assistant Recommender is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * The Modeling Assistant Recommender is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with The Modeling Assistant Recommender. If not, see <https://www.gnu.org/licenses/>.
 */

import fr.shift.modeling.backend.data.neo4j.datasource.Neo4jDatasource;
import fr.shift.modeling.backend.data.neo4j.entity.attribute.AttributeContextQueryResult;
import fr.shift.modeling.backend.data.neo4j.entity.attribute.AttributeOccurrenceQueryResult;
import fr.shift.modeling.backend.data.neo4j.entity.attribute.AttributeSiblingQueryResult;
import fr.shift.modeling.backend.data.neo4j.entity.attribute.AttributeTotalOccurenceQueryResult;
import fr.shift.modeling.backend.data.neo4j.entity.relation.RelationContextQueryResult;
import fr.shift.modeling.backend.data.neo4j.entity.relation.RelationOccurrenceQueryResult;
import fr.shift.modeling.backend.data.neo4j.entity.relation.RelationSiblingQueryResult;
import fr.shift.modeling.backend.data.neo4j.entity.relation.RelationTotalOccurenceQueryResult;
import fr.shift.modeling.backend.data.redis.datasource.RedisDatasource;
import fr.shift.modeling.backend.data.redis.entity.KeyEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * The RelationRecommenderRemoteDatasource is responsible for collecting remote data to compute Relation recommendations.
 * The different methods it holds describe how data is retrieved for each involved individual score.
 */
@Service
public class RelationRecommenderRemoteDatasource {

    private Neo4jDatasource neo4jDatasource;
    private RedisDatasource redisDatasource;

    public RelationRecommenderRemoteDatasource(Neo4jDatasource neo4jDatasource, RedisDatasource redisDatasource) {
        this.neo4jDatasource = neo4jDatasource;
        this.redisDatasource = redisDatasource;
    }


    public Mono<Map<String, RelationTotalOccurenceQueryResult>> getRelationTotalOccurrence(List<String> classNameList) {
        return redisDatasource.getKeyTotalOccurrence(classNameList, KeyEntity.Type.CLASS)
                .map(new Function<>() {
                    @Override
                    public Map<String, RelationTotalOccurenceQueryResult> apply(List<KeyEntity> keyEntities) {
                        Map<String, RelationTotalOccurenceQueryResult> attributeExcluQueryResultMap = new HashMap<>();
                        for (KeyEntity keyEntity : keyEntities) {
                            attributeExcluQueryResultMap.put(keyEntity.getName(), new RelationTotalOccurenceQueryResult(keyEntity.getName(), keyEntity.getValue()));
                        }
                        return attributeExcluQueryResultMap;
                    }
                });
    }


    public Mono<List<RelationOccurrenceQueryResult>> getRelationOccurrence(String className, List<String> linkedClassNames) {
        return neo4jDatasource.getRelationOccurrence(className, linkedClassNames);
    }


    public Mono<List<RelationSiblingQueryResult>> getRelationSiblings(String className, List<String> linkedClassNameList) {
        return neo4jDatasource.getRelationSiblings(className, linkedClassNameList);
    }


    public Mono<List<RelationContextQueryResult>> getRelationContext(String classname, List<String> classNameList, List<String> linkedClassNames) {
        return neo4jDatasource.getRelationContext(classname, classNameList, linkedClassNames);
    }

}
