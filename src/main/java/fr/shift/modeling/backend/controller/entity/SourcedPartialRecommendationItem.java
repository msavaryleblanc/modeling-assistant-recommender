package fr.shift.modeling.backend.controller.entity;
/*
 * This file is part of the Modeling Assistant Recommender. Author: Maxime Savary-Leblanc
 * The Modeling Assistant Recommender is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * The Modeling Assistant Recommender is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with The Modeling Assistant Recommender. If not, see <https://www.gnu.org/licenses/>.
 */
import java.util.HashMap;
import java.util.Map;

public class SourcedPartialRecommendationItem extends PartialRecommendationItem {

    private final Map<String, Integer> sourcesMap = new HashMap<>();

    public SourcedPartialRecommendationItem(String attributeName, String scoreName, double score) {
        super(attributeName, scoreName, score);
    }

    public SourcedPartialRecommendationItem(String attributeName) {
        super(attributeName);
    }

    public Map<String, Integer> getSourcesMap() {
        return sourcesMap;
    }

    @Override
    public String toString() {
        return "SourcedPartialRecommendationItem{" +
                "sourcesMap=" + sourcesMap +
                ", name='" + name + '\'' +
                ", scoreName='" + scoreName + '\'' +
                ", score=" + score +
                '}';
    }
}
