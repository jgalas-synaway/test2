package com.synaway.oneplaces.test;

import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(locations = {"classpath:application-context.xml", "file:src/main/webapp/WEB-INF/1places-servlet.xml", "file:src/test/resources/application-context-test.xml" })
abstract public class AbstractIntegrationTest {

}
