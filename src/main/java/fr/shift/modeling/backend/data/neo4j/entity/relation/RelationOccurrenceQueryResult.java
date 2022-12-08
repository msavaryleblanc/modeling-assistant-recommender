package fr.shift.modeling.backend.data.neo4j.entity.relation;
/*
 * This file is part of the Modeling Assistant Recommender. Author: Maxime Savary-Leblanc
 * The Modeling Assistant Recommender is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * The Modeling Assistant Recommender is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with The Modeling Assistant Recommender. If not, see <https://www.gnu.org/licenses/>.
 */

public class RelationOccurrenceQueryResult {

    private String className;
    private String relationType;
    private int occNumber;
    private boolean isEndNode;

    public RelationOccurrenceQueryResult(String className, String relationType, int occNumber, boolean isEndNode) {
        this.className = className;
        this.relationType = relationType;
        this.occNumber = occNumber;
        this.isEndNode = isEndNode;
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

    public int getOccNumber() {
        return occNumber;
    }

    public void setOccNumber(int occNumber) {
        this.occNumber = occNumber;
    }

    public boolean isEndNode() {
        return isEndNode;
    }

    public void setEndNode(boolean endNode) {
        isEndNode = endNode;
    }
}
