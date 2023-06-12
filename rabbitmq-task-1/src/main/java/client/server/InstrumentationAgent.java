package client.server;
import java.lang.instrument.Instrumentation;

public class InstrumentationAgent {
//  private static volatile Instrumentation instrumentationAgent;
private static volatile Instrumentation globalInstrumentation;
//  public static void premain(final String args, final Instrumentation instrumentation)
//  {
//    instrumentationAgent = instrumentation;
//  }
//
//  public static long getObjectSize(final Object object)
//  {
//    if(instrumentationAgent == null)
//    {
//      throw new IllegalArgumentException("Agent not initialized");
//    }
//    System.out.println("heree 1111");
//    return instrumentationAgent.getObjectSize(object);
//  }

  public static void premain(String s, Instrumentation instrumentation)
  {
    System.out.println("inside premain method");
    globalInstrumentation = instrumentation;
//    System.out.println(instrumentation.getObjectSize(new InstrumentationAgent()));
  }
  public static void agentmain(String agentArgs, Instrumentation inst)
  {
    System.out.println("inside agentmain method");
    globalInstrumentation = inst;
  }
  public static long getObjectSize(Object object)
  {
    if (globalInstrumentation == null) {
      throw new IllegalStateException("Agent not initialized.");
    }
    return globalInstrumentation.getObjectSize(object);
  }


}
