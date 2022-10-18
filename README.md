# Easytool
> Easytool的目标是干掉大部分冗余的复杂代码，从而最大限度的避免“复制粘贴”代码的问题，彻底改变我们写代码的方式。

## 简介
```
Easytool是一个小型的Java工具类库，封装了一些常用的通用的方法，降低相关API的学习成本，提高工作效率，使Java拥有函数式语言般的优雅。

Easytool中的大部分方法来自开发过程中的真实需求，它既是大型项目开发中解决小问题的利器，也是小型项目中的效率担当。
```

## 模块
|       名称       |                     介绍                     |
| :--------------: | :------------------------------------------: |
|   easytool-all   |              包含所有模块的引用              |
|  easytool-core   |   核心包，包括对集合处理、日期、各种Util等   |
| easytool-crypto  | 加密解密模块，提供对称、非对称和摘要算法封装 |
| easytool-process |        基于spring封装的任务编排小框架        |
可以根据需求对每个模块单独引入，也可以通过引入easytool-all方式引入所有模块。

## 使用
在项目的pom.xml的dependencies中加入以下内容:
```
<dependency>
    <groupId>cc.jinhx</groupId>
    <artifactId>easytool-all</artifactId>
    <version>自行查看最新版本</version>
</dependency>
```

## 参考文档
<b><a href="https://jinhx.cc/article/1582293625641893888"> 中文文档 </a></b>

## 关于源码
<b><a href="https://github.com/jinhx128/easytool"> https://github.com/jinhx128/easytool </a></b>

| 分支  |                                   作用                                   |
| :---: | :----------------------------------------------------------------------: |
| main  | 主分支，release版本使用的分支，与中央库提交的jar一致，不接收任何pr或修改 |
|  dev  |           开发分支，默认为下个版本的SNAPSHOT版本，接受修改或pr           |

## 贡献代码
1. 在github上fork项目到自己的repo
2. 把fork过去的项目也就是你的项目clone到你的本地
3. 修改代码（记得一定要修改dev分支）
4. commit后push到自己的库（dev分支）
5. 登录github在你首页可以看到一个pull request按钮，点击它，填写一些说明信息，然后提交即可
6. 等待维护者合并

## bug反馈或建议
<b><a href="https://github.com/jinhx128/easytool/issues"> https://github.com/jinhx128/easytool/issues </a></b>

提交问题反馈请说明正在使用的JDK版本、Easytool版本和相关依赖库版本。

## 关于作者
【<b>个人博客</b>】    【<b><a href="https://jinhx.cc"> https://jinhx.cc </a></b>】