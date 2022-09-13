package gov.cms.eqrs.awscli

import spock.lang.Specification

class ELBServiceSpec extends Specification {  

    def "is an AWSService"() {
        given: 'a ELBService instance'
            def service = new ELBService([op: 'listener-rules', arn: 'bub', region: 'us-east-1'])
        expect: 'an AWSService'
            service instanceof AWSService
    }

    def "should supply a script"() {
        given: 'a ELBService instance'
            def service = new ELBService([op: 'listener-rules', arn: 'bub', region: 'us-east-1'])
        when: 'a script is requested'
            def script = service.getScript()
        then: 'the appropriate script is returned'
            script.script == """aws elbv2 describe-rules \
                --output json --region us-east-1 \
                --query 'Rules[*].{priority: Priority,value: Conditions[0].Values[0]}'\
                --listener-arn bub"""
    }
}
