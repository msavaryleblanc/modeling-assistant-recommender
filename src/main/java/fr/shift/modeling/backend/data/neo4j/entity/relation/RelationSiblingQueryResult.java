package fr.shift.modeling.backend.data.neo4j.entity.relation;
/*
 * This file is part of the Modeling Assistant Recommender. Author: Maxime Savary-Leblanc
 * The Modeling Assistant Recommender is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * The Modeling Assistant Recommender is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with The Modeling Assistant Recommender. If not, see <https://www.gnu.org/licenses/>.
 */

import java.util.List;

public class RelationSiblingQueryResult {

    private String sourceName;
    private String type;
    private String targetName;
    private boolean isEndNode;
    private List<Integer> linkClassIds;

    public RelationSiblingQueryResult(String sourceName, String type, String targetName, boolean isEndNode, List<Integer> linkClassIds) {
        this.sourceName = sourceName;
        this.type = type;
        this.targetName = targetName;
        this.isEndNode = isEndNode;
        this.linkClassIds = linkClassIds;
    }

    public RelationSiblingQueryResult() {
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public boolean isEndNode() {
        return isEndNode;
    }

    public void setEndNode(boolean endNode) {
        isEndNode = endNode;
    }

    public List<Integer> getLinkClassIds() {
        return linkClassIds;
    }

    public void setLinkClassIds(List<Integer> linkClassIds) {
        this.linkClassIds = linkClassIds;
    }

    @Override
    public String toString() {
        return "ClassSiblingQueryResult{" +
                "sourceName='" + sourceName + '\'' +
                ", type='" + type + '\'' +
                ", targetName='" + targetName + '\'' +
                ", isEndNode=" + isEndNode +
                ", linkClassIds=" + linkClassIds +
                '}';
    }
}
