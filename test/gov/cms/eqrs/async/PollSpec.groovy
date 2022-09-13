package gov.cms.eqrs.async

import spock.lang.Specification
import spock.lang.Unroll
import gov.cms.eqrs.async.PollBuilder

class PollSpec extends Specification {  

    def "should regulate initialization"() {  
        when: 'constructing a poll'
            def poll = new PollBuilder()
                .withAction({'noop'})
                .withInterval(1)
                .withLimit(1)
                .withSatisfaction({'noop'})
                .build()
        then: "yep, it's a poll"
            poll instanceof Poll
    }

    @Unroll("should error when #scenario is absent")
    def "absent attribute"() {  
        given: 'potential invokations'
            def attributes = [
                withAction: {'noop'},
                withInterval: 1,
                withLimit: 1,
                withSatisfaction: {'noop'}]
            attributes.remove(scenario)

        when: 'initialized with a missing value'
            PollBuilder builder = new PollBuilder()
            for (method: builder.metaClass.methods) {
                if (attributes.keySet().contains(method.name)) {
                    method.invoke(builder, attributes[method.name])
                }
            }
            builder.build()
        then: 'the build operation should fail'
            def e = thrown AsyncException
            e.message.contains 'Must provide assign all of the following values'
            
        where:
            scenario << ['withAction', 'withInterval', 'withLimit', 'withSatisfaction']
    }

    def "should facilitate satisfactory outcomes"() {
        when: 'constructing a poll'
            def iterationCount = 0
            def pollResult = new PollBuilder()
                .withAction({ iterationCount++ })
                .withInterval(50)
                .withLimit(4)
                .withSatisfaction({ input ->
                    (input > 2) ? [done: true, outcome: input] : [done: false]
                })
                .build()
                .poll()
            Thread.sleep(300)

        then: "the result should be true"
            pollResult == 3
    }

    def "should communicate unsatisfactory outcomes"() {
        when: 'constructing a poll'
            def iterationCount = 0
            def pollResult = new PollBuilder()
                .withAction({ iterationCount++ })
                .withInterval(50)
                .withLimit(2)
                .withSatisfaction({ input ->
                    (input > 4) ? [done: true, outcome: input] : [done: false]
                })
                .build()
                .poll()
            Thread.sleep(300)

        then: "the result should be false"
            !pollResult
    }
}
