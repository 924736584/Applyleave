package com.Yfun.interview.util;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.apache.log4j.spi.LoggerFactory;

/**
 * @ClassName : LogProcessingUtil
 * @Description :
 * @Author : DeYuan
 * @Date: 2020-08-30 11:21
 */
public class LogProcessingUtil {
    private enum LOG_LEVEL{ERROR,DEBUG};
    private  Logger LOGGER;
    private String[] defaultParam=null;
    /* logger */
    public LogProcessingUtil(Class T){
          this.LOGGER= Logger.getLogger(T);
    }
    public void configParam(String... param){
        defaultParam=null;
        if(param.length>0) {
            defaultParam = new String[param.length];
            System.arraycopy(param, 0, defaultParam, 0, param.length);
        }
    }
    public  void error(String msg){
        processingParam(LOG_LEVEL.ERROR,msg,defaultParam);
    }
    public  void debug(String msg){
        processingParam(LOG_LEVEL.ERROR,msg,defaultParam);
    }
    public  void error(String msg,String ... param){
        processingParam(LOG_LEVEL.ERROR,msg,param);
    }
    public  void debug(String msg,String ... param){
        processingParam(LOG_LEVEL.DEBUG,msg,param);
    }
    public void warn(String msg){
        LOGGER.warn(msg);
    }
    public void info(String msg){
        LOGGER.info(msg);
    }
    private  void processingParam(LOG_LEVEL log_level,String msg,String ... param){
      new Thread(new Runnable() {
            @Override
            public void run() {
                if(log_level.equals(LOG_LEVEL.DEBUG)){
                    LOGGER.debug(msg);
                }else if(log_level.equals(LOG_LEVEL.ERROR)){
                    StringBuilder builder=new StringBuilder();
                    if(param!=null && param.length>0){
                        for (int i = 0; i < param.length; i++) {
                            builder.append(param[i]);
                            if(i!=param.length-1){
                                builder.append("|&|&|");
                            }
                        }
                        MDC.put("passparam",new String(builder));
                        LOGGER.error(msg);
                        MDC.remove("passparam");
                    }
                }
            }
        }).start();
    }
}
