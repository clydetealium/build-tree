package gov.cms.eqrs.awscli

import spock.lang.Specification

class STSServiceSpec extends Specification {  

    def "is an AWSService"() {
        given: 'a STSService instance'
            def service = new STSService([op: 'get-caller-identity'])
        expect: 'an AWSService'
            service instanceof AWSService
    }

    def "should supply a script"() {
        given: 'a STSService instance'
            def service = new STSService([op: 'get-caller-identity', region: 'us-east-1'])
        when: 'a script is requested'
            def script = service.getScript()
        then: 'the appropriate script is returned'
            script.script == '''aws sts get-caller-identity --region us-east-1'''
    }

    def "should require an 'op'"() {
        when: 'a bunk STSService instantiation is attempted'
            def service = new STSService()
            service.validate()
        then: 'an exception is thrown'
            def e = thrown AWSCLIValidationException
            e.message == 'Unsupported operation "null".'
    }

    def "should require a valid 'op'"() {
        when: 'a bunk STSService instantiation is attempted'
            def service = new STSService([op: 'bub'])
            service.validate()
        then: 'an exception is thrown'
            def e = thrown AWSCLIValidationException
            e.message == 'Unsupported operation "bub".'
    }
}
