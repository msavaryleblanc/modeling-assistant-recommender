package fr.shift.modeling.backend.data.mapper.relation;
/*
 * This file is part of the Modeling Assistant Recommender. Author: Maxime Savary-Leblanc
 * The Modeling Assistant Recommender is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * The Modeling Assistant Recommender is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with The Modeling Assistant Recommender. If not, see <https://www.gnu.org/licenses/>.
 */

import fr.shift.modeling.backend.controller.entity.PartialRecommendationItem;
import fr.shift.modeling.backend.data.neo4j.entity.relation.RelationOccurrenceQueryResult;
import fr.shift.modeling.backend.data.neo4j.entity.relation.RelationTotalOccurenceQueryResult;
import reactor.util.function.Tuple2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Is responsible for transforming {@link RelationOccurrenceQueryResult} and {@link RelationTotalOccurenceQueryResult}
 * from the remote data source to {@link PartialRecommendationItem} which is the standard object for an individual scores.
 * It holds the logic of computing score s1 (Recurrence) and s2 (Exclusivity).
 */
public class RelationOccurrenceAndExclusivityMapperProvider {

    public static Function<Tuple2<List<RelationOccurrenceQueryResult>, Map<String, RelationTotalOccurenceQueryResult>>, List<PartialRecommendationItem>> getMapper() {
        return new Function<Tuple2<List<RelationOccurrenceQueryResult>, Map<String, RelationTotalOccurenceQueryResult>>, List<PartialRecommendationItem>>() {
            @Override
            public List<PartialRecommendationItem> apply(Tuple2<List<RelationOccurrenceQueryResult>, Map<String, RelationTotalOccurenceQueryResult>> objects) {

                Map<String, PartialRecommendationItem> itemMap = new HashMap<>();
                Map<String, Integer> counterMap = new HashMap<>();
                //Because the list is ordered, we know the first value is the max.
                int maxOcc = -1;
                for (RelationOccurrenceQueryResult relationOccurrenceQueryResult : objects.getT1()) {

                        String relationType = RelationTypeMapper.getStringType(relationOccurrenceQueryResult.getRelationType(), relationOccurrenceQueryResult.isEndNode());

                        itemMap.computeIfAbsent(relationOccurrenceQueryResult.getClassName(), new Function<String, PartialRecommendationItem>() {
                            @Override
                            public PartialRecommendationItem apply(String s) {
                                return new PartialRecommendationItem(relationOccurrenceQueryResult.getClassName());
                            }
                        });
                        counterMap.computeIfAbsent(relationOccurrenceQueryResult.getClassName(), new Function<String, Integer>() {
                            @Override
                            public Integer apply(String s) {
                                return 0;
                            }
                        });

                        counterMap.replace(relationOccurrenceQueryResult.getClassName(), counterMap.get(relationOccurrenceQueryResult.getClassName()) + relationOccurrenceQueryResult.getOccNumber());
                        itemMap.get(relationOccurrenceQueryResult.getClassName()).addType(relationType, relationOccurrenceQueryResult.getOccNumber());

                        if (counterMap.get(relationOccurrenceQueryResult.getClassName()) > maxOcc) {
                            maxOcc = counterMap.get(relationOccurrenceQueryResult.getClassName());
                        }

                }

                List<PartialRecommendationItem> exclusivityRecommendationItemList = new ArrayList<>();

                for (Map.Entry<String, PartialRecommendationItem> entry : itemMap.entrySet()) {
                    double score = (counterMap.get(entry.getKey()) * 1d) / (maxOcc * 1d);
                    entry.getValue().setScore(score);
                    entry.getValue().setScoreName("s1");

                    PartialRecommendationItem exclusivityItem = new PartialRecommendationItem(entry.getValue().getName());
                    exclusivityItem.setScoreName("s2");
                    double score2 = (counterMap.get(entry.getKey()) * 1d) / objects.getT2().get(entry.getValue().getName()).getTotalOcc();
                    exclusivityItem.setScore(score2);
                    exclusivityRecommendationItemList.add(exclusivityItem);
                }

                exclusivityRecommendationItemList.addAll(itemMap.values());

                return exclusivityRecommendationItemList;
            }


        };
    }

}
