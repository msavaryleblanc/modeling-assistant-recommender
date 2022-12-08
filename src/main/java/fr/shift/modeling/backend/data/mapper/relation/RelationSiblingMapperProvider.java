package fr.shift.modeling.backend.data.mapper.relation;
/*
 * This file is part of the Modeling Assistant Recommender. Author: Maxime Savary-Leblanc
 * The Modeling Assistant Recommender is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * The Modeling Assistant Recommender is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with The Modeling Assistant Recommender. If not, see <https://www.gnu.org/licenses/>.
 */

import fr.shift.modeling.backend.controller.entity.PartialRecommendationItem;
import fr.shift.modeling.backend.controller.entity.SourcedPartialRecommendationItem;
import fr.shift.modeling.backend.data.neo4j.entity.relation.RelationSiblingQueryResult;

import java.util.*;
import java.util.function.Function;

/**
 * Is responsible for transforming {@link RelationSiblingMapperProvider} from the remote data source to
 * {@link PartialRecommendationItem} which is the standard object for an individual scores.
 * It holds the logic of computing score s3 (Sibling synergy).
 */
public class RelationSiblingMapperProvider {

    public static Function<List<RelationSiblingQueryResult>, List<PartialRecommendationItem>> getMapper(List<String> linkedClassNames) {
        return new Function<>() {
            @Override
            public List<PartialRecommendationItem> apply(List<RelationSiblingQueryResult> relationSiblingQueryResultList) {
                Map<String, Integer> occurenceMap = new HashMap<>();
                Map<String, SourcedPartialRecommendationItem> partialRecommendationItemMap = new HashMap<>();
                Set<Integer> distinctClassSet = new HashSet<>();


                for (RelationSiblingQueryResult relationSiblingQueryResult : relationSiblingQueryResultList) {
                    System.out.println(relationSiblingQueryResult);

                    String relationType = RelationTypeMapper.getStringType(relationSiblingQueryResult.getType(), relationSiblingQueryResult.isEndNode());


                    Integer currentCounter = occurenceMap.get(relationSiblingQueryResult.getTargetName());
                        SourcedPartialRecommendationItem partialItem = partialRecommendationItemMap.get(relationSiblingQueryResult.getTargetName());
                        if (currentCounter == null) {
                            occurenceMap.put(relationSiblingQueryResult.getTargetName(), 0);
                            currentCounter = 0;
                        }
                        if (partialItem == null) {
                            partialItem = new SourcedPartialRecommendationItem(relationSiblingQueryResult.getTargetName());
                            partialRecommendationItemMap.put(relationSiblingQueryResult.getTargetName(), partialItem);
                        }

                        currentCounter += relationSiblingQueryResult.getLinkClassIds().size();
                        occurenceMap.replace(relationSiblingQueryResult.getTargetName(), currentCounter);
                        distinctClassSet.addAll(relationSiblingQueryResult.getLinkClassIds());

                        partialItem.addType(relationType, 1);

                        Integer sourceCounter = partialItem.getSourcesMap().get(relationSiblingQueryResult.getSourceName());
                        if (sourceCounter == null) {
                            partialItem.getSourcesMap().put(relationSiblingQueryResult.getSourceName(), 0);
                            sourceCounter = 0;
                        }
                        sourceCounter += relationSiblingQueryResult.getLinkClassIds().size();
                        partialItem.getSourcesMap().replace(relationSiblingQueryResult.getSourceName(), sourceCounter);


                }
                System.out.println(occurenceMap);
                List<PartialRecommendationItem> partialRecommendationItemList = new ArrayList<>();
                for (Map.Entry<String, Integer> entry : occurenceMap.entrySet()) {
                    SourcedPartialRecommendationItem item = partialRecommendationItemMap.get(entry.getKey());
                    double score = (entry.getValue() * 1d) / (distinctClassSet.size() * 1d * linkedClassNames.size());
                    item.setScore(score);
                    item.setScoreName("s3");
                    partialRecommendationItemList.add(item);
                    //System.out.println(item.toString());
                }
                return partialRecommendationItemList;
            }
        };
    }

}
