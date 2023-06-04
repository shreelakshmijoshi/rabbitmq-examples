package client.server;

import io.vertx.rabbitmq.RabbitMQMessage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Monitor {
  public ConcurrentHashMap<String, Map<String, Long>> userQMap;
  public ConcurrentHashMap<String, Long> userTotalMap;

  public Monitor() {
    userQMap = new ConcurrentHashMap<>();
    userTotalMap = new ConcurrentHashMap<>();
  }


  // queueName | dataConsumed | userID
  // userId -> (queueName, sum), (queueName, sum), (queueName, sum)

  public long getTotalUserData(String userId) {
      System.out.println(userQMap);
      System.out.println(userTotalMap);
      return userTotalMap.get(userId);
  }



  public void updateSize(RabbitMQMessage message, String userId, String queueName) {

      System.out.println("hereee0000");
//      long size = InstrumentationAgent.getObjectSize(message);
      long size = 1;
      System.out.println("heree111");
      System.out.println("object size : " + size); //
      userQMap.putIfAbsent(userId, new ConcurrentHashMap<>());
      userQMap.get(userId).put(queueName, userQMap.get(userId).getOrDefault(queueName, 0L)+size);
      userTotalMap.put(userId, userTotalMap.getOrDefault(userId, 0L)+size);

  }
}
