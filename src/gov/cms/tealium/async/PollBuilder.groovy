package gov.cms.tealium.async

class PollBuilder implements Serializable {
    private Closure satisfaction
    private Integer limit
    private Closure action
    private Integer interval

    PollBuilder(){}

    PollBuilder withAction(Closure action) {
        this.action = action
        this
    }

    PollBuilder withLimit(int limit) {
        this.limit = limit
        this
    }

    PollBuilder withInterval(int interval) {
        this.interval = interval
        this
    }

    PollBuilder withSatisfaction(Closure satisfaction) {
        this.satisfaction = satisfaction
        this
    }

    Poll build() {
        if (satisfaction == null || limit == null || 
            action == null || interval == null) {
            def message = "Must provide assign all of the following values: satisfaction, limit, action, interval."
            throw new AsyncException(message)
        }
        new Poll(satisfaction: satisfaction, limit: limit, 
            action: action, interval: interval)
    }
}