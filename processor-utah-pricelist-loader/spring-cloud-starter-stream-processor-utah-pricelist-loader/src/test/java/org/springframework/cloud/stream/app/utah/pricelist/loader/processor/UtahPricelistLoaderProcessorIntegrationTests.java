/*
 * 
 * Copyright (c) 2018 Solace Corp.
 * 
 */

package org.springframework.cloud.stream.app.utah.pricelist.loader.processor;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.cloud.stream.test.binder.MessageCollector;

import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.annotation.DirtiesContext;

import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.CoreMatchers.startsWith;

import static org.junit.Assert.assertThat;
import static org.springframework.cloud.stream.test.matcher.MessageQueueMatcher.receivesPayloadThat;

/**
 * Integration Tests for the lookup Processor.
 *
 * @author Solace Corp.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@SpringBootTest
public abstract class UtahPricelistLoaderProcessorIntegrationTests {

	@Autowired
	protected Processor channels;

	@Autowired
	protected MessageCollector collector;
	
	private static final String RESULT_SUBSTRING = "{\"name\":\"$1.00 WINE SAMPLES 750ml\",\"div_code\":\"Y\",\"dept_code\":\"YS\",\"class_code\":\"YSE\",\"size\":750,\"csc\":995321,\"price\":1.09,\"lcboPrice\":0.0,\"status\":\"L\",\"tags\":null,\"_timestamp\":\"";

	/**
	 * Validates that the module loads with default properties.
	 */
	public static class UsingNothingIntegrationTests extends UtahPricelistLoaderProcessorIntegrationTests {
		public static void doGenericProcessorTest(Processor channels, MessageCollector collector) {
			channels.input().send(new GenericMessage<String>("Test Data"));
			assertThat(collector.forChannel(channels.output()), receivesPayloadThat(startsWith(RESULT_SUBSTRING)));
		}
		
		@Test
		public void test() {
			// Deferring usage of this until we can easily specify a local test file for a no props UT 
			
			
			//doGenericProcessorTest(channels, collector);
		}		
	}
    
	@SpringBootTest("utah.pricelist.loader.processorUrl=http://127.0.0.1:8888/test.html")
	public static class UsingPropsIntegrationTests extends UtahPricelistLoaderProcessorIntegrationTests {
		@Test
		public void test() {
			UsingNothingIntegrationTests.doGenericProcessorTest(channels, collector);
		}
	}

	@SpringBootApplication
	public static class UtahPricelistLoaderProcessorApplication {
		public static void main(String[] args) {
			SpringApplication.run(UtahPricelistLoaderProcessorApplication.class, args);
		}
	}

}
