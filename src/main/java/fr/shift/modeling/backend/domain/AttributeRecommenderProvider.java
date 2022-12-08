package fr.shift.modeling.backend.domain;
/*
 * This file is part of the Modeling Assistant Recommender. Author: Maxime Savary-Leblanc
 * The Modeling Assistant Recommender is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * The Modeling Assistant Recommender is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with The Modeling Assistant Recommender. If not, see <https://www.gnu.org/licenses/>.
 */
import fr.shift.modeling.backend.controller.entity.AttributeRecommendationRequest;
import fr.shift.modeling.backend.controller.entity.AttributeRecommendationResponse;
import fr.shift.modeling.backend.controller.entity.AttributeRecommendationHolder;
import fr.shift.modeling.backend.domain.evaluator.attribute.AttributeContextEvaluator;
import fr.shift.modeling.backend.domain.evaluator.attribute.SupervisedLearningAttributeContextEvaluator;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.function.Function;

/**
 * The AttributeRecommenderProvider has the role to provide the controller with the data computed from the various
 * datasources of the application. It maps the entities of the domain layer to entities for the controller layer.
 */
@Service
public class AttributeRecommenderProvider {

    private AttributeRecommendationAggregator attributeRecommendationAggregator;

    private AttributeContextEvaluator contextEvaluator;
    public AttributeRecommenderProvider(AttributeRecommendationAggregator attributeRecommendationAggregator) {
        this.attributeRecommendationAggregator = attributeRecommendationAggregator;
        this.contextEvaluator = new SupervisedLearningAttributeContextEvaluator();
    }

    /**
     * Exposes the attribute recommendation end-point to the controller layer of the application.
     * @param request the request emitted by the client, caught by the related controller
     * @return a asynchronous mono wrapper around the response to be delivered by the controller
     */
    public Mono<AttributeRecommendationResponse> getAttributePartialRecommendations(AttributeRecommendationRequest request) {

        //For now, we use only one evaluator, so we create it only once in constructor.

        return attributeRecommendationAggregator.getAttributePartialRecommendations(contextEvaluator, request).map(new Function<AttributeRecommendationHolder, AttributeRecommendationResponse>() {
            @Override
            public AttributeRecommendationResponse apply(AttributeRecommendationHolder attributeRecommendationHolder) {
                AttributeRecommendationResponse response = new AttributeRecommendationResponse();
                response.setRecommendations(attributeRecommendationHolder);
                return response;
            }
        });

    }
}
