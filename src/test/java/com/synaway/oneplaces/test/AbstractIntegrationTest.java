package com.synaway.oneplaces.test;

import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/application-context.xml", "file:src/test/resources/META-INF/spring/application-context-test.xml" })
abstract public class AbstractIntegrationTest {

}
