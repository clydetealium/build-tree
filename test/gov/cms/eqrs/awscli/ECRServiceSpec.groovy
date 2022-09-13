package gov.cms.eqrs.awscli

import spock.lang.Specification

class ECRServiceSpec extends Specification {  

    def "is an AWSService"() {
        given: 'a ECRService instance'
            def service = new ECRService([op: 'get-authorization-token'])
        expect: 'an AWSService'
            service instanceof AWSService
    }

    def "should supply a script"() {
        given: 'a ECRService instance'
            def service = new ECRService([op: 'get-authorization-token', region: 'us-east-1'])
        when: 'a script is requested'
            def script = service.getScript()
        then: 'the appropriate script is returned'
            script.script == '''aws ecr get-authorization-token --region us-east-1 \
                --query authorizationData[0].authorizationToken --output=text | \
                base64 -d'''
    }

    def "should supply a tag match script"() {
        given: 'a ECRService instance'
            def service = new ECRService([op: 'latest-by-tag-match', name: 'bub',
                region: 'us-east-1', match: 'huh'])
        when: 'a script is requested'
            def script = service.getScript()
        then: 'the appropriate script is returned'
            // This behaves strangely when using --output text, returns many values instead of single
            // returns single value when using --output json
            script.script == '''aws ecr describe-images --repository-name bub  \
                --region us-east-1 --query 'imageDetails[?imageTags[?contains(@, to_string(`huh`))]] | \
                max_by(@, &to_number(imagePushedAt)).imageTags | [0]' | tr -d \\\"'''
    }

    def "should support multiple matches"() {
        given: 'a ECRService instance'
            def service = new ECRService([op: 'latest-by-tag-match', name: 'bub',
                region: 'us-east-1', match: ['hotfix', 'release']])
        when: 'a script is requested'
            def script = service.getScript()
        then: 'the appropriate script is returned'
            // This behaves strangely when using --output text, returns many values instead of single
            // returns single value when using --output json
            script.script == '''aws ecr describe-images --repository-name bub  \
                --region us-east-1 --query 'imageDetails[?imageTags[?contains(@, to_string(`hotfix`)) || contains(@, to_string(`release`))]] | \
                max_by(@, &to_number(imagePushedAt)).imageTags | [0]' | tr -d \\\"'''
    }

    def "should support image scan findings"() {
        given: 'a ECRService instance'
            def service = new ECRService([op: 'describe-image-scan-findings', name: 'bub',
                imageId: 'image', region: 'us-east-1'])
        when: 'a script is requested'
            def script = service.getScript()
        then: 'the appropriate script is returned'
            script.script == '''aws ecr describe-image-scan-findings --repository-name bub \
                --image-id imageTag=image --region us-east-1'''
    }

    def "should support image tag listing"() {
        given: 'a ECRService instance'
            def service = new ECRService([op: 'list-image-tags',
                name: 'bub', region: 'us-east-1'])
        when: 'a script is requested'
            def script = service.getScript()
        then: 'the appropriate script is returned'
            script.script == '''aws ecr describe-images --repository-name bub \
                --region us-east-1 \
                --query 'reverse(sort_by(imageDetails,& imagePushedAt)[*].{tag: imageTags[0], pushedAt: imagePushedAt})''''
    }

    def "should require an 'op'"() {
        when: 'a bunk ECRService instantiation is attempted'
            def service = new ECRService()
            service.validate()
        then: 'an exception is thrown'
            def e = thrown AWSCLIValidationException
            e.message == 'Unsupported operation "null".'
    }

    def "should require a valid 'op'"() {
        when: 'a bunk ECRService instantiation is attempted'
            def service = new ECRService([op: 'bub'])
            service.validate()
        then: 'an exception is thrown'
            def e = thrown AWSCLIValidationException
            e.message == 'Unsupported operation "bub".'
    }
}
