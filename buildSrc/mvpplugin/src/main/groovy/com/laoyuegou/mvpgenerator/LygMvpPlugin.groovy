package com.laoyuegou.mvpgenerator

import org.gradle.api.Project
import org.gradle.api.Plugin

class LygMvpPlugin implements Plugin<Project> {
    void apply(Project target){
        target.extensions.create("LygMvp", LygMvpExtension)

        target.task('generateMvp', type: LygMvpTask){
            group "mvpGenerator"
        }
    }
}