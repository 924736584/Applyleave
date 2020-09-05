package com.Yfun.interview.beanconfig.datanbase;

import com.Yfun.interview.util.ReadPropertiesResourceUtil;
import com.alibaba.druid.pool.DruidDataSource;
import com.mysql.cj.jdbc.Driver;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.io.FileNotFoundException;
import java.util.Properties;

/**
 * @ClassName : MybatiesConfig
 * @Description : MybatiesConfig
 * @Author : DeYuan
 * @Date: 2020-08-28 17:47
 */
@Configuration
public class MybatisConfig {
    private Properties properties=null;
    private SqlSessionFactoryBean sqlSession=null;
    private DruidDataSource source=null;
    @Bean
    public DataSource dataSource() {
        source = new DruidDataSource();
        source.setUrl(properties.getProperty("url"));
        try {
            Driver driver = (Driver) Class.forName(properties.getProperty("driver")).newInstance();
        source.setDriver(driver);
    }catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        source.setUsername( properties.getProperty("username"));
        source.setPassword( properties.getProperty("password"));
        return source;
    }
    @Bean
    public SqlSessionFactoryBean sqlSessionFactoryBeanConfig(){

        ReadPropertiesResourceUtil readProperties=null;
        try{
            readProperties = new ReadPropertiesResourceUtil();
            properties=readProperties.getProperties();
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
        sqlSession = new SqlSessionFactoryBean();
        sqlSession.setDataSource(dataSource());
        return sqlSession;
    }
    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer(){
        MapperScannerConfigurer mapperScanner = new MapperScannerConfigurer();
        mapperScanner.setBasePackage("com.Yfun.interview.mapper");
        mapperScanner.setSqlSessionFactoryBeanName("sqlSessionFactoryBeanConfig");
        return mapperScanner;
    }
}
