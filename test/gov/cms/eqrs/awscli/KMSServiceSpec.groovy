package gov.cms.eqrs.awscli

import spock.lang.Specification

class KMSServiceSpec extends Specification {  

    def "is an AWSService"() {
        given: 'a KMSService instance'
            def service = new KMSService([op: 'key-id-from-alias', alias: 'alias/dev2/meep', region: 'us-east-1'])
        expect: 'an AWSService'
            service instanceof AWSService
    }

    def "should supply a script for key id retrieval from alias"() {
        given: 'a KMSService instance'
            def service = new KMSService([op: 'key-id-from-alias', alias: 'alias/dev2/meep', region: 'us-east-1'])
        when: 'a script is requested'
            def script = service.getScript()
        then: 'the appropriate script is returned'
            script.script == '''aws kms list-aliases \
              --query "Aliases[?AliasName == 'alias/dev2/meep'].TargetKeyId" \
              --output text \
              --region us-east-1'''
    }

    def "should require an 'op'"() {
        when: 'a bunk KMSService instantiation is attempted'
            def service = new KMSService([alias: 'alias/dev2/meep', region: 'us-east-1'])
            service.validate()
        then: 'an exception is thrown'
            def e = thrown AWSCLIValidationException
            e.message == 'Unsupported operation "null".'
    }

    def "should require a valid 'op'"() {
        when: 'a bunk KMSService instantiation is attempted'
            def service = new RDSService([op: 'meep', alias: 'alias/dev2/meep', region: 'us-east-1'])
            service.validate()
        then: 'an exception is thrown'
            def e = thrown AWSCLIValidationException
            e.message == 'Unsupported operation "meep".'
    }

    def "should enforce required parameters "() {
        when: 'a bunk KMSService instantiation is attempted'
            def service = new KMSService([op: 'key-id-from-alias', region: 'us-east-1'])
            service.validate()
        then: 'an exception is thrown'
            def e = thrown AWSCLIValidationException
            e.message == 'Parameter "alias" is required for key-id-from-alias.'
    }
}
