package com.synaway.oneplaces.test;

import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(locations = {"file:src/main/resources/META-INF/spring/application-context.xml", "file:src/test/resources/META-INF/spring/application-context-test.xml" })
abstract public class AbstractIntegrationTest {

}
