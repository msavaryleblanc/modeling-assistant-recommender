package fr.shift.modeling.backend.data.neo4j.entity.attribute;
/*
 * This file is part of the Modeling Assistant Recommender. Author: Maxime Savary-Leblanc
 * The Modeling Assistant Recommender is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * The Modeling Assistant Recommender is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with The Modeling Assistant Recommender. If not, see <https://www.gnu.org/licenses/>.
 */

import java.util.List;

public class AttributeContextQueryResult {

    //The name of the attribute
    private String attributeName;

    //The type of the attribute
    private String attributeType;

    //The id of the model it comes from
    private int modelId;

    //Sum of all ctxLevel
    private int maxCtx;
    private List<String> sources;

    public String getAttributeName() {
        return attributeName;
    }

    public int getModelId() {
        return modelId;
    }

    public int getMaxCtx() {
        return maxCtx;
    }

    public String getAttributeType() {
        return attributeType;
    }

    public List<String> getSources() {
        return sources;
    }

    public AttributeContextQueryResult(String attributeName, String attributeType, int modelId, int maxCtx, List<String> sources) {
        this.attributeName = attributeName;
        this.attributeType = attributeType;
        this.modelId = modelId;
        this.maxCtx = maxCtx;
        this.sources = sources;
        System.out.println("Sources for " + attributeName + " are " + sources);
    }

    public void setSources(List<String> sources) {
        this.sources = sources;
    }

    @Override
    public String toString() {
        return "AttributeContextQueryResult{" +
                "attributeName='" + attributeName + '\'' +
                ", modelId=" + modelId +
                ", maxCtx=" + maxCtx +
                ", sources=" + sources +
                '}';
    }
}
