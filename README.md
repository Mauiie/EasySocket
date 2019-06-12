EasySocket
==============

Android端简单易用的socket框架`EasySocket`。一个简单易上手的Socket框架，通过简单的配置调用就能实现连接，并带有自动重连功能和心跳包的功能。

**jar下载**：[![](https://jitpack.io/v/ruihanL/EasySocket.svg)](https://jitpack.io/#ruihanL/EasySocket)



如何引用
==============
Gradle
------
在工程添加库
```java

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}


```
再添加依赖
```java

	dependencies {
	        implementation 'com.github.ruihanL:EasySocket:1.1'
	}


```


使用举例：
------

直接创建实例然后调用connect即可连上服务器。

基本用法如下。
主要注意配置网络权限，还好收到信息的回调在异步线程，如需显示到UI，请把数据发送到主线程中。

```java


        EasySocket.Builder builder = new EasySocket.Builder();
        EasySocket socket = builder.setIp("10.10.201.52")
                .setPort(30000)
                .setCallback(new Callback() {
                    @Override
                    public void onConnected() {

                    }

                    @Override
                    public void onDisconnected() {

                    }

                    @Override
                    public void onReconnected() {

                    }

                    @Override
                    public void onSend() {

                    }

                    @Override
                    public void onReceived(byte[] msg) {
                        String text = new String(msg);
                        show(text);
                    }

                    @Override
                    public void onError(String msg) {

                    }

                    @Override
                    public void onSendHeart() {

                    }
                }).build();

        socket.connect();

```

心跳功能
-----
开启心跳包功能后，会隔设定的时间向服务器发送指定的心跳包，并等待服务器返回应答，若服务器没有再规定的时间内应答，将会断开连接重新连接。这种情况适用于比较严格的场景。

```java

		EasySocket.Builder builder = new EasySocket.Builder();
        builder.setIp("10.10.201.52")
                .setPort(30000)
                /**
                 * 默认不开启心跳。
                 * 当此项为true时，下面的参数才有效。
                 */
                .setNeedHeart(true)
                /**
                 * 默认心跳包为0xA.
                 */
                .setHeartPackage(new byte[]{0xA})
                /**
                 * 客户端发送心跳包间隔默认10秒
                 */
                .setHeartInterval(10000)
                /**
                 * 客户端等待服务器回应心跳包默认5秒，
                 * 此数值需要比心跳发送间隔小。
                 */
                .setMaxSeverResponseHeartOutTime(5000)


```


其他
---


源码内带有`测试服务器`和一个`示例App`。


License
=======

    Copyright 2012 Linrh
    Copyright 2011 The Android Open Source Project

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.


