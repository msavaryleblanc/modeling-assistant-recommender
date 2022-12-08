package fr.shift.modeling.backend.data.neo4j.entity.relation;
/*
 * This file is part of the Modeling Assistant Recommender. Author: Maxime Savary-Leblanc
 * The Modeling Assistant Recommender is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * The Modeling Assistant Recommender is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with The Modeling Assistant Recommender. If not, see <https://www.gnu.org/licenses/>.
 */

import java.util.List;

public class RelationContextQueryResult {


    private String className;
    private String relationType;
    private boolean isEndNode;
    private int maxCtx;
    private int modelId;
    private List<String> sources;

    public RelationContextQueryResult(String className, String relationType, boolean isEndNode, int maxCtx, int modelId, List<String> sources) {
        this.className = className;
        this.relationType = relationType;
        this.isEndNode = isEndNode;
        this.maxCtx = maxCtx;
        this.modelId = modelId;
        this.sources = sources;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getRelationType() {
        return relationType;
    }

    public void setRelationType(String relationType) {
        this.relationType = relationType;
    }

    public boolean isEndNode() {
        return isEndNode;
    }

    public void setEndNode(boolean endNode) {
        isEndNode = endNode;
    }

    public int getMaxCtx() {
        return maxCtx;
    }

    public void setMaxCtx(int maxCtx) {
        this.maxCtx = maxCtx;
    }

    public int getModelId() {
        return modelId;
    }

    public void setModelId(int modelId) {
        this.modelId = modelId;
    }

    public List<String> getSources() {
        return sources;
    }

    public void setSources(List<String> sources) {
        this.sources = sources;
    }
}
