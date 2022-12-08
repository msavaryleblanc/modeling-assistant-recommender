package fr.shift.modeling.backend.controller.entity;

/*
 * This file is part of the Modeling Assistant Recommender. Author: Maxime Savary-Leblanc
 * The Modeling Assistant Recommender is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * The Modeling Assistant Recommender is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with The Modeling Assistant Recommender. If not, see <https://www.gnu.org/licenses/>.
 */
public class AttributeRecommendationResponse {

    AttributeRecommendationHolder recommendations;
    RequestHandlingInfo<AttributeRecommendationRequest> infos;

    public AttributeRecommendationHolder getAttributes() {
        return recommendations;
    }

    public void setRecommendations(AttributeRecommendationHolder attributes) {
        this.recommendations = attributes;
    }

    public RequestHandlingInfo<AttributeRecommendationRequest> getInfos() {
        return infos;
    }

    public void setInfos(RequestHandlingInfo<AttributeRecommendationRequest> infos) {
        this.infos = infos;
    }
}
