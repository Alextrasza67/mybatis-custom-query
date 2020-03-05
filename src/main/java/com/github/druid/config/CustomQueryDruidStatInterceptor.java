//package com.github.druid.config;
//
//import com.alibaba.druid.support.spring.stat.DruidStatInterceptor;
//import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.stereotype.Component;
//
//@Configuration
//@Component
//public class CustomQueryDruidStatInterceptor extends DruidStatInterceptor{
//
//
//
//    @Bean
//    public BeanNameAutoProxyCreator beanNameAutoProxyCreator(){
//        BeanNameAutoProxyCreator beanNameAutoProxyCreator=new BeanNameAutoProxyCreator();
//        //设置要创建代理的那些Bean的名字
//        beanNameAutoProxyCreator.setBeanNames("customQueryMapper");
//        //设置拦截链名字(这些拦截器是有先后顺序的)
//        beanNameAutoProxyCreator.setInterceptorNames("customQueryDruidStatInterceptor");
//        return beanNameAutoProxyCreator;
//    }
//
//}
