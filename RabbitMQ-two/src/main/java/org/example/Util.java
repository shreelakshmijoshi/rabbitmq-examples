package org.example;

import java.util.ArrayList;
import java.util.List;

public class Util {

    public static String DEFAULT_EXCHANGE = "my-exchange";
    public static String DEFAULT_EXCHANGE_TYPE = "direct";
    private List<String> queueList;

    public Util()
    {
        this.queueList = new ArrayList<>();
    }

    public void addQueue(String queueName)
    {
        this.queueList.add(queueName);
    }

    public List<String> getQueueList()
    {
        return this.queueList;
    }

    public String getQueue(int index)
    {
        return this.queueList.get(index);
    }


}
