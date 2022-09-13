package gov.cms.tealium.async

import groovy.transform.PackageScope

class Poll implements Serializable {
    private def config
    
    @PackageScope
    Poll(config) {
        this.config = config
    }

    def poll() {
        def outcome = false
        for (int i = 0; i < config.limit; i++) {
            def output = config.action.call()
            def result = config.satisfaction.call(output)
            if (result.done) {
                outcome = result.outcome
                break
            }
            Thread.sleep(config.interval)
        }
        outcome
    }
}