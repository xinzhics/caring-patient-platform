package com.caring.sass.common.utils;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @ClassName SaasLinkedBlockingQueue
 * @Description
 * @Author yangShuai
 * @Date 2022/3/7 15:44
 * @Version 1.0
 */
public class SaasLinkedBlockingQueue<E> extends LinkedBlockingQueue<E> {


    public SaasLinkedBlockingQueue(int capacity) {
        super(capacity);
    }


    @Override
    public boolean offer(E e) {
        try {
            put(e);
            return true;
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
        }
        return false;
    }
}
