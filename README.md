# Xposed-Unshell
## 简介
一代壳脱壳Xposed模块。
## 原理
* __一代壳定义__：对dex文件整体进行加密，运行时还原dex文件，然后进行加载和运行。
* __脱壳过程__：使用Xposed在com.android.dex.Dex类的构造函数`Dex(ByteBuffer data){...}`处下after hook，取出Dex类中的data属性，并保存到被hook应用的私有目录中。
* __脱壳原理__：Dex类中存有dex文件数据，应用在运行时会实例化该类，可参考Android源码，源码位置：libcore/dex/src/main/java/com/android/dex/Dex.java
* __具体实现__：请阅读[hook代码](https://github.com/davidblus/Xposed-Unshell/blob/master/src/main/java/com/example/xposedunshell/XposedUnshell.java#L26)，思路和实现都很简单。

## 测试
Android 5.0.2 -- API 21 系统中测试通过。