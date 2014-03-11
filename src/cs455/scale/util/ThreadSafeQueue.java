package cs455.scale.util;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Author: Thilina
 * Date: 3/11/14
 */
public class ThreadSafeQueue<T> {

    private final Queue<T> queue;

    public ThreadSafeQueue() {
        queue = new LinkedList<T>();
    }

    public boolean add(T obj){
        synchronized (queue){
            return queue.add(obj);
        }
    }

    public boolean isEmpty(){
        synchronized (queue){
            return queue.isEmpty();
        }
    }

    public T peek(){
        synchronized (queue){
            return queue.peek();
        }
    }

    public T remove(){
        synchronized (queue){
            return queue.remove();
        }
    }

    public void remove(T obj){
        synchronized (queue){
            queue.remove(obj);
        }
    }

}
