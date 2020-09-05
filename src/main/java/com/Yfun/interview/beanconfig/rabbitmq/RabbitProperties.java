package com.Yfun.interview.beanconfig.rabbitmq;

import com.Yfun.interview.annotation.AutoData;
import com.Yfun.interview.annotation.Obtain;
import org.springframework.stereotype.Component;

/**
 * @ClassName : RabbitProperties
 * @Description :
 * @Author : DeYuan
 * @Date: 2020-09-01 16:43
 */
@Component
@Obtain(file_name = "GlobalConfiguration.properties",prefix = "rabbitmq.config")
public class RabbitProperties {
    @AutoData
    private String host;
    @AutoData
    private String vhost;

    public RabbitProperties() {
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getVhost() {
        return vhost;
    }

    public void setVhost(String vhost) {
        this.vhost = vhost;
    }
}
