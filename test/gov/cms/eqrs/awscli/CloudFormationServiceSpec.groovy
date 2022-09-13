package gov.cms.eqrs.awscli

import spock.lang.Specification

class CloudFormationServiceSpec extends Specification {  

    def "is an AWSService"() {
        given: 'a CloudFormationParameter instance'
            def service = new CloudFormationService([op: 'describe-stacks', name: 'meep'])
        expect: 'an AWSService'
            service instanceof AWSService
    }

    def "should supply a script"() {
        given: 'a CloudFormationParameter instance'
            def service = new CloudFormationService([
                op: 'describe-stacks', name: 'meep', region: 'us-east-1'])
        when: 'a script is requested'
            def script = service.getScript()
        then: 'the appropriate script is returned'
            script.script == '''aws cloudformation describe-stacks \
                --stack-name meep --region us-east-1'''
    }

    def "should support update-stack"() {
        given: 'a CloudFormationParameter instance'
            def parameters = '[{"ParameterKey":"one","ParameterValue":1}]'
            def service = new CloudFormationService([
                op: 'update-stack', name: 'meep', region: 'us-east-1',
                template: 'meep/mawp', parameters: parameters])
        when: 'a script is requested'
            def script = service.getScript()
        then: 'the appropriate script is returned'
            script.script.startsWith '''
                set +e
                output=$(aws cloudformation update-stack \
                --template-body file://meep/mawp \
                --parameters '[{"ParameterKey":"one","ParameterValue":1}]' \
                --capabilities "CAPABILITY_IAM" "CAPABILITY_NAMED_IAM" "CAPABILITY_AUTO_EXPAND" \
                --stack-name meep --region us-east-1 2>&1)'''
    }

    def "should require an 'op'"() {
        when: 'a bunk CloudFormationParameter instantiation is attempted'
            def service = new CloudFormationService()
            service.validate()
        then: 'an exception is thrown'
            def e = thrown AWSCLIValidationException
            e.message == 'Unsupported operation "null".'
    }

    def "should require a valid 'op'"() {
        when: 'a bunk CloudFormationParameter instantiation is attempted'
            def service = new CloudFormationService([op: 'bub'])
            service.validate()
        then: 'an exception is thrown'
            def e = thrown AWSCLIValidationException
            e.message == 'Unsupported operation "bub".'
    }

    def "should validate required parameters"() {
        when: 'a bunk CloudFormationParameter instantiation is attempted'
            def service = new CloudFormationService([op: 'describe-stacks'])
            service.validate()
        then: 'an exception is thrown'
            def e = thrown AWSCLIValidationException
            e.message == 'Parameter "name" is required for describe-stacks.'
    }

    def "should support stack-output inquiry"() {
        given: 'a CloudFormationParameter instance'
            def service = new CloudFormationService([
                op: 'stack-output', name: 'meep-stack',
                key: 'mawp', region: 'us-east-1'])
        when: 'a script is requested'
            def script = service.getScript()
        then: 'the appropriate script is returned'
            script.script == """aws cloudformation describe-stacks \
                --stack-name meep-stack --region us-east-1 --output text \
                --query 'Stacks[0].Outputs[?OutputKey==`mawp`].OutputValue'"""
    }
}
