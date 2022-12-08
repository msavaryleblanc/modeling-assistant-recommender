package fr.shift.modeling.backend.data;
/*
 * This file is part of the Modeling Assistant Recommender. Author: Maxime Savary-Leblanc
 * The Modeling Assistant Recommender is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * The Modeling Assistant Recommender is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with The Modeling Assistant Recommender. If not, see <https://www.gnu.org/licenses/>.
 */

import fr.shift.modeling.backend.controller.entity.PartialRecommendationItem;
import fr.shift.modeling.backend.data.mapper.relation.RelationContextMapperProvider;
import fr.shift.modeling.backend.data.mapper.relation.RelationOccurrenceAndExclusivityMapperProvider;
import fr.shift.modeling.backend.data.mapper.relation.RelationSiblingMapperProvider;
import fr.shift.modeling.backend.data.neo4j.entity.relation.RelationOccurrenceQueryResult;
import fr.shift.modeling.backend.data.neo4j.entity.relation.RelationTotalOccurenceQueryResult;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * The DataRepository is responsible for retrieving the unordered data from the Remote or Local repositories,
 * and for mapping them into {@link PartialRecommendationItem} objects which can be used by the domain layer
 * of the application.
 */

@Service
public class RelationRecommenderDataRepository {

    private final RelationRecommenderRemoteDatasource relationRecommenderRemoteDatasource;

    public RelationRecommenderDataRepository(RelationRecommenderRemoteDatasource relationRecommenderRemoteDatasource) {
        this.relationRecommenderRemoteDatasource = relationRecommenderRemoteDatasource;
    }

    public Mono<List<PartialRecommendationItem>> getOccurrenceAndExclusivityRankedItems(String className, List<String> linkedClassNames){
        return relationRecommenderRemoteDatasource.getRelationOccurrence(className, linkedClassNames)
                .flatMap(new Function<List<RelationOccurrenceQueryResult>, Mono<Tuple2<List<RelationOccurrenceQueryResult>, Map<String, RelationTotalOccurenceQueryResult>>>>() {
                    @Override
                    public Mono<Tuple2<List<RelationOccurrenceQueryResult>, Map<String, RelationTotalOccurenceQueryResult>>> apply(List<RelationOccurrenceQueryResult> relationOccurrenceQueryResultList) {
                        List<String> attributeNameList = relationOccurrenceQueryResultList.stream().map(RelationOccurrenceQueryResult::getClassName).toList();
                        return relationRecommenderRemoteDatasource.getRelationTotalOccurrence(attributeNameList)
                                .map(new Function<>() {
                                    @Override
                                    public Tuple2<List<RelationOccurrenceQueryResult>, Map<String, RelationTotalOccurenceQueryResult>> apply(Map<String, RelationTotalOccurenceQueryResult> totalOccurenceQueryResultMap) {
                                        return Tuples.of(relationOccurrenceQueryResultList, totalOccurenceQueryResultMap);
                                    }
                                });
                    }
                }).map(RelationOccurrenceAndExclusivityMapperProvider.getMapper());
    }


    public Mono<List<PartialRecommendationItem>> getSiblingRankedItems(String className, List<String> linkedClassNames){
        return relationRecommenderRemoteDatasource.getRelationSiblings(className, linkedClassNames)
                .map(RelationSiblingMapperProvider.getMapper(linkedClassNames));
    }


    public Mono<List<PartialRecommendationItem>> getContextRankedItems(String className, List<String> classNameList, List<String> linkedClassNames){
        return relationRecommenderRemoteDatasource.getRelationContext(className, classNameList, linkedClassNames)
                .map(RelationContextMapperProvider.getMapper());
    }

}

