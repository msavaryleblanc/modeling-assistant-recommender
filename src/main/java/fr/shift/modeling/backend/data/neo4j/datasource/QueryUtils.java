package fr.shift.modeling.backend.data.neo4j.datasource;
/*
 * This file is part of the Modeling Assistant Recommender. Author: Maxime Savary-Leblanc
 * The Modeling Assistant Recommender is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * The Modeling Assistant Recommender is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with The Modeling Assistant Recommender. If not, see <https://www.gnu.org/licenses/>.
 */
import java.util.List;

public class QueryUtils {

    /**
     * Formats a list of String to be included in the Neo4j cypher queries of {@link Neo4jDatasource}.
     * @param inputList a list of string to be formatted as list for Neo4j Cypher.
     * @return the formatted String list
     */
    public static String formatList(List<String> inputList){
        StringBuilder builder = new StringBuilder("[");
        for(int i = 0; i<inputList.size()-1;i++){
            builder.append("\"");
            builder.append(inputList.get(i));
            builder.append("\",");
        }
        if(inputList.size() > 0){
            builder.append("\"");
            builder.append(inputList.get(inputList.size()-1));
            builder.append("\"");
        }
        builder.append("]");
        return builder.toString();
    }
}
