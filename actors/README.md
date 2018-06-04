# Actors

### Pre-requirements

* install and launch MongoDB
* import data to mongo
    * cd data/post-nlp
    * mongoimport --db argumentation-agents --collection reviews --file reviews-with-features.json --jsonArray
* if error in mongo occurs:
    * mongo
    * use argumentation-agents
    * db.dropDatabase()

### Installation

To install appropriate packages use IntelliJ IDEA.

### Launch agents

Type `run` in IntelliJ IDEA console.
