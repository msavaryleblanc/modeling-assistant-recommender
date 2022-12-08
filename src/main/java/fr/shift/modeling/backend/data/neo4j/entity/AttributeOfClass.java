package fr.shift.modeling.backend.data.neo4j.entity;
/*
 * This file is part of the Modeling Assistant Recommender. Author: Maxime Savary-Leblanc
 * The Modeling Assistant Recommender is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * The Modeling Assistant Recommender is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with The Modeling Assistant Recommender. If not, see <https://www.gnu.org/licenses/>.
 */

public class AttributeOfClass {

    //The name of the attributes
    private String className;
    private String attributeName;
    private int counter;

    public String getClassName() {
        return className;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public int getCounter() {
        return counter;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    @Override
    public String toString() {
        return "AttributeOfClass{" +
                "className='" + className + '\'' +
                ", attributeName='" + attributeName + '\'' +
                ", counter=" + counter +
                '}';
    }
}
