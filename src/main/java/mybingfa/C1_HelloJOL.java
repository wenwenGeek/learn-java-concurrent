package mybingfa;

import org.openjdk.jol.info.ClassLayout;

public class C1_HelloJOL {

    public static void main(String[] args) throws InterruptedException {
        //method1();
        method2();
    }

    private static void method1() {
        Object o = new Object();
        System.out.println(ClassLayout.parseInstance(o).toPrintable());

        synchronized (o) {
            System.out.println(ClassLayout.parseInstance(o).toPrintable());
        }

        /**
         * 输出信息：
         * java.lang.Object object internals:
         *  OFFSET  SIZE   TYPE DESCRIPTION                               VALUE         --  001代表无锁状态
         *       0     4        (object header)                           01 00 00 00 (00000001 00000000 00000000 00000000) (1)         --  markword 信息
         *       4     4        (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0)         --  markword 信息
         *       8     4        (object header)                           e5 01 00 20 (11100101 00000001 00000000 00100000) (536871397) -- 方法区对象信息
         *      12     4        (loss due to the next object alignment)                                                                 -- 对齐成16
         * Instance size: 16 bytes
         * Space losses: 0 bytes internal + 4 bytes external = 4 bytes total
         *
         * java.lang.Object object internals:
         *  OFFSET  SIZE   TYPE DESCRIPTION                               VALUE         --  000代表轻量级锁（刚初始化就是轻量级锁的原因，是一般系统启动后4秒，偏向锁才能生效）
         *       0     4        (object header)                           f8 f4 bb 02 (11111000 11110100 10111011 00000010) (45872376)  -- 与上面不同的是加了锁改变了markword
         *       4     4        (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0)
         *       8     4        (object header)                           e5 01 00 20 (11100101 00000001 00000000 00100000) (536871397)
         *      12     4        (loss due to the next object alignment)
         * Instance size: 16 bytes
         * Space losses: 0 bytes internal + 4 bytes external = 4 bytes total
         */
    }

    private static void method2() throws InterruptedException {
        Thread.sleep(5000);
        Object o = new Object();
        System.out.println(ClassLayout.parseInstance(o).toPrintable());

        synchronized (o) {
            System.out.println(ClassLayout.parseInstance(o).toPrintable());
        }

        /**
         java.lang.Object object internals:
         OFFSET  SIZE   TYPE DESCRIPTION                               VALUE    --  101这是偏向锁
         0     4        (object header)                           05 00 00 00 (00000101 00000000 00000000 00000000) (5)
         4     4        (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0)
         8     4        (object header)                           e5 01 00 20 (11100101 00000001 00000000 00100000) (536871397)
         12     4        (loss due to the next object alignment)
         Instance size: 16 bytes
         Space losses: 0 bytes internal + 4 bytes external = 4 bytes total

         java.lang.Object object internals:
         OFFSET  SIZE   TYPE DESCRIPTION                               VALUE    --  101这是偏向锁,后面跟着一堆都是线程编号
         0     4        (object header)                           05 e8 ed 02 (00000101 11101000 11101101 00000010) (49145861)
         4     4        (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0)
         8     4        (object header)                           e5 01 00 20 (11100101 00000001 00000000 00100000) (536871397)
         12     4        (loss due to the next object alignment)
         Instance size: 16 bytes
         Space losses: 0 bytes internal + 4 bytes external = 4 bytes total
         */
    }
}
