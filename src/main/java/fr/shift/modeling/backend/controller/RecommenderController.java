package fr.shift.modeling.backend.controller;
/*
 * This file is part of the Modeling Assistant Recommender. Author: Maxime Savary-Leblanc
 * The Modeling Assistant Recommender is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * The Modeling Assistant Recommender is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with The Modeling Assistant Recommender. If not, see <https://www.gnu.org/licenses/>.
 */

import fr.shift.modeling.backend.controller.entity.RecommendationRequest;
import fr.shift.modeling.backend.controller.entity.RecommendationResponse;
import fr.shift.modeling.backend.controller.entity.RequestHandlingInfo;
import fr.shift.modeling.backend.controller.exception.RecommendationComputationError;
import fr.shift.modeling.backend.domain.AttributeRecommenderProvider;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import reactor.util.retry.Retry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

@RestController
public class RecommenderController {

    private final AttributeRecommenderProvider attributeRecommenderProvider;

    @Value("${build.version}")
    private String buildVersion;

    public RecommenderController(AttributeRecommenderProvider attributeRecommenderProvider) {
        this.attributeRecommenderProvider = attributeRecommenderProvider;
    }

    /**
     * Exposes the API end-point for attributes recommendations.
     */
    @RequestMapping(
            value = "/attributes/",
            method = RequestMethod.POST)
    public DeferredResult<RecommendationResponse> processAttributes(@RequestBody RecommendationRequest request,
                                                                    final HttpServletResponse response) {

        long initTime = new Date().getTime();

        DeferredResult<RecommendationResponse> deferred = new DeferredResult<RecommendationResponse>(90000L);

        if (request.getName() == null || request.getName().isEmpty()) {
            deferred.setErrorResult("The request must contain name property");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return deferred;
        }

        attributeRecommenderProvider.getAttributeRecommendations(request).map(getServerInfoMapper(initTime, request))
                .retryWhen(Retry.fixedDelay(5, Duration.ofMillis(1))
                        .filter(e -> e instanceof Exception)
                        .doBeforeRetry(res -> System.out.println("-------> Error retrieved, try again"))
                        .doAfterRetry(res -> System.out.println("-------> Try again finished")))
                .doOnError(throwable -> System.out.println(throwable.getMessage()))
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        deferred.setErrorResult(new RecommendationComputationError("Server unavailable, please retry"));
                        response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
                    }
                })
                .subscribe(deferred::setResult);

        return deferred;
    }

    /**
     * Exposes the API end-point for attributes recommendations.
     */
    @RequestMapping(
            value = "/relations/",
            method = RequestMethod.POST)
    public DeferredResult<RecommendationResponse> processRelations(@RequestBody RecommendationRequest request, final HttpServletResponse response) {

        long initTime = new Date().getTime();

        DeferredResult<RecommendationResponse> deferred = new DeferredResult<RecommendationResponse>(90000L);

        if (request.getName() == null || request.getName().isEmpty()) {
            deferred.setErrorResult("The request must contain name property");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return deferred;
        }

        attributeRecommenderProvider.getRelationRecommendations(request).map(getServerInfoMapper(initTime, request))
                .retryWhen(Retry.fixedDelay(5, Duration.ofMillis(1))
                        .filter(e -> e instanceof Exception)
                        .doBeforeRetry(res -> System.out.println("-------> Error retrieved, try again"))
                        .doAfterRetry(res -> System.out.println("-------> Try again finished")))
                .doOnError(throwable -> System.out.println(throwable.getMessage()))
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        deferred.setErrorResult(new RecommendationComputationError("Server unavailable, please retry"));
                        response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
                    }
                })
                .subscribe(deferred::setResult);

        return deferred;
    }


    private Function<RecommendationResponse, RecommendationResponse> getServerInfoMapper(long initTime, RecommendationRequest request) {
        return new Function<RecommendationResponse, RecommendationResponse>() {
            @Override
            public RecommendationResponse apply(RecommendationResponse recommendationResponse) {
                RequestHandlingInfo<RecommendationRequest> requestHandlingInfo = new RequestHandlingInfo<>(request, initTime, buildVersion);
                recommendationResponse.setInfos(requestHandlingInfo);
                return recommendationResponse;
            }
        };
    }

}