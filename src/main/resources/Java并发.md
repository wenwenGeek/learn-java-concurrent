# java并发

## 用户态与内核态

ring 0 - 1 - 2 - 3

我们自己的应用程序在3级

Linux 操作系统运行在0级别可以直接和硬件打交道

详细介绍：https://www.cnblogs.com/gizing/p/10925286.html

#### 重量级锁

synchronize 运行在 JVM

想要加锁

需要向 **内核** 申请锁

升级过程：先在用户态加锁，加不上再升级到内核态



#### 轻量级锁

在用户态完成

不需要动用内核

只需要在JVM里就可以完成



## CAS

英文名：``compare and swap``、``compare and set``、``compare and exchange``

他是**<u>自旋锁</u>**

会存在**ABA**问题，解决办法：<u>加版本号</u>，相关文章：https://blog.csdn.net/u011277123/article/details/90699619

在Java代码中用到CAS的地方：AtomicInteger等 

![image-20200626121339663](image\java并发\image-20200626121339663.png)

#### CAS的底层实现原理：是调用native的代码

- **java 源码**

```java
public final native boolean compareAndSwapInt(Object var1, long var2, int var4, int var5);
```

- **HotSpot虚拟机写的C++ 源码**（详细：https://blog.csdn.net/u014082714/article/details/50825597）

![image-20200626132544234](image\java并发\image-20200626132544234.png)

**asm汇编指令**

**lock_if_mp**：程序会根据当前处理器的类型来决定是否为cmpxchg指令添加lock前缀。如果程序是在多处理器上运行，就为cmpxchg指令加上lock前缀（lock cmpxchg）。反之，如果程序是在单处理器上运行，就省略lock前缀（单处理器自身会维护单处理器内的顺序一致性，不需要lock前缀提供的内存屏障效果）。

**mp**：的意思是 multi-process，多核CPU



## 一个对象的内存布局

Object o = new Object(); 内存布局

- Mark word：占8字节，头部标记字节

  - 锁信息
  - hashCode信息
  - GC信息（分代年龄）占4个bit，所以年龄最大是15

- klass point：占4字节，指向方法区的指针

- instance data:成员变量所占部分：

- padding：对齐，补齐之前三个所占空间不足2的n次方，假如前面三个占了12个字节，padding需要占4个，补充到16

  ![这里写图片描述](image\java并发\SouthEast)

关于此部分的代码请看``C1_HelloJOL.java``

详细介绍点这里：https://blog.csdn.net/zqz_zqz/article/details/70246212

## 锁升级步骤

![image-20200626145104838](image\java并发\image-20200626145104838.png)

- 偏向锁一般在线程启动后的4秒开始启动，为什么？因为系统开启状态的时候会有很多线程需要竞争，所以不需要开启偏向锁。

  通过jvm参数：-XX:BiasedLockingStartupDelay=0，可以将启动延迟开启偏向锁，设置为0秒

  

- 1：没有任何竞争者的时候是偏向锁

- 2：多了一个竞争者，就开始用CAS自旋抢占这个锁，升级为轻量锁

- 3：如果自旋的线程过多，太消耗CPU资源了，所以这时需要升级为重量级锁，需要CPU来管理这些线程

  JVM对于自旋周期的选择，jdk1.5这个限度是一定的写死的，在1.6引入了适应性自旋锁，适应性自旋锁意味着自旋的时间不在是固定的了，而是由前一次在同一个锁上的自旋时间以及锁的拥有者的状态来决定，基本认为一个线程上下文切换的时间是最佳的一个时间，同时JVM还针对当前CPU的负荷情况做了较多的优化

  1. 如果平均负载小于CPUs则一直自旋
  2. 如果有超过(CPUs/2)个线程正在自旋，则后来线程直接阻塞
  3. 如果正在自旋的线程发现Owner发生了变化则延迟自旋时间（自旋计数）或进入阻塞
  4. 如果CPU处于节电模式则停止自旋
  5. 自旋时间的最坏情况是CPU的存储延迟（CPU A存储了一个数据，到CPU B得知这个数据直接的时间差）
  6. 自旋时会适当放弃线程优先级之间的差异 

- 4：升级到重量级锁后，不需要线程自旋了，

## 偏向锁



## Synchronized 与 Lock的区别

1：Synchronized 是内置的关键字，Lock是一个雷

2：Synchronized 无法获取锁的状态，Lock可以判断是否获取锁了

3：Synchronized 会自动释放锁，Lock必须要手动释放锁

4：Synchronized 线程 1（获得锁，阻塞）、线程2（等待，傻傻的等）；Lock锁就不一定会等待下 去； 

5：Synchronized 可重入锁，不可以中断的，非公平；Lock ，可重入锁，可以 判断锁，非公平（可以 自己设置）； 

6：Synchronized 适合锁少量的代码同步问题，Lock 适合锁大量的同步代码！



### 其他相关文章

##### [Java锁---偏向锁、轻量级锁、自旋锁、重量级锁](https://www.cnblogs.com/linghu-java/p/8944784.html)