基于hadoop搭建的云盘==>CLOUD

主要完成的功能有：登录，注册，退出登录，上传文件，新建文件夹，搜索文件，显示hadoop的详细信息，修改文件名，删除文件或者目录，下载文件或者目录，返回上一级，返回根目录

------

相关技术：

后端主要采用springboot+jpa，登录采用了spring下的security做了登录验证

数据库采用了mysql和redis，redis主要用于保存会话状态

文件存储采用了由Apache基金会开发的分布式系统基础架构 hadoop

前端采用了bootstrap+vue

------

ps:arrow_right:如需下载运行，需要自定义错误码(成功码为6000，错误码以7000开始)和异常以及写好Result类，还需在pom中把下面这个依赖给删掉

[或者在这个项目中](https://github.com/ErfengV/comehome)把这个依赖下载下来也行

![3](https://github.com/ErfengV/CLOUD/blob/main/pic/3.png)

相关数据库文件在sql目录里，在yml配置中改成自己本地的就可以了

还需记得搭建自己的hadoop，[要是不知道怎么搭建的小伙伴可以来这里](https://blog.csdn.net/Er_fengV)

（要是运行成功了，别忘了点个小星星鸭>,<）

------

页面展示：

（登录页面：）

<img src="https://github.com/ErfengV/CLOUD/blob/main/pic/1.png" alt="1" style="zoom:50%;" />

（注册页面：）



<img src="https://github.com/ErfengV/CLOUD/blob/main/pic/1623312934324.png" alt="1623312934324" style="zoom:50%;" />

（首页：）

![2](\pic\2.png)



（hdfs详细信息的页面(有点小简陋)：）

<img src="https://github.com/ErfengV/CLOUD/blob/main/pic/1623313023510.png" alt="1623313023510" style="zoom:50%;" />
