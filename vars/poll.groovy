import gov.cms.eqrs.async.PollBuilder

def call(config) {
    new PollBuilder()
        .withAction(config.action)
        .withInterval(config.interval)
        .withLimit(config.limit)
        .withSatisfaction(config.satisfaction)
        .build()
        .poll()
}