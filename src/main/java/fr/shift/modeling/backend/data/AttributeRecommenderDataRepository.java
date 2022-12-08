package fr.shift.modeling.backend.data;
/*
 * This file is part of the Modeling Assistant Recommender. Author: Maxime Savary-Leblanc
 * The Modeling Assistant Recommender is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * The Modeling Assistant Recommender is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with The Modeling Assistant Recommender. If not, see <https://www.gnu.org/licenses/>.
 */

import fr.shift.modeling.backend.data.mapper.attribute.AttributeContextMapperProvider;
import fr.shift.modeling.backend.data.mapper.attribute.AttributeOccurrenceAndExclusivityMapperProvider;
import fr.shift.modeling.backend.data.mapper.attribute.AttributeSiblingMapperProvider;
import fr.shift.modeling.backend.data.neo4j.entity.attribute.AttributeTotalOccurenceQueryResult;
import fr.shift.modeling.backend.data.neo4j.entity.attribute.AttributeOccurrenceQueryResult;
import fr.shift.modeling.backend.controller.entity.PartialRecommendationItem;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.*;
import java.util.function.Function;

/**
 * The DataRepository is responsible for retrieving the unordered data from the Remote or Local repositories,
 * and for mapping them into {@link PartialRecommendationItem} objects which can be used by the domain layer
 * of the application.
 */

@Service
public class AttributeRecommenderDataRepository {

    private final AttributeRecommenderRemoteDatasource attributeRecommenderRemoteDataSource;

    public AttributeRecommenderDataRepository(AttributeRecommenderRemoteDatasource attributeRecommenderRemoteDataSource) {
        this.attributeRecommenderRemoteDataSource = attributeRecommenderRemoteDataSource;
    }

    /**
     * Computes the PartialRecommendationItem objects for score s1 and s2, which are Recurrence and Exclusivity.
     * @param className the class name for which to retrieve the attribute recommendation.
     * @param attributeNameList the list of attributes names already contained by the target class with name className
     * @return the list of PartialRecommendationItem for s1 and s2
     */
    public Mono<List<PartialRecommendationItem>> getOccurrenceAndExclusivityRankedItems(String className, List<String> attributeNameList){
        return attributeRecommenderRemoteDataSource.getAttributeOccurence(className, attributeNameList)
                .flatMap(new Function<List<AttributeOccurrenceQueryResult>, Mono<Tuple2<List<AttributeOccurrenceQueryResult>, Map<String, AttributeTotalOccurenceQueryResult>>>>() {
                    @Override
                    public Mono<Tuple2<List<AttributeOccurrenceQueryResult>, Map<String, AttributeTotalOccurenceQueryResult>>> apply(List<AttributeOccurrenceQueryResult> attributeOccurrenceQueryResults) {
                        List<String> attributeNameList = attributeOccurrenceQueryResults.stream().map(AttributeOccurrenceQueryResult::getAttributeName).toList();
                        return attributeRecommenderRemoteDataSource.getAttributeTotalOccurrence(attributeNameList)
                                .map(new Function<>() {
                                    @Override
                                    public Tuple2<List<AttributeOccurrenceQueryResult>, Map<String, AttributeTotalOccurenceQueryResult>> apply(Map<String, AttributeTotalOccurenceQueryResult> attributeExcluQueryResults) {
                                        return Tuples.of(attributeOccurrenceQueryResults, attributeExcluQueryResults);
                                    }
                                });
                    }
                }).map(AttributeOccurrenceAndExclusivityMapperProvider.getMapper(attributeNameList));
    }

    /**
     * Computes the PartialRecommendationItem objects for score s3 Siblings.
     * @param attributeNameList the list of attributes names already contained by the target class with name className,
     *                          for which we want siblings.
     * @param excludeDefaultAttributes which indicates if very common attributes such as id must be omitted for sibling computation.
     * @return the list of PartialRecommendationItem for s3
     */
    public Mono<List<PartialRecommendationItem>> getSiblingRankedItems(List<String> attributeNameList, boolean excludeDefaultAttributes){
        return attributeRecommenderRemoteDataSource.getAttributeSiblings(attributeNameList, excludeDefaultAttributes).map(AttributeSiblingMapperProvider.getMapper(attributeNameList));
    }

    /**
     * Computes the PartialRecommendationItem objects for score s4 Context
     * @param className the name of the class targeted for recommendations
     * @param classNameList the name of classes in the project context, e.g. surrounding className in the diagram
     * @param attributeNameList the name of attributes already owned by the targetClass
     * @return the list of PartialRecommendationItem for s4
     */
    public Mono<List<PartialRecommendationItem>> getContextRankedItems(String className, List<String> classNameList, List<String> attributeNameList){
        return attributeRecommenderRemoteDataSource.getAttributeContext(className, classNameList, attributeNameList).map(AttributeContextMapperProvider.getMapper());
    }

}

