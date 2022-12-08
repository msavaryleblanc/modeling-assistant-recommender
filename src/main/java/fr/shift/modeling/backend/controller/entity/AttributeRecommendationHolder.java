package fr.shift.modeling.backend.controller.entity;
/*
 * This file is part of the Modeling Assistant Recommender. Author: Maxime Savary-Leblanc
 * The Modeling Assistant Recommender is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * The Modeling Assistant Recommender is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with The Modeling Assistant Recommender. If not, see <https://www.gnu.org/licenses/>.
 */
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class AttributeRecommendationHolder {
    @JsonProperty("attributes")
    List<AttributeRecommendationItem> attributeRecommendationItemList;

    @JsonProperty("sourcesMax")
    List<MaxSourceItem> maxSourceItemList;

    public List<AttributeRecommendationItem> getAttributeRecommendationItemList() {
        return attributeRecommendationItemList;
    }

    public void setAttributeRecommendationItemList(List<AttributeRecommendationItem> attributeRecommendationItemList) {
        this.attributeRecommendationItemList = attributeRecommendationItemList;
    }

    public List<MaxSourceItem> getMaxSourceItemList() {
        return maxSourceItemList;
    }

    public void setMaxSourceItemList(List<MaxSourceItem> maxSourceItemList) {
        this.maxSourceItemList = maxSourceItemList;
    }

}
