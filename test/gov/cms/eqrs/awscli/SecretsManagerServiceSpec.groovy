package gov.cms.eqrs.awscli

import spock.lang.Specification
import spock.lang.Unroll

class SecretsManagerServiceSpec extends Specification {  

    def "is an AWSService"() {
        given: 'a SecretsManagerService instance'
            def service = new SecretsManagerService([op: 'get-secret-value', secretId: 'meep'])
        expect: 'an AWSService'
            service instanceof AWSService
    }

    def "should supply a script"() {
        given: 'a SecretsManagerService instance'
            def service = new SecretsManagerService([
                op: 'get-secret-value', secretId: 'meep', region: 'us-east-1'])
        when: 'a script is requested'
            def script = service.getScript()
        then: 'the appropriate script is returned'
            script.script == '''aws secretsmanager get-secret-value \
            --secret-id 'meep' \
            --version-stage AWSCURRENT \
            --region us-east-1 \
            --query 'SecretString' \
            --output text 2> /dev/null || echo {\\\"status\\\": \$?}'''
    }

    def "should require an 'op'"() {
        when: 'a bunk SecretsManagerService instantiation is attempted'
            def service = new SecretsManagerService()
            service.validate()
        then: 'an exception is thrown'
            def e = thrown AWSCLIValidationException
            e.message == 'Unsupported operation "null".'
    }

    def "should require a valid 'op'"() {
        when: 'a bunk SecretsManagerService instantiation is attempted'
            def service = new SecretsManagerService([op: 'bub'])
            service.validate()
        then: 'an exception is thrown'
            def e = thrown AWSCLIValidationException
            e.message == 'Unsupported operation "bub".'
    }

    def "should validate required parameters"() {
        when: 'a bunk SecretsManagerService instantiation is attempted'
            def service = new SecretsManagerService([op: 'get-secret-value'])
            service.validate()
        then: 'an exception is thrown'
            def e = thrown AWSCLIValidationException
            e.message == 'Parameter "secretId" is required for get-secret-value.'
    }

    @Unroll("sh attribute assignment #scenario.label")
    def "should support sh attribute overrides"() {
        given: 'Parameters'
            def parameters = [op: 'get-secret-value', secretId: 'meep', region: 'us-east-1']
            if (scenario.override) {
                parameters['override'] = scenario.override
            }
        when: 'a script is requested'
            def service = new SecretsManagerService(parameters)
            def script = service.getScript()
        then: 'the appropriate script is returned'
            script[scenario.option] == true
        where:
            scenario << [
            [ label: 'no override', option: 'returnStdout'],
            [ label: 'returnStatus', override: [returnStatus: true], option: 'returnStatus'],
            [ label: 'returnStdout', override: [returnStdout: true], option: 'returnStdout']]
    }
}
