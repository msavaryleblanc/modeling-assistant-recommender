package fr.shift.modeling.backend.controller.entity;

/*
 * This file is part of the Modeling Assistant Recommender. Author: Maxime Savary-Leblanc
 * The Modeling Assistant Recommender is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * The Modeling Assistant Recommender is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with The Modeling Assistant Recommender. If not, see <https://www.gnu.org/licenses/>.
 */
import java.util.ArrayList;
import java.util.List;

public class RecommendationRequest {

    String name;
    List<String> attributes;
    List<String> classNames = new ArrayList<>();
    List<String> linkedClasses = new ArrayList<>();
    Identity identity;
    FilterOptions filterOptions = new FilterOptions();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getAttributes() {
        return attributes == null ? new ArrayList<>() : attributes;
    }

    public List<String> getClassNames() {
        return classNames;
    }

    public List<String> getLinkedClasses() {
        return linkedClasses == null ? new ArrayList<>() : linkedClasses;
    }

    public Identity getIdentity() {
        return identity;
    }

    public FilterOptions getFilterOptions() {
        return filterOptions;
    }

    @Override
    public String toString() {
        return "AttributeRecommendationRequest{" +
                "name='" + name + '\'' +
                ", attributes=" + attributes +
                ", classNames=" + classNames +
                ", linkedClasses=" + linkedClasses +
                ", identity=" + identity +
                ", filterOptions=" + filterOptions +
                '}';
    }
}
