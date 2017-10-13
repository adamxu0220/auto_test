//hard code: only check fold nuttx&apps, and suppose nuttx_apps always build zdk branch
//get_pure_sourcecode(GERRIT_HOST, GERRIT_PORT, GERRIT_BRANCH)
//get_pure_sourcecode(GERRIT_HOST, GERRIT_PORT, GERRIT_BRANCH)
def get_pure_sourcecode(GERRIT_HOST, GERRIT_PORT, GERRIT_BRANCH) {
        withEnv(['GERRIT_HOST='+GERRIT_HOST, 'GERRIT_PORT='+GERRIT_PORT]){
                sh '''
                if [ ! -d "nuttx" ]; then
                        git clone ssh://haizhou@"${GERRIT_HOST}":"${GERRIT_PORT}"/nuttx
                fi
                if [ ! -d "apps" ]; then
                        git clone ssh://haizhou@"${GERRIT_HOST}":"${GERRIT_PORT}"/nuttx_apps apps
                fi
                '''
        }

        dir("nuttx"){
                sh "git checkout master"
                sh "git fetch"
                sh "git reset --hard origin/master"
                sh "git checkout "+GERRIT_BRANCH
                sh "git reset --hard origin/"+GERRIT_BRANCH
                sh "git rebase"
        }
        dir("apps"){
                sh '''
                git checkout master
                git fetch
                git reset --hard origin/master
                git checkout zdk
                git reset --hard origin/zdk
                git rebase
                '''
        }
}

def cherry_pick(GERRIT_HOST, GERRIT_PORT, GERRIT_PROJECT, GERRIT_REFSPEC, work_dir){
        dir(work_dir){
                String git_command = String.format("git fetch ssh://haizhou@%s:%s/%s %s && git cherry-pick FETCH_HEAD", GERRIT_HOST, GERRIT_PORT, GERRIT_PROJECT, GERRIT_REFSPEC)
                sh git_command
        }
}

return this
