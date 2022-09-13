import groovy.json.JsonSlurperClassic

def call(json) {
    new JsonSlurperClassic().parseText(json)
}