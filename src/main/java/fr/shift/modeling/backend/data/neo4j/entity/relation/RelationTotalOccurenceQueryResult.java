package fr.shift.modeling.backend.data.neo4j.entity.relation;
/*
 * This file is part of the Modeling Assistant Recommender. Author: Maxime Savary-Leblanc
 * The Modeling Assistant Recommender is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * The Modeling Assistant Recommender is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with The Modeling Assistant Recommender. If not, see <https://www.gnu.org/licenses/>.
 */

public class RelationTotalOccurenceQueryResult {

    private String className;

    private int totalOcc;

    private boolean isEndNode;

    public RelationTotalOccurenceQueryResult(String className, int totalOcc, boolean isEndNode) {
        this.className = className;
        this.totalOcc = totalOcc;
        this.isEndNode = isEndNode;
    }
    public RelationTotalOccurenceQueryResult(String className, int totalOcc) {
        this.className = className;
        this.totalOcc = totalOcc;
        this.isEndNode = false;
    }
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getTotalOcc() {
        return totalOcc;
    }

    public void setTotalOcc(int totalOcc) {
        this.totalOcc = totalOcc;
    }

    public boolean isEndNode() {
        return isEndNode;
    }

    public void setEndNode(boolean endNode) {
        isEndNode = endNode;
    }
}
