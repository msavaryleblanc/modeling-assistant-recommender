package fr.shift.modeling.backend.controller.entity;

/*
 * This file is part of the Modeling Assistant Recommender. Author: Maxime Savary-Leblanc
 * The Modeling Assistant Recommender is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * The Modeling Assistant Recommender is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with The Modeling Assistant Recommender. If not, see <https://www.gnu.org/licenses/>.
 */
import com.fasterxml.jackson.annotation.JsonProperty;

import java.text.DecimalFormat;
import java.util.*;
import java.util.function.Function;

public class RecommendationItem {

    @JsonProperty("scores")
    List<PartialRecommendationItem> partialRecommendationItemList;

    Map<String, Integer> types;

    String name;
    Double confidenceScore = 0d;
    transient DecimalFormat numberFormat = new DecimalFormat("0.00");


    public RecommendationItem(String name) {
        this.name = name;
        this.partialRecommendationItemList = new ArrayList<>();
        this.types = new HashMap<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RecommendationItem that)) return false;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public Double getScore(String scoreName) {
        for (PartialRecommendationItem partialRecommendationItem : partialRecommendationItemList) {
            if (scoreName.equals(partialRecommendationItem.getScoreName())) {
                return partialRecommendationItem.getScore();
            }
        }
        return 0d;
    }

    public void aggregateTypes() {
        int totalValue = 0;
        for (PartialRecommendationItem partialRecommendationItem : partialRecommendationItemList) {
            for (Map.Entry<String, Integer> entry : partialRecommendationItem.getTypes().entrySet()) {
                addType(entry.getKey(), entry.getValue());
                totalValue += entry.getValue();
            }
        }
        int totalAvailable = 100;
        for (Map.Entry<String, Integer> entry : types.entrySet()) {
            int value = entry.getValue();

            int percentage = (int) Math.round((100d * value) / (1d * totalValue));
            if ((totalAvailable - percentage) >= 0) {
                entry.setValue(percentage);
                totalAvailable = totalAvailable - percentage;
            } else {
                entry.setValue(percentage - 1);
                totalAvailable = totalAvailable - percentage + 1;
            }
        }
        types = sortByComparator(types, false);
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

    public String getName() {
        return name;
    }


    public List<PartialRecommendationItem> getPartialRecommendationItemList() {
        return partialRecommendationItemList;
    }

    public Double getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(Double confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    @Override
    public String toString() {
        return "[R] [" + numberFormat.format(confidenceScore) + "] \t" +
                numberFormat.format(getScore("s1")) + "\t\t" +
                numberFormat.format(getScore("s2")) + "\t\t" +
                numberFormat.format(getScore("s3")) + "\t\t" +
                numberFormat.format(getScore("s4")) + "\t\t" +
                name;


    }

    private static Map<String, Integer> sortByComparator(Map<String, Integer> unsortMap, final boolean order) {

        List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(unsortMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2) {
                if (order) {
                    return o1.getValue().compareTo(o2.getValue());
                } else {
                    return o2.getValue().compareTo(o1.getValue());

                }
            }
        });

        // Maintaining insertion order with the help of LinkedList
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }


}
