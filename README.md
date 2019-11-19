# framedemo


需要在使用buildSrc 插件的module下创建一个mvpGenerator.properties文件

# 里面填充内容位：

* applicationId=com.example
* isLibrarys=false 
* author=dongxl
* isViewActivity=false
* packageName=homepage
* functionName=GameType

* 说明：applicationId： 所在module 包名 如：com.example 
     isLibrarys： 是否在module下创建 ture module下创建 暂时用不到 所有情况都写false
     author： 自己
     isViewActivity： 创建是否Activity true Activity false Fragment
     packageName： 生成文件位置：子包名 是跟在module 包名后面的 如：填写test 生成最后的位置是com.example.test
     functionName： 生成文件的位置 如果需要生成TestActivity 只需要 添加Test就行 fragment 同理
     
     所有值后面不能有多余的空格