package fr.shift.modeling.backend.data.neo4j.entity;
/*
 * This file is part of the Modeling Assistant Recommender. Author: Maxime Savary-Leblanc
 * The Modeling Assistant Recommender is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * The Modeling Assistant Recommender is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with The Modeling Assistant Recommender. If not, see <https://www.gnu.org/licenses/>.
 */

import java.util.List;

public class AttributeSiblingQueryResult {

    //The name of the attributes
    private String attributeName;

    private String attributeType;

    //The id of the containing class, useful to get
    //the number of distinct classes
    private List<Integer> classIdList;

    //The occurence number of this attributes with regard
    //to the list of attributes in parameter
    private int occNumber;

    private String siblingName;

    public String getAttributeName() {
        return attributeName;
    }



    public int getOccNumber() {
        return occNumber;
    }

    public String getSiblingName() {
        return siblingName;
    }

    public String getAttributeType() {
        return attributeType;
    }

    public List<Integer> getClassIdList() {
        return classIdList;
    }

    public AttributeSiblingQueryResult(String attributeName, String attributeType, List<Integer> classIdList, int occNumber, String siblingName) {
        this.attributeName = attributeName;
        this.attributeType = attributeType;
        this.classIdList = classIdList;
        this.occNumber = occNumber;
        this.siblingName = siblingName;
    }

    @Override
    public String toString() {
        return "AttributeSiblingQueryResult{" +
                "attributeName='" + attributeName + '\'' +
                ", attributeType='" + attributeType + '\'' +
                ", classIdList=" + classIdList +
                ", occNumber=" + occNumber +
                ", siblingName='" + siblingName + '\'' +
                '}';
    }
}
