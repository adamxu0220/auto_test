import groovy.json.JsonSlurper;
def __jenkins_reviewer(GERRIT_HOST, GERRIT_PORT, GERRIT_CHANGE_NUMBER, GERRIT_PATCHSET_NUMBER, result){
        def reason;
        if (result.contentEquals("-1") || result.contentEquals("-2")) {
                reason = "Auto Build&Test failed; ref:"+BUILD_URL+"/console"
        }else if (result.contentEquals("+1") || result.contentEquals("+2")) {
                reason = "Auto Build&Test passed; ref:"+BUILD_URL+"/console"
        }
        else {
                error String.format("invalid result value:%s found; only -1,-2,+1,+2 valied", result)
        }
        String review_cmd_str = String.format(
                        "ssh -p %s -i ~/sh-gerrit-build-reviewer sh-gerrit-build-reviewer@%s  gerrit review %s,%s  --code-review %s -m '%s'",
                        GERRIT_PORT,
                        GERRIT_HOST,
                        GERRIT_CHANGE_NUMBER,
                        GERRIT_PATCHSET_NUMBER,
                        result,
                        reason)
        review_cmd_str.execute()
}
        
def jenkins_reviewer_pretest_passed(GERRIT_HOST, GERRIT_PORT, GERRIT_CHANGE_NUMBER, GERRIT_PATCHSET_NUMBER){
        __jenkins_reviewer(GERRIT_HOST, GERRIT_PORT, GERRIT_CHANGE_NUMBER, GERRIT_PATCHSET_NUMBER, "+1")
}
def jenkins_reviewer_pretest_failed(GERRIT_HOST, GERRIT_PORT, GERRIT_CHANGE_NUMBER, GERRIT_PATCHSET_NUMBER){
        __jenkins_reviewer(GERRIT_HOST, GERRIT_PORT, GERRIT_CHANGE_NUMBER, GERRIT_PATCHSET_NUMBER, "-1")
}
def jenkins_reviewer_pretest(GERRIT_HOST, GERRIT_PORT, GERRIT_CHANGE_NUMBER, GERRIT_PATCHSET_NUMBER, fg_pass){
        __jenkins_reviewer(GERRIT_HOST, GERRIT_PORT, GERRIT_CHANGE_NUMBER, GERRIT_PATCHSET_NUMBER, (fg_pass ? "+1":"-1"))
}
def jenkins_reviewer_merge(GERRIT_HOST, GERRIT_PORT, GERRIT_CHANGE_NUMBER, GERRIT_PATCHSET_NUMBER, fg_pass){
        __jenkins_reviewer(GERRIT_HOST, GERRIT_PORT, GERRIT_CHANGE_NUMBER, GERRIT_PATCHSET_NUMBER, (fg_pass ? "+2":"-2"))
}
def get_CLs_detail(CLSets, GERRIT_PROJECT_list, GERRIT_CHANGE_NUMBER_list, GERRIT_PATCHSET_NUMBER_list, GERRIT_CHANGE_OWNER_EMAIL_list) {

    int total_selected;//Integer.getInt(json_test.getJSONObject("total").toString());
    def jsonSlurper = new JsonSlurper()
    def jsonObject = jsonSlurper.parseText(CLSets)
    jsonSlurper = null
    total_selected = 0
    jsonObject.GERRIT_PROJECT.each{GERRIT_PROJECT_list[total_selected]=it;total_selected++}
    total_selected = 0
    jsonObject.GERRIT_CHANGE_NUMBER.each{GERRIT_CHANGE_NUMBER_list[total_selected]=it;total_selected++}
    total_selected = 0
    jsonObject.GERRIT_PATCHSET_NUMBER.each{GERRIT_PATCHSET_NUMBER_list[total_selected]=it;total_selected++}
    total_selected = 0
    jsonObject.GERRIT_CHANGE_OWNER_EMAIL.each{GERRIT_CHANGE_OWNER_EMAIL_list[total_selected]=it;total_selected++}

    return total_selected;
}

return this

