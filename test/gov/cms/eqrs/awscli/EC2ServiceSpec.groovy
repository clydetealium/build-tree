package gov.cms.eqrs.awscli

import spock.lang.Specification

class EC2ServiceSpec extends Specification {  

    def "is an AWSService"() {
        given: 'a EC2Service instance'
            def service = new EC2Service([op: 'subnet-ids', name: 'bub', region: 'us-east-1'])
        expect: 'an AWSService'
            service instanceof AWSService
    }

    def "should supply a subnet id script"() {
        given: 'a EC2Service instance'
            def service = new EC2Service([op: 'subnet-ids', name: 'bub', region: 'us-east-1'])
        when: 'a script is requested'
            def script = service.getScript()
        then: 'the appropriate script is returned'
            script.script == '''aws ec2 describe-subnets --query \
              'join(`,`, Subnets[?Tags[?Key==`Name` && contains(Value, `bub`)]].[SubnetId][][])' \
              --output text --region us-east-1'''
    }

    def "should supply a security group ids script"() {
        given: 'a EC2Service instance'
            def service = new EC2Service([op: 'security-group-ids',
                names: ['beep', 'boop'], region: 'us-east-1'])
        when: 'a SG script is requested'
            def script = service.getScript()
        then: 'the appropriate script is returned'
            script.script == """aws ec2 describe-security-groups \
                --filters Name=tag:Name,Values=beep,boop --output text --region us-east-1 \
                --query 'join(`,`, SecurityGroups[].GroupId)'"""
    }

    def "should require an 'op'"() {
        when: 'a bunk EC2Service instantiation is attempted'
            def service = new EC2Service([name: 'bub', region: 'us-east-1'])
            service.validate()
        then: 'an exception is thrown'
            def e = thrown AWSCLIValidationException
            e.message == 'Unsupported operation "null".'
    }

    def "should require a valid 'op'"() {
        when: 'a bunk EC2Service instantiation is attempted'
            def service = new EC2Service([op: 'meep', name: 'bub', region: 'us-east-1'])
            service.validate()
        then: 'an exception is thrown'
            def e = thrown AWSCLIValidationException
            e.message == 'Unsupported operation "meep".'
    }
}
