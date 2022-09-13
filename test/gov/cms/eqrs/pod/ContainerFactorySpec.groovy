package gov.cms.eqrs.pod

import spock.lang.Specification

class ContainerFactorySpec extends Specification {  

    def "no labels specified"() {  
        when: 'invoked with no labels'
            def factory = new ContainerFactory()
            def containers = factory.getContainers()
        then: 'a single default specification'
            containers.containers == [[
                command: 'cat',
                ttyEnabled: true,
                workingDir: '/home/jenkins/agent',
                privileged: true,
                alwaysPullImage:true,
                name: 'npm-agent',
                image: 'nexus-cloud.hcqis.org:28456/core-agents/eqrs-node12-agent:latest'
            ]]
    }

    def "unsupported label specified"() {  
        when: 'invoked with unsupported label labels'
            def factory = new ContainerFactory()
            def containers = factory.getContainers(['meep'])
        then: 'the unsupported label is highlighted'
            containers.unsupported == ['meep']
    }

    def "using custom container defaults"() {  
        when: 'invoked with unsupported label labels'
            def factory = new ContainerFactory([meep: 'mawp'])
            def containers = factory.getContainers()
        then: 'a single default specification'
            containers.containers == [[
                name: 'npm-agent',
                image: 'nexus-cloud.hcqis.org:28456/core-agents/eqrs-node12-agent:latest',
                meep:'mawp'
            ]]
    }

    def "supported config overrides"() {
        when: 'invoked with overrides'
            def factory = new ContainerFactory()
            def props = [
                labels: ['mvn-agent', 'postgres'],
                overrides: [
                    postgres: [
                        ttyEnabled: false,
                        workingDir: '/home/jenkins/agent',
                        privileged: true, 
                        alwaysPullImage: false, 
                    ]
                ]
            ] 

            def meta = factory.getContainers(props)
        
        then: 'the overrides take effect'
            meta.containers[0] == [
                command: 'cat',
                ttyEnabled: true,
                workingDir: '/home/jenkins/agent',
                privileged: true,
                alwaysPullImage: true,
                name: 'mvn-agent',
                image: 'nexus-cloud.hcqis.org:28456/core-agents/eqrs-maven-agent-jdk11-py3:latest'
            ]
            meta.containers[1] == [
                ttyEnabled: false,
                workingDir: '/home/jenkins/agent',
                privileged: true,
                alwaysPullImage: false,
                name: 'postgres',
                image:'postgres:12'
            ]
    }
}
