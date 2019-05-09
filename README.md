EasySocket
==============

一个简单易上手的Socket框架，通过简单的配置调用就能实现连接，并带有自动重连功能和心跳包的功能。

```java

    EasySocket.Builder builder = new EasySocket.Builder();
                 socket = builder.setIp("10.10.201.52")
                        .setPort(30000)
                         .setNeedHeart(true)//默认为false
                         .setMaxHeartTime(5000)//默认为5000
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
                }).build();

                socket.connect();


```

源码内带有测试服务器和一个示例App


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


