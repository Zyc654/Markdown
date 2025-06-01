## 网络编程 

- 网络：信息传输、接受、共享的虚拟平台



![ipv6](./Socket.assets/ipv6.png)

- ipv4 一定可以转为 ipv6 反之不一定

![端口](./Socket.assets/端口.png)

![端口1](./Socket.assets/端口1.png)

- 一个 ip 地址可以对应 65536 个端口 ，故实际上一个电脑可以有很多“端口”，也即是端口复用

![远程服务器](./Socket.assets/远程服务器.png)

- 两个局域网之间的主机无法直接进行通信

## UDP

![UDP](./Socket.assets/UDP.png)

![UDP不可靠](./Socket.assets/UDP不可靠.png)

![UDPcando](./Socket.assets/UDPcando.png)

![max](./Socket.assets/max.png)

UDP 首部一共是 32 位（源端口+目的端口）+32 位（长度+校验和）=64 位，UDP 头部里的 “长度（Length）” 字段的单位就是 **字节**（octet），而不是**位**（bit）。

### UDP-API

#### API-DatagramSocket

接受和发送 UDP 的类、负责发送某个 UDP 包，或者接受 UDP 包、不同于 TCP，UDP并没有合并到 Socket API中 （没有服务器端也没有客服端，直接通信）

监听发送者信息

![DatagramSocket](./Socket.assets/DatagramSocket.png)

> 这里的端口只能用来监听，并不是说只会在这一个端口发送，-- 接受数据的端口； 监听某个 ip 某个端口



![DatagramSocketfun](./Socket.assets/DatagramSocketfun.png)

#### API-DatagramPacket

设置接受者信息

![DatagramPAcket.png](./Socket.assets/DatagramPAcket.png)

 ![DatagramPAcketfun.png](./Socket.assets/DatagramPAcketfun.png)

> 后面两个参数仅仅发送时有效，指定具体的接收者信息

![DatagramPAcketfun1.png](./Socket.assets/DatagramPAcketfun1.png)

![DatagramPAcketfun2.png](./Socket.assets/DatagramPAcketfun2.png)

![DatagramPAcketfun3.png](./Socket.assets/DatagramPAcketfun3.png)

### UDP 实例演示

![UDPshow](./Socket.assets/UDPshow.png)

##  TCP

![whatisTCP.png](./Socket.assets/whatisTCP.png)

![TCP can do.png](./Socket.assets/TCP%20can%20do.png)

### TCP API

![TCP-API1.png](./Socket.assets/TCP-API1.png)

- 分为serversocket 和 socket

![TCP-API2.png](./Socket.assets/TCP-API2.png)

- accept 是服务器独有的，可以设置阻塞时间

- socket 可以创建多个 进程连接，实现进程与进程之间的数据传输

### TCP 可靠性

- 连接可靠性
- 传输可靠性
- ![四次挥手.png](./Socket.assets/四次挥手.png)

`ACK`是回送命令，`SYN`发起连接命令 ，`FIN`断开连接命令

`msl` 时间收不到回复消息会重发

## UDP辅助TCP实现点到点

### UDP搜索IP与端口

![UDP search IP & PORT.png](./Socket.assets/UDP search IP & PORT.png)

![04.png](./Socket.assets/04.png)

![05.png](./Socket.assets/05.png)

![06.png](./Socket.assets/06.png)

## 聊天室

因为文件过多存在跨文件夹 需要打包 .jar 才能执行   **后续自己学习**

![07.png](./Socket.assets/07.png)

![07-1.png](./Socket.assets/07-1.png)

一个客户端发送给另外几个客户端，服务器作为中转，群聊。

![chatroom.png](./Socket.assets/chatroom.png)

双通一个客户端需要两个线程，服务器资源回收，创建的主线程，监听客户端来源，发送之后的转发不阻塞客户端接收 ，最少是 2 * n + 4 个线程

1000多线程会消耗 100m 左右内存，

CPU：取决于数据的频繁性、数据的转发复杂性

内存：取决于客户端的数量、客户端发送的数据大小

线程：取决于连接的数量

- 优化

减少线程的数量，接收的监听必不可少，但是转发的线程池可以复用，转发使用线程池的批量转发(阻塞的情况下)

增加线程执行繁忙状态

客户端 Buffer 复用机制

## NIO

### 阻塞 IO 与非阻塞 IO

![非阻塞IO.png](./Socket.assets/非阻塞IO.png)

### NIO Family

![NIOFamily1.png](./Socket.assets/NIOFamily1.png)

![NIO-Buffer.png](./Socket.assets/NIO-Buffer.png)

![NIO-Cannel.png](./Socket.assets/NIO-Cannel.png)

 ![Selector.png](./Socket.assets/Selector.png)

![Selector1.png](./Socket.assets/Selector1.png)

Selector 是一个抽象类，有很多子类，半观察者模式不是完全观察者，事件注册，但是事件到达时不会直接回送，需要遍历池子来判断事件是否到达，select/selectNow 阻塞到有时间到达的时候，返回的是集合



![Selector2.png](./Socket.assets/Selector2.png)

返回的是selectdKeys()，通过返回值判断有什么事件到达，wakeup唤醒，返回值是个数，判断是否需要关闭。

![Selector3.png](./Socket.assets/Selector3.png)

FileChannel 仅仅表示可以使用块状的方式操作文件，一整块进行操作，和磁盘形成块状，但是不能说“文件什么时候可读，什么时候可写”，不能切换为非阻塞模式

![SelectionKey.png](./Socket.assets/SelectionKey.png)

所有集合，当前就绪集合，obj 附加值可以设置可以使用的时候通知，当可用时 obj 携带回来需要发送的数据

![ChanneltoBuffer.png](./Socket.assets/ChanneltoBuffer.png)

一个Channel可以输出数据到多个 Buffer 中，同理 Buffer 到 Channel 也是一样



- 监听客户端到达
- 接收、回送客户端数据
- 转发客户端数据到另一个客户端
- 多客户端的消息处理、

![NIO职责.png](./Socket.assets/NIO职责.png)

### NIO 优化 Thread

![NIOThread.png](./Socket.assets/NIOThread.png)

### NIO 优化的好处

Java NIO（New I/O）提供了一种高效的 I/O 操作方式，与传统的阻塞 I/O（BIO）相比，NIO 的优化带来了多方面的好处：

#### 1. **高并发处理能力**

- **非阻塞 I/O**：NIO 允许通道（Channel）在非阻塞模式下工作，这意味着线程可以在等待 I/O 操作完成时执行其他任务。这样，单个线程可以管理多个连接，极大地提高了系统的并发处理能力。
  
- **多路复用**：通过 `Selector`，NIO 可以在单个线程中监视多个通道的 I/O 事件。多路复用机制减少了线程的数量和上下文切换的开销，使得系统在高并发场景下表现更佳。

#### 2. **资源利用效率**

- **减少线程开销**：传统的阻塞 I/O 模型通常需要为每个连接创建一个线程，而 NIO 通过非阻塞 I/O 和多路复用机制，减少了对线程的需求，从而降低了线程创建和管理的开销。

- **更好的内存管理**：NIO 提供了 `ByteBuffer`，允许直接在内存中进行数据操作，减少了数据复制的次数，提高了内存使用效率。

#### 3. **灵活性和可扩展性**

- **灵活的缓冲区管理**：NIO 的 `ByteBuffer` 提供了灵活的缓冲区管理机制，支持多种数据类型的读写操作，并允许开发者根据需要调整缓冲区的大小。

- **可扩展的架构**：NIO 的设计使得应用程序可以轻松扩展以支持更多的连接和更高的吞吐量。通过调整选择器和线程池的配置，系统可以根据负载动态扩展。

#### 4. **性能优化**

- **减少 I/O 阻塞**：NIO 的非阻塞特性减少了 I/O 操作的阻塞时间，使得系统可以更高效地处理 I/O 密集型任务。

- **提高吞吐量**：通过减少线程切换和 I/O 阻塞，NIO 提高了系统的整体吞吐量，使得应用程序能够处理更多的请求。

#### 5. **适用于多种协议**

- **支持多种协议**：NIO 的灵活性使其适用于多种网络协议（如 TCP、UDP），并且可以轻松实现自定义协议。

#### 6. **异步 I/O 支持**

- **异步 I/O 操作**：NIO 提供了异步 I/O 操作的支持，使得应用程序可以在不阻塞线程的情况下执行 I/O 操作，进一步提高了系统的响应性和性能。

综上所述，Java NIO 的优化带来了显著的性能提升和资源利用效率，使得其成为高并发和 I/O 密集型应用的理想选择。在面试中，面试官可能会询问 NIO 的具体实现和优化策略，考察候选人对 NIO 的理解和应用能力。

### NIO 知识点

![NIOvsIO.png](./Socket.assets/NIOvsIO.png)

![阻塞IO处理数据.png](./Socket.assets/阻塞IO处理数据.png)

![NIO线程处理数据.png](./Socket.assets/NIO线程处理数据.png)

好处在于处理仅在于 channel 有数据的时候，没有数据的时候 selector 没有返回，不会进行后续过程，避免的后续阻塞，所有的阻塞都是依赖于 selector

![阻塞IO处理多客户端连接.png](./Socket.assets/阻塞IO处理多客户端连接.png)

![NIO处理多客户端连接.png](./Socket.assets/NIO处理多客户端连接.png)

## 数据传输稳定性优化

### 消息粘包

TCP 本质上并不会发送数据层面的粘包

TCP的发送方与接收方一定会确保数据是以一种有序的方式到达客户端

并且会确保数据包完整



UDP不保证消息完整性，所以 UDP 往往发生丢包等情况

TCP数据传输具有：顺序性，完整性

在常规所说 Socket **“粘包”**，并非数据传输层面粘包，而是业务层面粘包

**粘包** 是数据处理的逻辑层面上发生的粘包，包括 TCP、UDP甚至其他任意的数据流交互方案（Mina、Netty 等框架从根本来说也是为了解决粘包而设计的高并发库

![消息粘包.png](./Socket.assets/消息粘包.png)

### 消息不完整

从数据的传输层面来讲 TCP 也不会发生数据发生不全等情况

一旦出现一定是 TCP 停止运行终止之时

“数据不完整”  依然针对的是数据的逻辑接受层面

**在物理层面** 来讲数据一定是能安全的完整的到达另一端，但是另一端可能缓冲区不够或者数据处理上不够完整导致数据只能**读取一部分数据**  这种情况被称为 “数据不完整”，“数据丢包” 等

![消息不完整.png](./Socket.assets/消息不完整.png)

### 复现消息传输错误

![复现消息传输错误.png](./Socket.assets/复现消息传输错误.png)

重复触发问题主要在于 lib-clink/impl/ioselectprovider中的是否取消已经执行过的KeyOps的监听



消息粘包主要是Client 发送多条消息的时候可能会被当成一条消息接收，文件传输或者复杂消息传输的时候会出现大问题



单消息不完整问题 lib-clink/core中的 IoArgs 中将 byteBuffer 缓冲区缩小即可复现

### 有序的混传数据

![如何有序的混传数据.png](./Socket.assets/如何有序的混传数据.png)

开始结束标记方法都会影响性能，因为不知道当前是否读完，所以需要对每一个字节都进行校验

![起止标记技术实现.png](./Socket.assets/起止标记技术实现.png)

\n \0 分别表示结束和开始

![固定头部技术实现.png](./Socket.assets/固定头部技术实现.png)

### HTTP

HTTP 如何识别一个请求？

HTTP 如何读取请求头，请求头协议是怎么样的？

HTTP如何接受数据包体？

当数据是文件时，HTTP如何判断文件已经接受到底了？

![HTTP1.X.png](./Socket.assets/HTTP1.X.png)

Global header 总的长度

Packet header 头部部分

![Http1.x1.png](./Socket.assets/Http1.x1.png) 

![Http2.x.png](./Socket.assets/Http2.x.png)

HTTP 2 可能要经过一些握手和安全校验，且有了**帧**的概念，按帧分开， --> 分包的概念实现

![HTTP2.xConnection.png](./Socket.assets/HTTP2.xConnection.png)

HTTP2 可以通过一个 Socket 连接 ，发送不同的头的来连接，通过识别不同的头来返回不同的信息

![HTTP2Header.png](./Socket.assets/HTTP2Header.png)

IoArgs 是用于将数据丢到网络层的概念，数据传输是通过 IoArgs 完成的，但是保证数据完整是通过外层实现的，定义在 core/Packet、sendPacket reveivePacket 中



### 文件传输

![文件传输.png](./Socket.assets/文件传输.png)

PAcket put 到 queue 中 后 take 拿出来一个，写入到 IoArgs 中，进行注册，sender，注册想要发送数据，sender异步发送，sender经过 selector事件机制的回调之后，将 IoArgs 中的数据进行真实的发送，发送之后进行回调(onCompleted)，判断是否发送成功，listenter ，如果还未发送成功会再次写入注册循环，直至 packet被真实的发送完成

包头和包体的概念，首先会提取 Packet 中的数据长度和数据类型，然后将数据长度和数据类型在第一个包的最前面写入到 IoArgs 中，先将其发送出去，然后才发送真实数据



### 帧 Frame 数据结构

![帧的数据结构1.png](./Socket.assets/帧Frame数据结构1.png)

取长度时只需要取出 低 2 字节即可，但是拼接的时候会出现问题，会自动将前面高位变为1，故需要 & 0xFF

```java
 // 00000000 00000000 00000000 01000000
header[0] = (byte) (length >> 8);// 将长度右移8位,这里仅仅提取后两个字节
header[1] = (byte) (length);// 设置长度
header[2] = type;// 设置类型
header[3] = flag;// 设置标志
header[4] = (byte) identifier;// 设置标识符
header[5] = 0;// 设置保留字节
// 00000000
// 01000000
// 00000000 00000000 00000000 01000000
// 01000000   直接拼接前面的位会变为1，所以需要&0xFF (自动补齐前面为 1)
// 11111111 11111111 11111111 01000000  
// 00000000 00000000 00000000 11111111 0xFF
// 00000000 00000000 00000000 01000000
return ((((int) header[0]) & 0xFF) << 8) | (((int) header[1]) & 0xFF);// 返回Body的长度
```

- volatile 保证可见性，保证线程之间的可见性，保证线程之间的同步
- synchronized 保证线程安全，保证线程之间的同步，同一时间仅允许一个进行

core/frame.java

frames/AbsSendFrame.java

core/IoArgs.java 133h remaind()、112 finishWriting()、18 readFrom  32 writeTo



































