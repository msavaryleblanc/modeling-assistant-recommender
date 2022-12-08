package fr.shift.modeling.backend.controller;
/*
 * This file is part of the Modeling Assistant Recommender. Author: Maxime Savary-Leblanc
 * The Modeling Assistant Recommender is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * The Modeling Assistant Recommender is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with The Modeling Assistant Recommender. If not, see <https://www.gnu.org/licenses/>.
 */
import fr.shift.modeling.backend.controller.entity.AttributeRecommendationRequest;
import fr.shift.modeling.backend.controller.entity.AttributeRecommendationResponse;
import fr.shift.modeling.backend.controller.entity.RequestHandlingInfo;
import fr.shift.modeling.backend.domain.AttributeRecommenderProvider;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import reactor.util.retry.Retry;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.*;
import java.util.function.Function;

@RestController
public class RecommenderController {

    private AttributeRecommenderProvider attributeRecommenderProvider;

    @Value("${application.name}")
    private String applicationName;

    @Value("${build.version}")
    private String buildVersion;

    @Value("${build.timestamp}")
    private String buildTimestamp;

    public RecommenderController(AttributeRecommenderProvider attributeRecommenderProvider) {
        this.attributeRecommenderProvider = attributeRecommenderProvider;
    }

    /**
     * Exposes the API end-point for attributes recommendations.
     */
    @RequestMapping(
            value = "/attributes/",
            method = RequestMethod.POST)
    public DeferredResult<AttributeRecommendationResponse> process(@RequestBody AttributeRecommendationRequest request, HttpServletRequest requestServlet) {

        long initTime = new Date().getTime();

        DeferredResult<AttributeRecommendationResponse> deferred = new DeferredResult<AttributeRecommendationResponse>(90000L);

        System.out.println(request.toString());
        System.out.println(applicationName + " // " + buildVersion + " / " + buildTimestamp);

        Enumeration<String> headerNames = requestServlet.getHeaderNames();

        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                System.out.println("Header: " + requestServlet.getHeader(headerNames.nextElement()));
            }
        }

        attributeRecommenderProvider.getAttributePartialRecommendations(request).map(new Function<AttributeRecommendationResponse, AttributeRecommendationResponse>() {
                    @Override
                    public AttributeRecommendationResponse apply(AttributeRecommendationResponse attributeRecommendationResponse) {
                        RequestHandlingInfo<AttributeRecommendationRequest> requestHandlingInfo = new RequestHandlingInfo<>(request, initTime, buildVersion);
                        attributeRecommendationResponse.setInfos(requestHandlingInfo);
                        return attributeRecommendationResponse;
                    }
                }).retryWhen(Retry.fixedDelay(5, Duration.ofMillis(1))
                        .filter(e -> e instanceof Exception)
                        .doBeforeRetry(res -> System.out.println("-------> Error retrieved, try again"))
                        .doAfterRetry(res -> System.out.println("-------> Try again finished")))
                .subscribe(deferred::setResult);

        return deferred;
    }


}