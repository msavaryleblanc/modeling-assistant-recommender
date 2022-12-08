package fr.shift.modeling.backend.domain;
/*
 * This file is part of the Modeling Assistant Recommender. Author: Maxime Savary-Leblanc
 * The Modeling Assistant Recommender is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * The Modeling Assistant Recommender is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with The Modeling Assistant Recommender. If not, see <https://www.gnu.org/licenses/>.
 */
import fr.shift.modeling.backend.controller.entity.*;
import fr.shift.modeling.backend.data.AttributeRecommenderDataRepository;
import fr.shift.modeling.backend.domain.evaluator.attribute.AttributeContextEvaluator;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.function.Function;

/**
 * AttributeRecommendationAggregator is responsible for collecting {@link PartialRecommendationItem} objects for
 * the individual different scores computed in from {@link AttributeRecommenderDataRepository}, and condensing them into
 * AttributeRecommendationItem objects which are unique based on the attribute name.
 */
@Service
public class AttributeRecommendationAggregator {

    AttributeRecommenderDataRepository attributeRecommenderRepository;

    public AttributeRecommendationAggregator(AttributeRecommenderDataRepository attributeRecommenderRepository) {
        this.attributeRecommenderRepository = attributeRecommenderRepository;
    }

    /**
     * Aggregates the individual score recommendations for attributes
     * @param contextEvaluator the selected context evaluator, which holds the logic of individual score aggregation into the final score
     * @param request the request from the controller end point, wich is emmitted by the client
     * @return AttributeRecommendationHolder which owns the recommendations and their source explanations
     */
    public Mono<AttributeRecommendationHolder> getAttributePartialRecommendations(
            AttributeContextEvaluator contextEvaluator, AttributeRecommendationRequest request
) {

        String className = request.getName();
        List<String> attributeNameList = request.getAttributes();
        List<String> classNameList = request.getClassNames();
        FilterOptions filterOptions = request.getFilterOptions();

        return Flux.concat(
                        attributeRecommenderRepository.getOccurrenceAndExclusivityRankedItems(className, attributeNameList),
                        attributeRecommenderRepository.getSiblingRankedItems(attributeNameList, filterOptions.isExcludeDefaultAttributes()),
                        attributeRecommenderRepository.getContextRankedItems(className, classNameList, attributeNameList))
                .collectList().map(new Function<List<List<PartialRecommendationItem>>, List<PartialRecommendationItem>>() {
                    @Override
                    public List<PartialRecommendationItem> apply(List<List<PartialRecommendationItem>> lists) {
                        List<PartialRecommendationItem> total = new ArrayList<>();
                        lists.forEach(total::addAll);
                        return total;
                    }
                }).map(new Function<List<PartialRecommendationItem>, AttributeRecommendationHolder>() {
                    @Override
                    public AttributeRecommendationHolder apply(List<PartialRecommendationItem> partialRecommendationItems) {
                        AttributeRecommendationHolder attributeRecommendationHolder = new AttributeRecommendationHolder();

                        final HashMap<String, MaxSourceItem> maxSourceItemMap = new HashMap<>();
                        final HashMap<String, AttributeRecommendationItem> recommendationHashmap = new HashMap<>();
                        final List<PartialRecommendationItem> partialRecommendationItemList = new ArrayList<>();


                        for (PartialRecommendationItem partialRecommendationItem : partialRecommendationItems) {

                            //compute which is the best source for each score
                            if (partialRecommendationItem instanceof SourcedPartialRecommendationItem) {
                                SourcedPartialRecommendationItem sourcedPartialRecommendationItem = (SourcedPartialRecommendationItem) partialRecommendationItem;
                                maxSourceItemMap.computeIfAbsent(sourcedPartialRecommendationItem.getScoreName(), new Function<String, MaxSourceItem>() {
                                    @Override
                                    public MaxSourceItem apply(String s) {
                                        return new MaxSourceItem(s);
                                    }
                                });

                                MaxSourceItem maxSourceItem = maxSourceItemMap.get(sourcedPartialRecommendationItem.getScoreName());
                                for (Map.Entry<String, Integer> entry : sourcedPartialRecommendationItem.getSourcesMap().entrySet()) {
                                    if (entry.getValue() > maxSourceItem.getMaxValue()) {
                                        System.out.println("Get best for " + sourcedPartialRecommendationItem.toString());
                                        maxSourceItem.setMaxValue(entry.getValue());
                                        maxSourceItem.setSourceName(entry.getKey());
                                    }
                                }
                            }

                            //Aggregate score into a single item
                            AttributeRecommendationItem attributeRecommendationItem = recommendationHashmap.get(partialRecommendationItem.getName());
                            if (attributeRecommendationItem == null) {
                                attributeRecommendationItem = new AttributeRecommendationItem(partialRecommendationItem.getName());
                                recommendationHashmap.put(partialRecommendationItem.getName(), attributeRecommendationItem);
                            }
                            partialRecommendationItem.setName(null);
                            attributeRecommendationItem.getPartialRecommendationItemList().add(partialRecommendationItem);
                        }

                        attributeRecommendationHolder.setMaxSourceItemList(new ArrayList<>(maxSourceItemMap.values()));


                        List<AttributeRecommendationItem> attributeRecommendationItemList = new ArrayList<>();

                        for (AttributeRecommendationItem attributeRecommendationItem : recommendationHashmap.values()) {
                            evaluateRecommendationItem(contextEvaluator, attributeRecommendationItem, attributeNameList, classNameList);
                            attributeRecommendationItem.aggregateTypes();
                            //TODO: handle threshold
                            if (attributeRecommendationItem.getConfidenceScore() >= filterOptions.getScoreThreshold()) {
                                attributeRecommendationItemList.add(attributeRecommendationItem);
                                //System.out.println(attributeRecommendationItem.toString());
                            }
                        }

                        Collections.sort(attributeRecommendationItemList, new Comparator<AttributeRecommendationItem>() {
                            @Override
                            public int compare(AttributeRecommendationItem o1, AttributeRecommendationItem o2) {
                                return -Double.compare(o1.getConfidenceScore(), o2.getConfidenceScore());
                            }
                        });

                        if (filterOptions.getMaxElements() > -1 && attributeRecommendationItemList.size() > filterOptions.getMaxElements()) {
                            attributeRecommendationItemList = attributeRecommendationItemList.subList(0, filterOptions.getMaxElements());
                        }

                        for (AttributeRecommendationItem attributeRecommendationItem : attributeRecommendationItemList) {
                            System.out.println(attributeRecommendationItem.toString());
                        }
                        attributeRecommendationHolder.setAttributeRecommendationItemList(attributeRecommendationItemList);
                        return attributeRecommendationHolder;
                    }
                });
    }


    private void evaluateRecommendationItem(AttributeContextEvaluator contextEvaluator, AttributeRecommendationItem attributeRecommendationItem, List<String> attributeNameList, List<String> classNameList) {
        attributeRecommendationItem.setConfidenceScore(contextEvaluator.getAttributeScore(attributeRecommendationItem, attributeNameList, classNameList));
    }

}
