package fr.shift.modeling.backend.data;
/*
 * This file is part of the Modeling Assistant Recommender. Author: Maxime Savary-Leblanc
 * The Modeling Assistant Recommender is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * The Modeling Assistant Recommender is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with The Modeling Assistant Recommender. If not, see <https://www.gnu.org/licenses/>.
 */
import fr.shift.modeling.backend.data.neo4j.entity.attribute.AttributeContextQueryResult;
import fr.shift.modeling.backend.data.neo4j.entity.attribute.AttributeTotalOccurenceQueryResult;
import fr.shift.modeling.backend.data.neo4j.entity.attribute.AttributeOccurrenceQueryResult;
import fr.shift.modeling.backend.data.neo4j.entity.attribute.AttributeSiblingQueryResult;
import fr.shift.modeling.backend.data.neo4j.datasource.Neo4jDatasource;
import fr.shift.modeling.backend.data.redis.datasource.RedisDatasource;
import fr.shift.modeling.backend.data.redis.entity.KeyEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * The AttributeRecommenderRemoteDatasource is responsible for collecting remote data to compute Attribute recommendations.
 * The different methods it holds describe how data is retrieved for each involved individual score.
 */
@Service
public class AttributeRecommenderRemoteDatasource {

    private final Neo4jDatasource neo4jDatasource;
    private final RedisDatasource redisDatasource;

    public AttributeRecommenderRemoteDatasource(Neo4jDatasource neo4jDatasource, RedisDatasource redisDatasource) {
        this.neo4jDatasource = neo4jDatasource;
        this.redisDatasource = redisDatasource;
    }

    /**
     * Get the total number of appearance in the graph of each Attribute. To optimize the code, this data is stored in a Redis cache.
     *
     * @param attributeNameList the list of attribute names for which the total appearance should be retrieved.
     * @return the map of attribute names to their TotalOccurenceQueryResult
     */
    public Mono<Map<String, AttributeTotalOccurenceQueryResult>> getAttributeTotalOccurrence(List<String> attributeNameList) {
        return redisDatasource.getKeyTotalOccurrence(attributeNameList, KeyEntity.Type.ATTRIBUTE)
                .map(new Function<>() {
                    @Override
                    public Map<String, AttributeTotalOccurenceQueryResult> apply(List<KeyEntity> keyEntities) {
                        Map<String, AttributeTotalOccurenceQueryResult> attributeExcluQueryResultMap = new HashMap<>();
                        for (KeyEntity keyEntity : keyEntities) {
                            attributeExcluQueryResultMap.put(keyEntity.getName(), new AttributeTotalOccurenceQueryResult(keyEntity.getName(), keyEntity.getValue()));
                        }
                        return attributeExcluQueryResultMap;
                    }
                });
    }

    /**
     * Get the attributes owned by a specific class, which do not appear in the list of attribute names already contained by
     * the class targeted for recommendations.
     * @param className the class name for which to retrieve the attribute recommendation.
     * @param attributeNameList the list of attributes names already contained by the target class with name className
     * @return the list of identified attributes projections
     */
    public Mono<List<AttributeOccurrenceQueryResult>> getAttributeOccurence(String className, List<String> attributeNameList) {
        return neo4jDatasource.getAttributeOccurence(className, attributeNameList);
    }

    /**
     * Get the attributes which are siblings to attributes in the name list in parameters. This means that if an attribute
     * appears in the same class as an attribute with name in the list, it will be retrieved.
     * @param attributeNameList the name of attributes contained in the target class
     * @param excludeDefaultAttributes the property to ignore basic attributes for sibling computation. This might be the case
     *                                 for attributes very common to a lot of classes, such as id.
     * @return the list of identified attributes projections
     */
    public Mono<List<AttributeSiblingQueryResult>> getAttributeSiblings(List<String> attributeNameList, boolean excludeDefaultAttributes) {
        return neo4jDatasource.getAttributeSiblings(attributeNameList, excludeDefaultAttributes);
    }

    /**
     * Get the attributes which are in class named as parameter surrounded in the same diagram by classes in classNameList parameter.
     * Attributes whose name are in attributeNameList will not be retrieved.
     * @param classname the name of the class targeted for recommendations
     * @param classNameList the name of classes in the project context, e.g. surrounding className in the diagram
     * @param attributeNameList the name of attributes already owned by the targetClass
     * @return the list of identified attributes projections
     */
    public Mono<List<AttributeContextQueryResult>> getAttributeContext(String classname, List<String> classNameList, List<String> attributeNameList) {
        return neo4jDatasource.getAttributeContext(classname, classNameList, attributeNameList);
    }

}
