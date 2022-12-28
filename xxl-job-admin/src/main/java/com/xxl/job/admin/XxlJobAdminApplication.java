package com.xxl.job.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author xuxueli 2018-10-28 00:38:13
 */
@SpringBootApplication
@Slf4j
// @EnableEurekaClient
@EnableDiscoveryClient
public class XxlJobAdminApplication {

	public static void main(String[] args) {
        SpringApplication.run(XxlJobAdminApplication.class, args);
        log.info("XxlJob-admin注册中心启动了");
	}

}