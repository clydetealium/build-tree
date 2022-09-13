def call(maskMap, closure) {
    def passwordPairs = []
    def envAssignments = []

    maskMap.each {label, secret -> 
        passwordPairs.add([password: secret.toString(), var: label])
        envAssignments.add(label + "=" + secret)
    }

    wrap([$class: 'MaskPasswordsBuildWrapper', varPasswordPairs: passwordPairs]) {
        withEnv(envAssignments, closure)
    }
}