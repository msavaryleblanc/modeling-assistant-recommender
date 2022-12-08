package fr.shift.modeling.backend.data.mapper.relation;
/*
 * This file is part of the Modeling Assistant Recommender. Author: Maxime Savary-Leblanc
 * The Modeling Assistant Recommender is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * The Modeling Assistant Recommender is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with The Modeling Assistant Recommender. If not, see <https://www.gnu.org/licenses/>.
 */

import fr.shift.modeling.backend.controller.entity.PartialRecommendationItem;
import fr.shift.modeling.backend.controller.entity.SourcedPartialRecommendationItem;
import fr.shift.modeling.backend.data.neo4j.entity.relation.RelationContextQueryResult;

import java.util.*;
import java.util.function.Function;

/**
 * Is responsible for transforming {@link RelationContextQueryResult} from the remote data source to {@link PartialRecommendationItem}
 * which is the standard object for an individual scores. It holds the logic of computing score s4 (Context).
 */
public class RelationContextMapperProvider {

    public static Function<List<RelationContextQueryResult>, List<PartialRecommendationItem>> getMapper() {
        return new Function<>() {
            @Override
            public List<PartialRecommendationItem> apply(List<RelationContextQueryResult> relationContextQueryResultList) {
                HashMap<String, SourcedPartialRecommendationItem> partialRecommendationItemHashMap = new HashMap<>();
                HashMap<String, Integer> ctxMap = new HashMap<>();


                int maxHighestCtxLevel = -1;
                int maxTotalCtxLevel = -1;


                for (RelationContextQueryResult relationContextQueryResult : relationContextQueryResultList) {

                    String relationType = RelationTypeMapper.getStringType(relationContextQueryResult.getRelationType(), relationContextQueryResult.isEndNode());

                    partialRecommendationItemHashMap.computeIfAbsent(relationContextQueryResult.getClassName(), new Function<String, SourcedPartialRecommendationItem>() {
                        @Override
                        public SourcedPartialRecommendationItem apply(String s) {
                            return new SourcedPartialRecommendationItem(s);
                        }
                    });
                    ctxMap.computeIfAbsent(relationContextQueryResult.getClassName(), new Function<String, Integer>() {
                        @Override
                        public Integer apply(String s) {
                            return 0;
                        }
                    });

                    if (maxHighestCtxLevel == -1) {
                        maxHighestCtxLevel = relationContextQueryResult.getMaxCtx();
                    }

                    SourcedPartialRecommendationItem item = partialRecommendationItemHashMap.get(relationContextQueryResult.getClassName());
                    Integer currentContext = ctxMap.get(relationContextQueryResult.getClassName());
                    item.addType(relationType, 1);

                    currentContext += relationContextQueryResult.getMaxCtx();
                    ctxMap.replace(relationContextQueryResult.getClassName(), currentContext);

                    List<String> sources = new ArrayList<>(relationContextQueryResult.getSources());
                    sources.sort(Comparator.naturalOrder());

                    String key = String.join("+", sources);

                    item.getSourcesMap().computeIfAbsent(key, new Function<String, Integer>() {
                        @Override
                        public Integer apply(String s) {
                            return 0;
                        }
                    });
                    item.getSourcesMap().replace(key, item.getSourcesMap().get(key) + 1);

                }

                for (Map.Entry<String, Integer> entry : ctxMap.entrySet()) {
                    if (entry.getValue() > maxTotalCtxLevel) {
                        maxTotalCtxLevel = entry.getValue();
                    }
                }

                List<PartialRecommendationItem> partialRecommendationItemList = new ArrayList<>();

                for (Map.Entry<String, SourcedPartialRecommendationItem> entry : partialRecommendationItemHashMap.entrySet()) {
                    SourcedPartialRecommendationItem item = entry.getValue();

                    double score = ((ctxMap.get(entry.getKey()) * 1d) / (maxTotalCtxLevel * 1d));

                    item.setScoreName("s4");
                    item.setScore(score);
                    partialRecommendationItemList.add(item);
                }

                return partialRecommendationItemList;
            }
        };
    }

}
