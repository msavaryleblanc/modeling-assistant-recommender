package fr.shift.modeling.backend.data.mapper.attribute;
/*
 * This file is part of the Modeling Assistant Recommender. Author: Maxime Savary-Leblanc
 * The Modeling Assistant Recommender is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * The Modeling Assistant Recommender is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with The Modeling Assistant Recommender. If not, see <https://www.gnu.org/licenses/>.
 */
import fr.shift.modeling.backend.data.neo4j.entity.attribute.AttributeSiblingQueryResult;
import fr.shift.modeling.backend.controller.entity.PartialRecommendationItem;
import fr.shift.modeling.backend.controller.entity.SourcedPartialRecommendationItem;

import java.util.*;
import java.util.function.Function;

/**
 * Is responsible for transforming {@link AttributeSiblingQueryResult} from the remote data source to
 * {@link PartialRecommendationItem} which is the standard object for an individual scores.
 * It holds the logic of computing score s3 (Sibling synergy).
 */
public class AttributeSiblingMapperProvider {

    public static Function<List<AttributeSiblingQueryResult>, List<PartialRecommendationItem>> getMapper(List<String> attributeNameList) {
        return new Function<>() {
            @Override
            public List<PartialRecommendationItem> apply(List<AttributeSiblingQueryResult> attributeSiblingQueryResultList) {
                Map<String, Integer> occurenceMap = new HashMap<>();
                Map<String, SourcedPartialRecommendationItem> partialRecommendationItemMap = new HashMap<>();
                Set<Integer> distinctClassSet = new HashSet<>();

                for (AttributeSiblingQueryResult attributeSiblingQueryResult : attributeSiblingQueryResultList) {
                    System.out.println(attributeSiblingQueryResult);
                    if (!attributeNameList.contains(attributeSiblingQueryResult.getAttributeName())) {
                        Integer currentCounter = occurenceMap.get(attributeSiblingQueryResult.getAttributeName());
                        SourcedPartialRecommendationItem partialItem = partialRecommendationItemMap.get(attributeSiblingQueryResult.getAttributeName());
                        if (currentCounter == null) {
                            occurenceMap.put(attributeSiblingQueryResult.getAttributeName(), 0);
                            currentCounter = 0;
                        }
                        if (partialItem == null) {
                            partialItem = new SourcedPartialRecommendationItem(attributeSiblingQueryResult.getAttributeName());
                            partialRecommendationItemMap.put(attributeSiblingQueryResult.getAttributeName(), partialItem);
                        }

                        currentCounter += attributeSiblingQueryResult.getOccNumber();
                        occurenceMap.replace(attributeSiblingQueryResult.getAttributeName(), currentCounter);
                        distinctClassSet.addAll(attributeSiblingQueryResult.getClassIdList());
                        partialItem.addType(attributeSiblingQueryResult.getAttributeType(), 1);
                        Integer sourceCounter = partialItem.getSourcesMap().get(attributeSiblingQueryResult.getSiblingName());
                        if (sourceCounter == null) {
                            partialItem.getSourcesMap().put(attributeSiblingQueryResult.getSiblingName(), 0);
                            sourceCounter = 0;
                        }
                        sourceCounter += attributeSiblingQueryResult.getOccNumber();
                        partialItem.getSourcesMap().replace(attributeSiblingQueryResult.getSiblingName(), sourceCounter);

                    }
                }

                List<PartialRecommendationItem> partialRecommendationItemList = new ArrayList<>();
                for (Map.Entry<String, Integer> entry : occurenceMap.entrySet()) {
                    SourcedPartialRecommendationItem item = partialRecommendationItemMap.get(entry.getKey());
                    double score = (entry.getValue() * 1d) / (distinctClassSet.size() * 1d * attributeNameList.size());
                    item.setScore(score);
                    item.setScoreName("s3");
                    partialRecommendationItemList.add(item);
                }
                return partialRecommendationItemList;
            }
        };
    }

}
