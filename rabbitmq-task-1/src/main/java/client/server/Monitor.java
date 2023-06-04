package client.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Monitor {
  public Map<String, ArrayList<QueueSize>> map;

  public Monitor() {
    this.map = new HashMap<>();
  }


  // queueName | dataConsumed | userID
  // userId -> (queueName, sum), (queueName, sum), (queueName, sum)

  public List<QueueSize> getUserData(String userId) {
    return map.get(userId);
  }

  public void addUser(String userId) {
    System.out.println("Adding user");
    map.put(userId, new ArrayList<>());
  }

  public void addUserData(String userId, String queueName, long size) {
    if(map.get(userId) == null)
    {
      addUser(userId);
    }
    System.out.println("add user data");
    QueueSize queueSize = new QueueSize();
    queueSize.setQueueName(queueName);
    queueSize.setQueueSize(size);
    map.get(userId).add(queueSize);
  }

  public long getTotalUserData(String userId) {
    long sum = 0;
    List<QueueSize> data = this.getUserData(userId);
    System.out.println("user data : " + data);
    for (var val : data) {
      long currentSum = val.getQueueSize();
      sum += currentSum;
    }
    return sum;
  }


}
