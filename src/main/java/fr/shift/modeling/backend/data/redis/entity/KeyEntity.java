package fr.shift.modeling.backend.data.redis.entity;
/*
 * This file is part of the Modeling Assistant Recommender. Author: Maxime Savary-Leblanc
 * The Modeling Assistant Recommender is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * The Modeling Assistant Recommender is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with The Modeling Assistant Recommender. If not, see <https://www.gnu.org/licenses/>.
 */
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("Entity")
public class KeyEntity {

    public enum Type{
        ATTRIBUTE, CLASS, TYPE
    }

    @Id private String id;
    private String name;
    private int value;
    private Type type;

    public KeyEntity(String name, Type type) {
        this.name = name;
        this.type = type;
        this.value = 1;
    }

    public KeyEntity(String id, String name, int value, Type type) {
        this.id = id;
        this.name = name;
        this.value = value;
        this.type = type;
    }

    public KeyEntity() {
    }

    public int getValue() {
        return value;
    }

    public void incrementValue() {
        this.value = value + 1;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "KeyEntity{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", value=" + value +
                ", type=" + type +
                "} " + super.toString();
    }
}

