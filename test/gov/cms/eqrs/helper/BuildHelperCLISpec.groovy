package gov.cms.eqrs.helper

import spock.lang.Specification
import spock.lang.Unroll

class BuildHelperCLISpec extends Specification {
    def noop = {args -> }
    def pipelineMock = [:]
    def setup() {
        pipelineMock = [
            echo: noop,
            sh: noop,
            fileExists: noop,
            dir: {dir, closure -> closure()},
            git: noop
        ]
    }

    def "should initialize with defaults"() {
        when: 'instantiating an instance with defaults'
            def helper = new BuildHelperCLI(pipelineMock).init()
        then: 'all is well'
            helper != null
    }

    def "should error if compatibility check fails"() {
        given: 'failing compatibility check'
            def error = ''
            pipelineMock.sh = {arg -> throw new Exception()}
            pipelineMock.echo = {arg -> error = arg}
        when: 'instantiating an instance'
            def helper = new BuildHelperCLI(pipelineMock).init()
        then: 'we get a compatibility error'
            error == BuildHelperCLI.COMPATIBILITY_MESSAGE
            thrown Exception
    }

    @Unroll("should support #scenario.label")
    def "assemble commands"() {
        given: 'failing compatibility check'
            def helper = new BuildHelperCLI(pipelineMock).init()
            def command = helper.assembleArguments(scenario.arguments)
        expect: 'instantiating an instance'
            command.endsWith(scenario.command)
        where:
            scenario << [
                [
                    label: "single arguments",
                    arguments: [
                        single: 'one',
                        other: 2
                    ],
                    command: "--single one --other 2"
                ],
                [
                    label: "array arguments",
                    arguments: [
                        multi: ['one', 2]
                    ],
                    command: "--multi one --multi 2"
                ],[
                    label: "mixed arguments",
                    arguments: [
                        multi: ['one', 2],
                        single: 'three'
                    ],
                    command: "--multi one --multi 2 --single three"
                ]
            ]
    }
}
