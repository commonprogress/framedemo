package com.laoyuegou.mvpgenerator

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import java.text.SimpleDateFormat

class LygMvpTask extends DefaultTask {

    @TaskAction
    def generateMvpFile() {
        def lygMvpExtension = project.extensions.getByType(LygMvpExtension)
        //是否Librarys下创建类
        def isLibrarys = lygMvpExtension.isLibrarys
        //应用ID
        def applicationId = lygMvpExtension.applicationId;
        //包名
        def packageName = lygMvpExtension.packageName
        //功能名
        def functionName = lygMvpExtension.functionName
        //作者
        def author = lygMvpExtension.author
        //view是activity 还是 fragment
        def isViewActivity = lygMvpExtension.isViewActivity

        def mvpArray = [
                [
                        templateName: "MvpContract.template",
                        type        : "contract",
                        fileName    : "Contract.java"
                ],
                [
                        templateName: "MvpPresenter.template",
                        type        : "presenter",
                        fileName    : "Presenter.java"
                ],
                [
                        templateName: "MvpLayout.template",
                        type        : "laoyout",
                        fileName    : ".xml"
                ]
        ]

        if (isViewActivity) {
            mvpArray.add([
                    templateName: "MvpActivity.template",
                    type        : "activity",
                    fileName    : "Activity.java"
            ])
        } else {
            mvpArray.add([
                    templateName: "MvpFragment.template",
                    type        : "fragment",
                    fileName    : "Fragment.java"
            ])
        }

        String dateString = getFormatTime();

        def mBinding = [
                applicaitionId: applicationId,
                packageName   : packageName,
                functionName  : functionName,
                layoutName    : getToLowerCase(functionName),
                date          : dateString,
                author        : author
        ];

        def packageFilePath = lygMvpExtension.applicationId.replace(".", "/");

        //代码文件根路径
        def fullPath = project.projectDir.toString() + "/src/main/java/" + packageFilePath

        generateMvpFile(mvpArray, mBinding, isLibrarys, fullPath, isViewActivity)

    }

    void generateMvpFile(def mvpArray, def binding, def isLibrarys, def fullPath, def isViewActivity) {

        for (int i = 0; i < mvpArray.size(); i++) {
            preGenerateFile(mvpArray[i], binding, isLibrarys, fullPath, isViewActivity)
        }
    }

    void preGenerateFile(def map, def binding, def isLibrarys, def fullPath, def isViewActivity) {
        // File mvpContractTemplateFile = new File("template/" + map.templateFileName)

//        println "preGenerateFile : map.templateName=" + map.templateName
//        println "preGenerateFile : map.type=" + map.type
//        println "preGenerateFile : map.fileName=" + map.fileName
        def template = makeTemplate(isLibrarys, map.templateName, binding);
        def path;
        def fileName;
        if ("laoyout".equals(map.type)) {
            path = project.projectDir.toString() + "/src/main/res/layout/";
            if (isViewActivity) {
                fileName = path + "/activity_" + binding.layoutName + map.fileName;
            } else {
                fileName = path + "/fragment_" + binding.layoutName + map.fileName;
            }
        } else {
            path = fullPath + "/" + binding.packageName + "/" + map.type;
            fileName = path + "/" + binding.functionName + map.fileName
        }
        generateFile(path, fileName, template)
    }

/**
 * 加载模板
 */
    def makeTemplate(def isLibrarys, def templateName, def binding) {
        File f = null;
        if (isLibrarys) {
            f = new File("../../buildSrc/mvpplugin/template/" + templateName);
        } else {
            f = new File("../buildSrc/mvpplugin/template/" + templateName);
        }

        def engine = new groovy.text.GStringTemplateEngine()

        return engine.createTemplate(f).make(binding)
    }

/**
 * 生成文件
 * @param path
 * @param fileName
 * @param template
 */
    void generateFile(def path, def fileName, def template) {
        //验证文件路径，没有则创建
        validatePath(path);

        File mvpFile = new File(fileName);

        //如果文件已经存在，直接返回
        if (!mvpFile.exists()) {
            mvpFile.createNewFile()
        } else {
            return;
        }

        FileOutputStream out = new FileOutputStream(mvpFile, false)
        out.write(template.toString().getBytes("utf-8"))
        out.close()
    }

/**
 * 验证文件路径，没有则创建
 * @param path
 */
    void validatePath(def path) {
        File mvpFileDir = new File(path);

        if (!mvpFileDir.exists()) {
            mvpFileDir.mkdirs()
        }
    }

    def getToLowerCase(def fileName) {
        return fileName.toLowerCase();
    }

/**
 * 格式化当前时间
 * @return
 */
    def getFormatTime() {
        Date date = new Date();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(date);
    }

}