package fr.shift.modeling.backend.data.neo4j.datasource;
/*
 * This file is part of the Modeling Assistant Recommender. Author: Maxime Savary-Leblanc
 * The Modeling Assistant Recommender is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * The Modeling Assistant Recommender is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with The Modeling Assistant Recommender. If not, see <https://www.gnu.org/licenses/>.
 */
import fr.shift.modeling.backend.data.neo4j.entity.AttributeContextQueryResult;
import fr.shift.modeling.backend.data.neo4j.entity.AttributeOccurrenceQueryResult;
import fr.shift.modeling.backend.data.neo4j.entity.AttributeSiblingQueryResult;
import fr.shift.modeling.backend.data.redis.RedisConnector;
import org.neo4j.driver.Value;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

/**
 * The datasource for the Neo4j instance, which acts like DAO. It performs the adapted query on the
 * {@link Neo4jClient} to retrieve the data for the needs of the RemoteDataRepository objects.
 */
@Repository
public class Neo4jDatasource {

    Neo4jClient neo4jClient;

    public Neo4jDatasource(Neo4jClient neo4jClient) {
        this.neo4jClient = neo4jClient;
    }

    /**
     * Get the attributes owned by a specific class, which do not appear in the list of attribute names already contained by
     * the class targeted for recommendations.
     * @param className the class name for which to retrieve the attribute recommendation.
     * @param attributeNameList the list of attributes names already contained by the target class with name className
     * @return the list of identified attributes projections
     */
    public Mono<List<AttributeOccurrenceQueryResult>> getAttributeOccurence(String className, List<String> attributeNameList){
        return Flux.fromIterable(neo4jClient.query("MATCH (c:Class {name: '"+className+"'})-[r:hasAttribute]->(a:Attribute) WHERE not a.name IN "+QueryUtils.formatList(attributeNameList)+
                                "RETURN DISTINCT a.name AS attributeName, a.type as attributeType, COUNT(a.name) AS occInClass ORDER BY occInClass DESC LIMIT 50")
                .fetchAs(AttributeOccurrenceQueryResult.class)
                .mappedBy((typeSystem, record) -> new AttributeOccurrenceQueryResult(
                            record.get("attributeName").asString(),
                        record.get("attributeType").asString(),
                        record.get("occInClass").asInt())).all()).collectList();
    }


    /**
     * Get the attributes which are siblings to attributes in the name list in parameters. This means that if an attribute
     * appears in the same class as an attribute with name in the list, it will be retrieved.
     * @param attributeNameList the name of attributes contained in the target class
     * @param excludeDefaultAttributes the property to ignore basic attributes for sibling computation. This might be the case
     *                                 for attributes very common to a lot of classes, such as id.
     * @return the list of identified attributes projections
     */
    public Mono<List<AttributeSiblingQueryResult>> getAttributeSiblings(List<String> attributeNameList, boolean excludeDefaultAttributes){
        List<String> augmentedAttributeNameList = new ArrayList<>(attributeNameList);
        List<String> diminishedAttributeNameList = new ArrayList<>(attributeNameList);

        if(excludeDefaultAttributes) {
            augmentedAttributeNameList.add("id");
            diminishedAttributeNameList.remove("id");
        }

        return Flux.fromIterable(neo4jClient.query("MATCH (a:Attribute)<-[:hasAttribute]-(n:Class)-[:hasAttribute]->(p:Attribute) WHERE p.name IN "+QueryUtils.formatList(diminishedAttributeNameList)+
                                " and not a.name IN "+QueryUtils.formatList(augmentedAttributeNameList)+" with a.name as attributeName, a.type as attributeType, " +
                                "collect(distinct id(n)) AS classIdList, p.name AS siblingName, COUNT(p) as occNumber where occNumber > 1 " +
                                "RETURN DISTINCT attributeName,  attributeType, classIdList, siblingName, occNumber ORDER BY occNumber DESC")
                .fetchAs(AttributeSiblingQueryResult.class)
                .mappedBy((typeSystem, record) -> new AttributeSiblingQueryResult(
                        record.get("attributeName").asString(),
                        record.get("attributeType").asString(),
                            record.get("classIdList").asList(Value::asInt),
                            record.get("occNumber").asInt(),
                            record.get("siblingName").asString())).all()).collectList();
    }

    /**
     * Get the attributes which are in class named as parameter surrounded in the same diagram by classes in classNameList parameter.
     * Attributes whose name are in attributeNameList will not be retrieved.
     * @param classname the name of the class targeted for recommendations
     * @param classNameList the name of classes in the project context, e.g. surrounding className in the diagram
     * @param attributeNameList the name of attributes already owned by the targetClass
     * @return the list of identified attributes projections
     */
    public Mono<List<AttributeContextQueryResult>> getAttributeContext(String classname, List<String> classNameList, List<String> attributeNameList){
        return Flux.fromIterable(neo4jClient.query("MATCH (n:Class {name: '"+classname+"'})--(m:Model)-[r]-(p:Class) WITH n,m,p WHERE p.name in "+QueryUtils.formatList(classNameList)+" MATCH (n)-[:hasAttribute]-(a:Attribute) " +
                        "WHERE not a.name IN "+QueryUtils.formatList(attributeNameList)+" WITH a.name as attributeName, a.type as attributeType,n,m,p RETURN collect(p.name) as sources, id(m) as modelId, count(p.name) as maxCtx, attributeName, attributeType ORDER BY maxCtx DESC")
                .fetchAs(AttributeContextQueryResult.class)
                .mappedBy((typeSystem, record) -> new AttributeContextQueryResult(
                        record.get("attributeName").asString(),
                        record.get("attributeType").asString(),
                        record.get("modelId").asInt(),
                        record.get("maxCtx").asInt(),
                            record.get("sources").asList((Value::asString)))).all()).collectList();
    }


}
