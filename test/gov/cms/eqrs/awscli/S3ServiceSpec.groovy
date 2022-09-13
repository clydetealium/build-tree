package gov.cms.eqrs.awscli

import spock.lang.Specification
import spock.lang.Unroll

class S3ServiceSpec extends Specification {  

    def "is an AWSService"() {
        given: 'a S3Service instance'
            def service = new S3Service([op: 'search-by-bucket-name'])
        expect: 'an AWSService'
            service instanceof AWSService
    }

    def "should supply a bucket name search script"() {
        given: 'a S3Service instance'
            def service = new S3Service([op: 'search-by-bucket-name', searchTerm: 'bucket-name', region: 'us-east-1'])
        when: 'a script is requested'
            def script = service.getScript()
        then: 'the appropriate script is returned'
            script.script == '''aws s3api list-buckets \
                --query 'Buckets[?Name==`bucket-name`].Name' \
                --region us-east-1'''
    }

    def "should supply a bucket name fuzzy search script"() {
        given: 'a S3Service instance'
            def service = new S3Service([op: 'search-by-bucket-name', searchTerm: 'bucket-name',
                fuzzy: true, region: 'us-east-1'])
        when: 'a script is requested'
            def script = service.getScript()
        then: 'the appropriate script is returned'
            script.script == '''aws s3api list-buckets \
                --query 'Buckets[?contains(Name,`bucket-name`)].{Name:Name}' \
                --region us-east-1'''
    }

    def "should fail search-by-bucket-name without required parameters"() {
        when: 'a bunk S3Service instantiation is attempted'
            def service = new S3Service([op: 'search-by-bucket-name'])
            service.validate()
        then: 'an exception is thrown'
            def e = thrown AWSCLIValidationException
            e.message == 'Parameter "searchTerm" is required for search-by-bucket-name.'
    }

    def "should supply a bucket creation script"() {
        given: 'a S3Service instance'
            def service = new S3Service([op: 'create-bucket', name: 'bucket-name', region: 'us-east-1'])
        when: 'a script is requested'
            def script = service.getScript()
        then: 'the appropriate script is returned'
            script.script == '''aws s3api create-bucket \
                --bucket 'bucket-name' \
                --region us-east-1'''
    }

    def "should fail create-bucket without required parameters"() {
        when: 'a bunk S3Service instantiation is attempted'
            def service = new S3Service([op: 'create-bucket'])
            service.validate()
        then: 'an exception is thrown'
            def e = thrown AWSCLIValidationException
            e.message == 'Parameter "name" is required for create-bucket.'
    }

    def "should supply a put-object script"() {
        given: 'a S3Service instance'
            def service = new S3Service([op: 'put-object', bucket: 'bucket',
                key: 'key', body: 'body', region: 'region'])
        when: 'a script is requested'
            def script = service.getScript()
        then: 'the appropriate script is returned'
            script.script == '''aws s3api put-object \
                --bucket 'bucket' \
                --key key \
                --body body \
                --region region'''
    }

    def "should supply a get-object script"() {
        given: 'a S3Service instance'
            def service = new S3Service([op: 'get-object', bucket: 'bucket',
                key: 'key', outfile: './out.file', region: 'region'])
        when: 'a script is requested'
            def script = service.getScript()
        then: 'the appropriate script is returned'
            script.script == '''aws s3api get-object \
                --bucket 'bucket' \
                --key key \
                --region region \
                ./out.file'''
    }

    @Unroll("get-object should require #scenario.missing")
    def "should fail get-object without required parameters"() {
        when: 'a bunk S3Service instantiation is attempted'
            def service = new S3Service(scenario.params)
            service.validate()
        then: 'an exception is thrown'
            def e = thrown AWSCLIValidationException
            e.message == "Parameter \"${scenario.missing}\" is required for get-object."
        where:
            scenario << [
            [ missing: 'bucket',
                params: [op: 'get-object', key: 'key', region: 'region', outfile: 'out']],
            [ missing: 'key',
                params: [op: 'get-object', bucket: 'bucket', region: 'region', outfile: 'out']],
            [ missing: 'region',
                params: [op: 'get-object', bucket: 'bucket', key: 'key', outfile: 'out']],
            [ missing: 'outfile',
                params: [op: 'get-object', bucket: 'bucket', key: 'key', region: 'region']]
            ]
    }

    @Unroll("put-object should require #scenario.missing")
    def "should fail put-object without required parameters"() {
        when: 'a bunk S3Service instantiation is attempted'
            def service = new S3Service(scenario.params)
            service.validate()
        then: 'an exception is thrown'
            def e = thrown AWSCLIValidationException
            e.message == "Parameter \"${scenario.missing}\" is required for put-object."
        where:
            scenario << [
            [ missing: 'bucket',
                params: [op: 'put-object', key: 'key', body: 'body', region: 'region']],
            [ missing: 'key',
                params: [op: 'put-object', bucket: 'bucket', body: 'body', region: 'region']],
            [ missing: 'body',
                params: [op: 'put-object', bucket: 'bucket', key: 'key', region: 'region']],
            [ missing: 'region',
                params: [op: 'put-object', bucket: 'bucket', key: 'key', body: 'body']]
            ]
    }

    def "should supply a presign script"() {
        given: 'a S3Service instance'
            def service = new S3Service([op: 'presign',
                s3Url: 's3://bucket/key', region: 'us-east-1'])
        when: 'a script is requested'
            def script = service.getScript()
        then: 'the appropriate script is returned'
            script.script == '''aws configure set default.s3.signature_version s3v4 \
                    --region us-east-1
                   aws s3 presign s3://bucket/key --expires-in 3600 \
                    --region us-east-1'''
    }

    def "should supply a presign script with custom expiry"() {
        given: 'a S3Service instance'
            def service = new S3Service([op: 'presign', expiry: 500,
                s3Url: 's3://bucket/key', region: 'us-east-1'])
        when: 'a script is requested'
            def script = service.getScript()
        then: 'the appropriate script is returned'
            script.script == '''aws configure set default.s3.signature_version s3v4 \
                    --region us-east-1
                   aws s3 presign s3://bucket/key --expires-in 500 \
                    --region us-east-1'''
    }

    @Unroll("presign should require #scenario.missing")
    def "should fail presign without required parameters"() {
        when: 'a bunk S3Service instantiation is attempted'
            def service = new S3Service(scenario.params)
            service.validate()
        then: 'an exception is thrown'
            def e = thrown AWSCLIValidationException
            e.message == "Parameter \"${scenario.missing}\" is required for presign."
        where:
            scenario << [
                [ missing: 's3Url', params: [op: 'presign', region: 'region']],
                [ missing: 'region', params: [op: 'presign', s3Url: 's3://bucket/key']]
            ]
    }
    
    def "should require an 'op'"() {
        when: 'a bunk S3Service instantiation is attempted'
            def service = new S3Service()
            service.validate()
        then: 'an exception is thrown'
            def e = thrown AWSCLIValidationException
            e.message == 'Unsupported operation "null".'
    }

    def "should require a valid 'op'"() {
        when: 'a bunk S3Service instantiation is attempted'
            def service = new S3Service([op: 'bub'])
            service.validate()
        then: 'an exception is thrown'
            def e = thrown AWSCLIValidationException
            e.message == 'Unsupported operation "bub".'
    }
}
