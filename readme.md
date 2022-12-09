# Modeling Assistant Recommender

This project defines a score-based multi-criteria recommender system which is able to provide 
UML elements recommendations for requests. To learn about the current abilities of the system, 
please see Section [Current status](#current-status).

### Current Status
#### Version 1.0.0
- Support for Attribute recommendations based on scores s1 (Recurrence), s2 (Exclusivity), 
s3 (Siblings synergy), and s4 (Context).
- Support for Relation recommendations based on scores s1 (Recurrence), s2 (Exclusivity),
    s3 (Siblings synergy), and s4 (Context).
- Filtering support based on `FilterOptions` class, which allows filtering according to 
confidence score or number of recommendations.


## Installation
### Prerequisite
#### Docker
To facilitate the deployment of the server on your own machine, the backend relies on `docker-compose` and `docker` 
containers. Please make sure these tools are installed on your machine before trying to set up the environment.
You need to install Docker Engine, Docker CLI, and Docker Compose. The recommended way is to install Docker Desktop 
which includes all three tools (see more details at https://docs.docker.com/compose/install/).

#### Port 8080
By default, at startup, the modeling assistant recommender will be available on port 8080. Please make sure that this port 
is available when running the server.

### Install procedure
#### 1. Clone repository
Clone this repository to access it locally on your machine.

#### 2. Download data
If you want the Neo4j and Redis containers to start populated with a sample DB to use the recommender, please follow step 2. and 3.
Please download the following archive: https://shift-music.fr/work/modelingassistantrecommender/data-dump.zip
Extract the archive and place the folder `data-dump` to the root of this project.

#### 3. Run data population script

Open a terminal at the root of the project. Run the script `init-script.sh` as follows:
```
./init-script.sh
```
On Windows, please use the PowerShell. If you have trouble to run the file, try adding the executing rights wil the following command:
```
chmod +x ./init-script.sh
```
If you don't want to populate databases, class script `init-empty.sh`
#### 4. Start the docker containers
To start Neo4j and Redis instances, and start the recommender server, use the following command at the root of the project:
```
docker-compose up
```
If images of neo4j and redis must be retrieved for the first launch, the first start might take several minutes.
the construction of the Docker image of the app will also call a Maven build, which will download all libraries.
The containers will start after these processes.


## Using the recommender
The Modeling Assistant Recommender exposes API endpoints for clients to contact and get recommendations.
When the containers are started, you can retrieve recommendations by running POST HTTP requests on the endpoints as described in the following API documentation: https://app.swaggerhub.com/apis-docs/msavaryleblanc/ModelingAssistant/1.0.0

We also provide a collection of Postman requests, to try the server easily. 
You can download Postman [here](https://www.postman.com/downloads/) and import the collection that is located in the root of the project, as `modeling_assistant_recommender.postman_collection.json`





## Literature
The behaviour of the system, its rationale, and more details about the individual 
recommendation scores are available in the following research publications:
* [A recommender system to assist conceptual modeling with UML](http://dx.doi.org/10.18293/SEKE2021-039) (Maxime Savary-Leblanc, Xavier Le Pallec, Sébastien Gérard)
* [A modeling assistant for cognifying MBSE tools](http://dx.doi.org/10.1109/MODELS-C53483.2021.00097) (Maxime Savary-Leblanc, Xavier Le Pallec, Sébastien Gérard)
* [Augmenting software engineers with modeling assistants](http://dx.doi.org/10.13140/RG.2.2.18267.28967). Maxime Savary-Leblanc

## License GNU GPL3
Score-based multi-criteria recommender system for UML elements
Copyright (C) 2022  Maxime Savary-Leblanc

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program (see file `COPYING`). If not, see <http://www.gnu.org/licenses/>.