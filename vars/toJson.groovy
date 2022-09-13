import groovy.json.JsonOutput

def call(mapObj) {
    new JsonOutput().toJson(mapObj)
}