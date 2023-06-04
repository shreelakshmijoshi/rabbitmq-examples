package client.server;

import java.lang.instrument.Instrumentation;

public class InstrumentationAgent {
  private static volatile Instrumentation instrumentationAgent;

  public static void premain(final String args, final Instrumentation instrumentation)
  {
    instrumentationAgent = instrumentation;
  }

  public static long getObjectSize(final Object object)
  {
    if(instrumentationAgent == null)
    {
      throw new IllegalArgumentException("Agent not initialized");
    }
    System.out.println("heree 1111");
    return instrumentationAgent.getObjectSize(object);
  }
}
