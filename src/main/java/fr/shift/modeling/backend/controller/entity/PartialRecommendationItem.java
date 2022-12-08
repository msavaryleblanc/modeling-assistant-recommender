package fr.shift.modeling.backend.controller.entity;
/*
 * This file is part of the Modeling Assistant Recommender. Author: Maxime Savary-Leblanc
 * The Modeling Assistant Recommender is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * The Modeling Assistant Recommender is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with The Modeling Assistant Recommender. If not, see <https://www.gnu.org/licenses/>.
 */
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PartialRecommendationItem {

    String name;
    String scoreName;
    Map<String, Integer> types;
    double score;

    public PartialRecommendationItem(String name, String scoreName, double score) {
        this.name = name;
        this.scoreName = scoreName;
        this.score = score;
        this.types = new HashMap<>();
    }

    public PartialRecommendationItem(String name) {
        this.name = name;
        this.types = new HashMap<>();
    }

    public void setScoreName(String scoreName) {
        this.scoreName = scoreName;
    }

    public void setScore(double score) {
        this.score = applyFunction(score);
    }

    public String getName() {
        return name;
    }

    public String getScoreName() {
        return scoreName;
    }

    public double getScore() {
        return score;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addType(String typeName, int typeCounter) {
        if (typeName != null) {
            types.computeIfAbsent(typeName, new Function<String, Integer>() {
                @Override
                public Integer apply(String s) {
                    return 0;
                }
            });
            types.replace(typeName, types.get(typeName) + typeCounter);
        }
    }

    public Map<String, Integer> getTypes() {
        return types;
    }

    public int compareTo(PartialRecommendationItem partialRecommendationItem) {
        if (this.equals(partialRecommendationItem)) {
            return 0;
        } else {
            if ((partialRecommendationItem == null) || this.score > partialRecommendationItem.getScore()) {
                return 1;
            }
            return -1;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PartialRecommendationItem)) return false;
        PartialRecommendationItem that = (PartialRecommendationItem) o;
        return Double.compare(that.score, score) == 0 &&
                Objects.equals(name, that.name) &&
                Objects.equals(scoreName, that.scoreName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, scoreName, score);
    }

    private Double applyFunction(Double x) {
        double value = x;
        return x;
        /*if (x < 0.2d) {
            value = (1d - Math.exp(-27d * x)) / 5d;
        }
        return (-1d * (value * value) + (2d * value));*/
    }

    @Override
    public String toString() {
        return "PartialRecommendationItem{" +
                "name='" + name + '\'' +
                ", scoreName='" + scoreName + '\'' +
                ", types=" + types +
                ", score=" + score +
                '}';
    }
}
