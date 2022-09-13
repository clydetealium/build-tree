package gov.cms.eqrs.awscli

import spock.lang.Specification

class AWSServiceFactorySpec extends Specification {

    def "should support SSM"() {
        given: 'a factory instance'
            def factory = new AWSServiceFactory()
        when: 'a SSM service is requested'
            def interaction = factory.getService([
                service: 'ssm',
                op: 'get-parameter', 
                name: 'meep'])
        then: 'a service is returned'
            verifyAll {
                interaction instanceof AWSService
                interaction instanceof SSMService
            }
    }

    def "should support ECR"() {
        given: 'a factory instance'
            def factory = new AWSServiceFactory()
        when: 'a ECR service is requested'
            def interaction = factory.getService([
                service: 'ecr',
                op: 'get-authorization-token',
                region: 'us-east-1'])
        then: 'a service is returned'
            verifyAll {
                interaction instanceof AWSService
                interaction instanceof ECRService
            }
    }

    def "should support RDS"() {
        given: 'a factory instance'
            def factory = new AWSServiceFactory()
        when: 'a RDS service is requested'
            def interaction = factory.getService([
                service: 'rds',
                op: 'describe-db-instances',
                dbInstanceId: 'bub',
                region: 'us-east-1'])
        then: 'a service is returned'
            verifyAll {
                interaction instanceof AWSService
                interaction instanceof RDSService
            }
    }

    def "should support KMS"() {
        given: 'a factory instance'
            def factory = new AWSServiceFactory()
        when: 'a RDS service is requested'
            def interaction = factory.getService([
                service: 'kms',
                op: 'key-id-from-alias',
                alias: '/alias/dev2/bub',
                region: 'us-east-1'])
        then: 'a service is returned'
            verifyAll {
                interaction instanceof AWSService
                interaction instanceof KMSService
            }
    }

    def "should support S3"() {
        given: 'a factory instance'
            def factory = new AWSServiceFactory()
        when: 'a S3 service is requested'
            def interaction = factory.getService([
                service: 's3', op: 'presign',
                s3Url: 's3://bucket/key', region: 'us-east-1'])
        then: 'a service is returned'
            verifyAll {
                interaction instanceof AWSService
                interaction instanceof S3Service
            }
    }

    def "should support Security Token Service"() {
        given: 'a factory instance'
            def factory = new AWSServiceFactory()
        when: 'a Security Token Service is requested'
            def interaction = factory.getService([
                service: 'sts',
                op: 'get-caller-identity',
                region: 'us-east-1'])
        then: 'a service is returned'
            verifyAll {
                interaction instanceof AWSService
                interaction instanceof STSService
            }
    }

    def "should support SecretsManager"() {
        given: 'a factory instance'
            def factory = new AWSServiceFactory()
        when: 'a SecretsManager service is requested'
            def interaction = factory.getService([
                service: 'secretsmanager',
                op: 'get-secret-value',
                secretId: 'meep',
                region: 'us-east-1'])
        then: 'a service is returned'
            verifyAll {
                interaction instanceof AWSService
                interaction instanceof SecretsManagerService
            }
    }

    def "should require specification of supported service"() {
        given: 'a factory instance'
            def factory = new AWSServiceFactory()
        when: 'an unsupported service is specified'
            def interaction = factory.getService([service: 'notSupported'])
        then: 'an exception is thrown'
            def e = thrown AWSCLIValidationException
            e.message.contains 'Unsupported service: "notSupported".'
    }
}
