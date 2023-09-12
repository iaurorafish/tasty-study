# 需求产生

本项目其实是B站一个Redis课程的企业应用实战练习，即[《黑马程序员Redis入门到实战教程》](https://www.bilibili.com/video/BV1cr4y1671t)。因为在家总是忍不住刷手机，难以静心学习，所以就产生了出门自习的念头。但大型图书馆都离家太远了，往返耗时都得要两三个小时，不太划算。所以就想创建一个服务于出门自习党的项目，即将该网课里的【黑马点评】项目中的商户，限制在与自习相关的商户中。其他功能与【黑马点评】等消费点评类应用无异，即大家可以通过该应用快速找到附近可以自习的地方，并通过写博客、写评论等方式与大家进行交流，分享经验。

# 名字由来

谈到学习的地方，忍不住就想起迅哥儿的《三味书屋》。因为本项目收纳的商户不仅有严格意义上适合学习的图书馆、自习室，还有西式快餐店、咖啡厅等有桌椅且服务员不赶人的地方，以及奶茶店、打印店、便利店等起辅助学习作用的地方，所以就取名为【百味书屋】好了，意译为【Tasty Study】。

# 项目介绍

因为是Redis课程的练习，所以项目重点放在Redis的企业应用上，利用Redis的不同数据结构缓存数据，起到快速响应操作、降低数据库压力等作用。但并不表示Redis是某操作的最优解，在企业实际运用中可能有更优的技术。

前端项目使用的是Nginx服务器，后端项目使用的SpringBoot框架。

# 功能实现

本项目并没有实现消费点评类应用的所有常见功能，即不是一个可以直接上线使用的成熟应用。仅仅实现了以下功能：

![](E:\MyProgramming\TastyStudy\功能实现.png)

注：图片来自《黑马程序员Redis入门到实战教程》课程的PPT

# 项目运行

## 创建数据库

1. 建库

   ```sql
   CREATE DATABASE tasty_study;
   USE tasty_study;
   ```

2. 利用tasty_study.sql文件导入表和数据（文件在项目的resource文件夹下）

## 导入后端项目

1. 用IDEA打开TastyStudy文件夹

2. 修改application.yaml文件中的mysql、redis配置信息

   * 驱动类：JDBC连接Mysql5需用com.mysql.jdbc.Driver，JDBC连接Mysql6需用com.mysql.cj.jdbc.Driver
   * MySQL用户名及密码
   * Redis的IP地址、密码

3. 修改com.config包下RedissonConfig类的Redis配置信息

4. 运行测试类的ShopServiceTest的testSaveShop2Redis方法、testSaveShop2RedisGeo方法，将商户数据缓存到Redis中

5. 在Redis中创建一个Stream类型的消息队列，名为stream.order

   ```
    xgroup create stream.order g1 0 MKSTREAM
   ```

6. 启动项目

7. 在浏览器访问：http://localhost:8081/shop-type/list ，如果可以看到数据则证明运行没有问题

## 运行前端项目

1. 在nginx文件夹所在目录下打开一个CMD窗口，输入命令

```
start nginx.exe
```

2. 打开浏览器，访问http://localhost:8080/ ，按F12（笔记本内置键盘按Fn+F12），选择手机模式

# 一些BUG

1. 商户的坐标是使用[经纬度查询定位网站](http://jingweidu.757dy.com/)特意查询的数据，但只精确到小数点后五位（可以的话还是精确到小数点后六位吧），因此GEO计算的距离与百度地图软件查到的距离有0~2.5公里的偏差
2. 图片大小最好限制在70KB以内，大一些的话，能成功上传到\imgs\blogs文件夹下，但没法写入到数据库中
3. 秒杀券异步下单抛出空指针，暂时还无法实现购买功能