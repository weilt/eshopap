package com.weilt.eshopproduct.request;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author weilt
 * @com.weilt.eshopproduct.request
 * @date 2018/8/27 == 1:28
 */
public class RequestQueue {

    //单例，静态内部类，绝对线程安全
    private static class Singleton{
        public static RequestQueue instance;
        static {
            instance = new RequestQueue();
        }
        public static RequestQueue getInstance(){
            return instance;
        }
    }
    public static RequestQueue getInstance(){
        return Singleton.instance;
    }

    private List<ArrayBlockingQueue<Request>> queues = new ArrayList<ArrayBlockingQueue<Request>>();

    /**
     * 添加一个queue
     * @param queue
     */
    public void addQueue(ArrayBlockingQueue<Request> queue){
            this.queues.add(queue);
    }

    /**
     * 获取内存队列的数量
     * @return
     */
    public int queueSize() {
        return queues.size();
    }

    /**
     * 获取内存队列
     * @param index
     * @return
     */
    public ArrayBlockingQueue<Request> getQueue(int index) {
        return queues.get(index);
    }

    private Map<Integer, Boolean> flagMap = new ConcurrentHashMap<Integer, Boolean>();

    public Map<Integer, Boolean> getFlagMap() {
        return flagMap;
    }
}
