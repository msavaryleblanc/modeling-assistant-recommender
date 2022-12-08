package fr.shift.modeling.backend.controller.entity;
/*
 * This file is part of the Modeling Assistant Recommender. Author: Maxime Savary-Leblanc
 * The Modeling Assistant Recommender is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * The Modeling Assistant Recommender is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with The Modeling Assistant Recommender. If not, see <https://www.gnu.org/licenses/>.
 */
public class Identity {
    String version;
    String hostname;
    String os;
    String java;
    String username;
    String session;

    public Identity(String version, String hostname, String username, String os, String java, String session) {
        super();
        this.version = version;
        this.hostname = hostname;
        this.os = os;
        this.java = java;
        this.username = username;
        this.session = session;
    }

    public String getVersion() {
        return version;
    }

    public String getHostname() {
        return hostname;
    }

    public String getOs() {
        return os;
    }

    public String getJava() {
        return java;
    }

    public String getUsername() {
        return username;
    }

    public String getSession() {
        return session;
    }

    @Override
    public String toString() {
        return "{" +
                "version='" + version + '\'' +
                ", hostname='" + hostname + '\'' +
                ", os='" + os + '\'' +
                ", java='" + java + '\'' +
                ", username='" + username + '\'' +
                ", session='" + session + '\'' +
                '}';
    }
}
