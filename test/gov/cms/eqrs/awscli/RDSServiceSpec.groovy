package gov.cms.eqrs.awscli

import spock.lang.Specification

class RDSServiceSpec extends Specification {  

    def "is an AWSService"() {
        given: 'a RDSService instance'
            def service = new RDSService([op: 'describe-db-instances', dbInstanceId: 'bub', region: 'us-east-1'])
        expect: 'an AWSService'
            service instanceof AWSService
    }

    def "should supply a describe db instances script"() {
        given: 'a RDSService instance'
            def service = new RDSService([op: 'describe-db-instances', dbInstanceId: 'bub', region: 'us-east-1'])
        when: 'a script is requested'
            def script = service.getScript()
        then: 'the appropriate script is returned'
            script.script == '''aws rds describe-db-instances \
              --db-instance-identifier bub \
              --region us-east-1'''
    }

    def "should supply a modify db password script"() {
        given: 'a RDSService instance'
            def service = new RDSService([op: 'modify-db-instance-master-password',
                dbInstanceId: 'bub', password: 'secret value', region: 'us-east-1'])
        when: 'a script is requested'
            def script = service.getScript()
        then: 'the appropriate script is returned'
            script.script == '''aws rds modify-db-instance \
              --db-instance-identifier bub \
              --master-user-password $password \
              --region us-east-1'''
    }

    def "should generate a mask map"() {
        given: 'a RDSService instance'
            def service = new RDSService([op: 'modify-db-instance-master-password',
                dbInstanceId: 'bub', password: 'secret value', region: 'us-east-1'])
        when: 'a mask map is generated'
            def maskMap = service.generateMaskMap()
        then: 'the appropriate values are represented'
            maskMap['password'] == 'secret value'
    }

    def "should require an 'op'"() {
        when: 'a bunk RDSService instantiation is attempted'
            def service = new RDSService([dbInstanceId: 'bub', region: 'us-east-1'])
            service.validate()
        then: 'an exception is thrown'
            def e = thrown AWSCLIValidationException
            e.message == 'Unsupported operation "null".'
    }

    def "should require a valid 'op'"() {
        when: 'a bunk RDSService instantiation is attempted'
            def service = new RDSService([op: 'meep', dbInstanceId: 'bub', region: 'us-east-1'])
            service.validate()
        then: 'an exception is thrown'
            def e = thrown AWSCLIValidationException
            e.message == 'Unsupported operation "meep".'
    }

    def "should enforce required parameters "() {
        when: 'a bunk RDSService instantiation is attempted'
            def service = new RDSService([op: 'describe-db-instances'])
            service.validate()
        then: 'an exception is thrown'
            def e = thrown AWSCLIValidationException
            e.message == 'Parameter "dbInstanceId" is required for describe-db-instances.'
    }
}
