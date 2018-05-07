package com.changyu.foryou.service;


import java.util.concurrent.DelayQueue;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.changyu.foryou.model.DSHOrder;

@Service  
public class DelayService {  
      
    private static final Logger log = Logger.getLogger(DelayService.class);  
      
      
    private boolean start ;    
    private OnDelayedListener listener;  
    private DelayQueue<DSHOrder> delayQueue = new DelayQueue<DSHOrder>();  
      
    public static interface OnDelayedListener{  
        public void onDelayedArrived(DSHOrder order);  
    }  
  
    public void start(OnDelayedListener listener){  
        if(start){  
            return;  
        }  
        log.info("DelayService 启动");  
        start = true;  
        this.listener = listener;  
        new Thread(new Runnable(){  
            public void run(){  
                try{  
                    while(true){  
                        DSHOrder order = delayQueue.take();  
                        if(DelayService.this.listener != null){  
                            DelayService.this.listener.onDelayedArrived(order);  
                        }  
                    }  
                }catch(Exception e){  
                    e.printStackTrace();  
                }  
            }  
        }).start();;  
    }  
      
    public void add(DSHOrder order){  
        delayQueue.put(order);  
        System.out.println("delayQueue add,size="+ delayQueue.size());
    }  
  
    public boolean remove(DSHOrder order){  
    	System.out.println("delayQueue remove before,size="+ delayQueue.size());
        return delayQueue.remove(order);       
    }  
       
     public void remove(long orderId){  
        DSHOrder[] array = delayQueue.toArray(new DSHOrder[]{});  
        if(array == null || array.length <= 0){  
            return;  
        }  
        DSHOrder target = null;  
        for(DSHOrder order : array){  
            if(order.getOrderId() == orderId){  
                target = order;  
                break;  
            }  
        }  
        if(target != null){  
            delayQueue.remove(target);  
        }  
    }  
} 