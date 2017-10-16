def Build(){
    sh '''
        pwd
        cd apps
        git checkout zdk
        cd ../nuttx
        git checkout "${GERRIT_BRANCH}"
        git branch
        
        cd configs/nrf52832_dk
        make clean -j2
        make nsh -j2 
        make clean -j2
        make ble_app_uart -j2
        make clean -j2
    '''
}
node('build_server') {
    
    def res_stage_get_pure_sourcecode = true
    def res_stage_get_CL = true
    def res_stage_build = true
    checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[url: 'ssh://haizhou@101.132.142.37:30149/auto_test']]]) 
    def common_api = load 'pipeline/common_api.groovy'
    def git_func = load 'pipeline/git_func.groovy'    
    try {
        stage('get_pure_sourcecode'){
            git_func.get_pure_sourcecode(GERRIT_HOST, GERRIT_PORT, GERRIT_BRANCH)
        }
    }
    catch (exc) {
        echo exc.toString()
        res_stage_get_pure_sourcecode = false 
    }
   
    
    try {
        stage('get_CL'){
            if (res_stage_get_pure_sourcecode) {
                git_func.cherry_pick(GERRIT_HOST, GERRIT_PORT, GERRIT_PROJECT, GERRIT_REFSPEC, (GERRIT_PROJECT.equals("nuttx_apps") ? "apps":GERRIT_PROJECT))
            }
        }
    }
    catch (exc) {
        echo exc.toString()
        res_stage_get_CL = false  
    }  
   
    
    try {
        stage('Build'){
            if (res_stage_get_pure_sourcecode && res_stage_get_CL){
                Build()
            }
        }
    }
    catch (exc) {
        echo exc.toString()
        res_stage_build = false
    }
    
    stage('GerritReviewer'){
        common_api.jenkins_reviewer_merge(GERRIT_HOST, GERRIT_PORT, GERRIT_CHANGE_NUMBER, GERRIT_PATCHSET_NUMBER, res_stage_get_pure_sourcecode && res_stage_get_CL && res_stage_build)
    }
    if (! (res_stage_get_pure_sourcecode && res_stage_get_CL && res_stage_build)){
        currentBuild.result = 'FAILURE'
    }
}
