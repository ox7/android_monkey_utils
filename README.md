# android_monkey_utils

一个monkey+instrumentaion构成的自动化monkey测试工具

---

## **Keyword**

- monkey
- instrument

---

#### **Monkey简介**
  
> The Monkey is a program that runs on your emulator or device and generates pseudo-random streams of user events such as clicks, touches, or gestures, as well as a number of system-level events. You can use the Monkey to stress-test applications that you are developing, in a random yet repeatable manner.
  
通常来说，Monkey我们一般用于测试APP的稳定性。至于用法可详见Google的Monkey文档: [UI/Application Exerciser Monkey](http://developer.android.com/tools/help/monkey.html)

#### **Monkey分析**

废话不多说，言简意赅的总结一下（时间限制，不细说Monkey整体类结构等，只简单阐述关键点）：

通过分析Monkey源码，Monkey的事务流程如下：  

![Monkey_Framework](http://img2.tbcdn.cn/L1/461/1/fc6267481b1fd53ed507a7652c0a0a988f1d2b00)

- 伪随机事件生成（包括点击、拖拽、缩放、键盘Menu等）
- 伪随机事件注入
- 目标APP行为监控

因为Monkey是一个拥有系统级权限的命令行工具，所以其伪随机事件注入和APP行为事件监控是调用系统的内部隐藏api实现的。非系统级权限和Root权限无法正常访问。

注入部分逻辑分为两个阶段，早期版本使用系统

```java
android.view.IWindowManager;
android.app.IActivityManager;
```
两个内部类实现事件的注入。后期Android系统的

```java
android.hardware.input.InputManager;
```
地位大幅提升，统一接管了事件注入等一系列输入逻辑。


APP监控是通过内部类  
```java
android.app.ActivityManagerNative;
android.app.IActivityController;
```
来实现页面跳转、Crash、ANR等一系列事件的监控。


#### **Monkey特点**

- 独立进程运行与目标APP完全隔离
- 通过系统内部api，调用注入事件与监听Log服务
- 最小化影响目标APP运行状况
- 命令行运行，不需要root权限

#### **android_monkey_utils方案**

基于Monkey这些特点，Monkey整体的改造方案选择Instrumentation + Monkey。

简单的讲，保留Monkey的伪随机事件生成层，将注入层改为基于Android Instrumentation框架实现事件的注入。

在保留Monkey大部分优点的同时，将Monkey封装成一个Android Instrumentation test case，脱离命令行，可以包装进一个Apk，实现手机端自主运行。

一些特殊注意点：
- 项目打包的测试apk，必须与目标待测试apk保持签名一致，同debug，或同release签名。
- 目前暂时关闭了事件回放（在处理几十万、百万级事件回放时需要更好的方案，待之后版本优化）
- 目前暂时关闭事件Crash截屏

#### **Monkey Apk使用**

- IDE直接以Android JUnit Test运行，此模式可以Debug等
- 打包成Apk安装到手机运行，此模式不同的手机第一次启动时会有卡顿，稍等片刻即可。



### License
```java
/*
 * Copyright (C) 2015 lymph team <lymphteam@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
```
