package gov.cms.tealium.utility

class ListUtils {

    static boolean isCollectionOrArray(object) {    
        [Collection, Object[]].any { it.isAssignableFrom(object.getClass()) }
    }
}