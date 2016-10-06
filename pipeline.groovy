/**
 * Created by vbabin on 06.10.2016.
 */
node {
    def mvnHome
    stage('Preparation') { // for display purposes
        // Get some code from a GitHub repository
        git 'https://github.com/bsoft-biz/orders.git'
        // Get the Maven tool.
        // ** NOTE: This 'M3' Maven tool must be configured
        // **       in the global configuration.
        mvnHome = tool 'M3'
        bat 'echo %systemdrive%'
        configFileProvider([configFile(fileId: 'email.properties', targetLocation: 'src\\main\\resources\\email.properties')
                            , configFile(fileId: 'persistence.properties', targetLocation: 'src\\main\\resources\\persistence.properties')]) {
            // some block
        }
    }
    stage('Build') {
        // Run the maven build
        if (isUnix()) {
            sh "'${mvnHome}/bin/mvn' -Dmaven.test.failure.ignore clean package"
        } else {
            bat(/"${mvnHome}\bin\mvn" -Dmaven.test.failure.ignore clean package/)
        }
    }
    stage('Results') {
        junit '**/target/surefire-reports/TEST-*.xml'
        archive 'target/*.jar'
    }
}