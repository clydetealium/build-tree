package gov.cms.tealium.utility

import groovy.json.JsonSlurperClassic
import groovy.json.JsonOutput

class MapUtils {

    static Map merge(Map rhs, Map lhs) {
        def lhsClone = new JsonSlurperClassic().parseText(new JsonOutput().toJson(lhs))
        rhs.inject(lhsClone) { map, entry ->
            if (map[entry.key] instanceof Map && entry.value instanceof Map) {
                map[entry.key] = merge(map[entry.key], entry.value)
            } else if (map[entry.key] instanceof Collection && entry.value instanceof Collection) {
                map[entry.key] += entry.value
            } else {
                map[entry.key] = entry.value
            }
            map
        }
    }
}