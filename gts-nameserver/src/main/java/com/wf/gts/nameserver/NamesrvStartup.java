package com.wf.gts.nameserver;
import java.util.Properties;
import java.util.concurrent.Callable;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import com.wf.gts.nameserver.util.ServerUtil;
import com.wf.gts.nameserver.util.ShutdownHookThread;
import com.wf.gts.remoting.netty.NettyServerConfig;


public class NamesrvStartup {
  
  public static Properties properties = null;
  public static CommandLine commandLine = null;
  
  public static void main(String[] args) {
      main0(args);
  }

  public static NamesrvController main0(String[] args) {
      try {
          Options options = ServerUtil.buildCommandlineOptions(new Options());
          commandLine = ServerUtil.parseCmdLine("mqnamesrv", args, buildCommandlineOptions(options), new PosixParser());
          if (null == commandLine) {
              System.exit(-1);
              return null;
          }
          final NamesrvConfig namesrvConfig = new NamesrvConfig();
          final NettyServerConfig nettyServerConfig = new NettyServerConfig();
          //配置
          nettyServerConfig.setListenPort(9876);
          
     /*     if (commandLine.hasOption('c')) {
              String file = commandLine.getOptionValue('c');
              if (file != null) {
                  InputStream in = new BufferedInputStream(new FileInputStream(file));
                  properties = new Properties();
                  properties.load(in);
                  MixAll.properties2Object(properties, namesrvConfig);
                  MixAll.properties2Object(properties, nettyServerConfig);
                  namesrvConfig.setConfigStorePath(file);
                  System.out.printf("load config properties file OK, " + file + "%n");
                  in.close();
              }
          }*/

          /*if (commandLine.hasOption('p')) {
              System.exit(0);
          }*/
        /*  MixAll.properties2Object(ServerUtil.commandLine2Properties(commandLine), namesrvConfig);
          if (null == namesrvConfig.getRocketmqHome()) {
              System.out.printf("Please set the %s variable in your environment to match the location of the RocketMQ installation%n", MixAll.ROCKETMQ_HOME_ENV);
              System.exit(-2);
          }*/

          final NamesrvController controller = new NamesrvController(namesrvConfig, nettyServerConfig);
          boolean initResult = controller.initialize();
          if (!initResult) {
              controller.shutdown();
              System.exit(-3);
          }
          Runtime.getRuntime().addShutdownHook(new ShutdownHookThread(new Callable<Void>() {
              @Override
              public Void call() throws Exception {
                  controller.shutdown();
                  return null;
              }
          }));
          controller.start();
          return controller;
      } catch (Throwable e) {
          e.printStackTrace();
          System.exit(-1);
      }
      return null;
  }

  public static Options buildCommandlineOptions(final Options options) {
      Option opt = new Option("c", "configFile", true, "Name server config properties file");
      opt.setRequired(false);
      options.addOption(opt);
      opt = new Option("p", "printConfigItem", false, "Print all config item");
      opt.setRequired(false);
      options.addOption(opt);
      return options;
  }
  

}
