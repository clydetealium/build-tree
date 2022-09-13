package gov.cms.eqrs.awscli

import spock.lang.Specification

class SSMServiceSpec extends Specification {  

    def "is an AWSService"() {
        given: 'a SSMParameter instance'
            def service = new SSMService([op: 'get-parameter', name: 'meep'])
        expect: 'an AWSService'
            service instanceof AWSService
    }

    def "should supply a script"() {
        given: 'a SSMParameter instance'
            def service = new SSMService([
                op: 'get-parameter', name: 'meep', region: 'us-east-1'])
        when: 'a script is requested'
            def script = service.getScript()
        then: 'the appropriate script is returned'
            script.script == '''aws ssm get-parameter --name meep \
                --with-decryption --query Parameter.Value \
                --output text --region us-east-1'''
    }

    def "should supply a put secure parameter script"() {
        given: 'a SSMParameter instance'
            def service = new SSMService([
                op: 'put-secure-parameter', name: 'meep', value: 'mawp',
                description: 'a parameter', region: 'us-east-1'])
        when: 'a script is requested'
            def script = service.getScript()
        then: 'the appropriate script is returned'
            script.script == '''aws ssm put-parameter --name meep \
                --description 'a parameter' \
                --value 'mawp' \
                --type SecureString \
                --overwrite \
                --region us-east-1'''
    }

    def "should generate a mask map"() {
        given: 'a SSMService instance'
            def service = new SSMService([
                op: 'put-secure-parameter', name: 'meep', value: 'mawp',
                description: 'a parameter', region: 'us-east-1'])
        when: 'a mask map is generated'
            def maskMap = service.generateMaskMap()
        then: 'the appropriate values are represented'
            maskMap['value'] == 'mawp'
    }

    def "should require an 'op'"() {
        when: 'a bunk SSMParameter instantiation is attempted'
            def service = new SSMService()
            service.validate()
        then: 'an exception is thrown'
            def e = thrown AWSCLIValidationException
            e.message == 'Unsupported operation "null".'
    }

    def "should require a valid 'op'"() {
        when: 'a bunk SSMParameter instantiation is attempted'
            def service = new SSMService([op: 'bub'])
            service.validate()
        then: 'an exception is thrown'
            def e = thrown AWSCLIValidationException
            e.message == 'Unsupported operation "bub".'
    }

    def "should validate required parameters"() {
        when: 'a bunk SSMParameter instantiation is attempted'
            def service = new SSMService([op: 'get-parameter'])
            service.validate()
        then: 'an exception is thrown'
            def e = thrown AWSCLIValidationException
            e.message == 'Parameter "name" is required for get-parameter.'
    }
}
