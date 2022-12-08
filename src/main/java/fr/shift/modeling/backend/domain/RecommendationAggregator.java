package fr.shift.modeling.backend.domain;
/*
 * This file is part of the Modeling Assistant Recommender. Author: Maxime Savary-Leblanc
 * The Modeling Assistant Recommender is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * The Modeling Assistant Recommender is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with The Modeling Assistant Recommender. If not, see <https://www.gnu.org/licenses/>.
 */
import fr.shift.modeling.backend.controller.entity.*;
import fr.shift.modeling.backend.data.AttributeRecommenderDataRepository;
import fr.shift.modeling.backend.data.RelationRecommenderDataRepository;
import fr.shift.modeling.backend.domain.evaluator.ContextEvaluator;
import fr.shift.modeling.backend.domain.evaluator.attribute.AttributeContextEvaluator;
import fr.shift.modeling.backend.domain.evaluator.relation.RelationContextEvaluator;
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
public class RecommendationAggregator {

    AttributeRecommenderDataRepository attributeRecommenderRepository;
    RelationRecommenderDataRepository relationRecommenderDataRepository;

    public RecommendationAggregator(AttributeRecommenderDataRepository attributeRecommenderRepository,
                                    RelationRecommenderDataRepository relationRecommenderDataRepository) {
        this.attributeRecommenderRepository = attributeRecommenderRepository;
        this.relationRecommenderDataRepository = relationRecommenderDataRepository;
    }

    /**
     * Aggregates the individual score recommendations for attributes
     * @param contextEvaluator the selected context evaluator, which holds the logic of individual score aggregation into the final score
     * @param request the request from the controller end point, wich is emmitted by the client
     * @return AttributeRecommendationHolder which owns the recommendations and their source explanations
     */
    public Mono<RecommendationHolder> getAttributePartialRecommendations(AttributeContextEvaluator contextEvaluator, RecommendationRequest request) {

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
                }).map(getPartialItemsMappingFunction(contextEvaluator, request));
    }

    public Mono<RecommendationHolder> getRelationPartialRecommendations(RelationContextEvaluator contextEvaluator, RecommendationRequest request) {

        String className = request.getName();
        List<String> classNameList = request.getClassNames();
        List<String> linkedClassNames = request.getLinkedClasses();

        return Flux.concat(
                        relationRecommenderDataRepository.getOccurrenceAndExclusivityRankedItems(className,linkedClassNames),
                        relationRecommenderDataRepository.getSiblingRankedItems(className, linkedClassNames),
                        relationRecommenderDataRepository.getContextRankedItems(className, classNameList,linkedClassNames))
                .collectList().map(new Function<List<List<PartialRecommendationItem>>, List<PartialRecommendationItem>>() {
                    @Override
                    public List<PartialRecommendationItem> apply(List<List<PartialRecommendationItem>> lists) {
                        List<PartialRecommendationItem> total = new ArrayList<>();
                        lists.forEach(total::addAll);
                        return total;
                    }
                }).map(getPartialItemsMappingFunction(contextEvaluator, request));
    }

    private Function<List<PartialRecommendationItem>, RecommendationHolder> getPartialItemsMappingFunction(ContextEvaluator contextEvaluator, RecommendationRequest request){

        List<String> attributeNameList = request.getAttributes();
        List<String> classNameList = request.getClassNames();
        FilterOptions filterOptions = request.getFilterOptions();

        return new Function<List<PartialRecommendationItem>, RecommendationHolder>() {
            @Override
            public RecommendationHolder apply(List<PartialRecommendationItem> partialRecommendationItems) {
                RecommendationHolder recommendationHolder = new RecommendationHolder();

                final HashMap<String, MaxSourceItem> maxSourceItemMap = new HashMap<>();
                final HashMap<String, RecommendationItem> recommendationHashmap = new HashMap<>();
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
                    RecommendationItem recommendationItem = recommendationHashmap.get(partialRecommendationItem.getName());
                    if (recommendationItem == null) {
                        recommendationItem = new RecommendationItem(partialRecommendationItem.getName());
                        recommendationHashmap.put(partialRecommendationItem.getName(), recommendationItem);
                    }
                    partialRecommendationItem.setName(null);
                    recommendationItem.getPartialRecommendationItemList().add(partialRecommendationItem);
                }

                recommendationHolder.setMaxSourceItemList(new ArrayList<>(maxSourceItemMap.values()));


                List<RecommendationItem> recommendationItemList = new ArrayList<>();

                for (RecommendationItem recommendationItem : recommendationHashmap.values()) {
                    evaluateRecommendationItem(contextEvaluator, recommendationItem, attributeNameList, classNameList);
                    recommendationItem.aggregateTypes();

                    if (recommendationItem.getConfidenceScore() >= filterOptions.getScoreThreshold()) {
                        recommendationItemList.add(recommendationItem);
                        //System.out.println(attributeRecommendationItem.toString());
                    }
                }

                Collections.sort(recommendationItemList, new Comparator<RecommendationItem>() {
                    @Override
                    public int compare(RecommendationItem o1, RecommendationItem o2) {
                        return -Double.compare(o1.getConfidenceScore(), o2.getConfidenceScore());
                    }
                });

                if (filterOptions.getMaxElements() > -1 && recommendationItemList.size() > filterOptions.getMaxElements()) {
                    recommendationItemList = recommendationItemList.subList(0, filterOptions.getMaxElements());
                }

                for (RecommendationItem recommendationItem : recommendationItemList) {
                    System.out.println(recommendationItem.toString());
                }
                recommendationHolder.setItems(recommendationItemList);
                return recommendationHolder;
            }
        };
    }

    private void evaluateRecommendationItem(ContextEvaluator contextEvaluator, RecommendationItem recommendationItem, List<String> attributeNameList, List<String> classNameList) {
        recommendationItem.setConfidenceScore(contextEvaluator.getConfidenceScore(recommendationItem, attributeNameList, classNameList));
    }

}
