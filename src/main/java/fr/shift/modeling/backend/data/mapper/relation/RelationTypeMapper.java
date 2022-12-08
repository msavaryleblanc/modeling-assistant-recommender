package fr.shift.modeling.backend.data.mapper.relation;

public class RelationTypeMapper {

    public static String getStringType(String relationType, boolean isEndNode) {
        if("UNDIRECTED_ASSOCIATION".equals(relationType)){
            return relationType;
        }
        String suffix = isEndNode ? "_IN" : "_OUT";
        return relationType + suffix;
    }
}
