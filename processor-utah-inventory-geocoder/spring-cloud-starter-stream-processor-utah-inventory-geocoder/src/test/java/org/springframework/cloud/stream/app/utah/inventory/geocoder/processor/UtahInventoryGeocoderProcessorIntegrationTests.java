/*
 * 
 * Copyright (c) 2018 Solace Corp.
 * 
 */

package org.springframework.cloud.stream.app.utah.inventory.geocoder.processor;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.annotation.Output;

import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.solace.demo.utahdabc.datamodel.Product;
import com.solace.demo.utahdabc.datamodel.ProductInventoryData;
import com.solace.demo.utahdabc.datamodel.StoreInventory;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.springframework.cloud.stream.test.matcher.MessageQueueMatcher.receivesPayloadThat;

/**
 * Integration Tests for the geocoder Processor.
 *
 * @author Solace Corp.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@SpringBootTest
public abstract class UtahInventoryGeocoderProcessorIntegrationTests {

	@Autowired
	protected Processor channels;

	@Autowired
	protected MessageCollector collector;
	
	/**
	 * Validates that the module loads with default properties.
	 */
	public static class UsingNothingIntegrationTests extends UtahInventoryGeocoderProcessorIntegrationTests {
		private static final String RESULT_SUBSTRING = "{\"warehouseInventoryQty\":0,\"warehouseOnOrderQty\":0,\"productStatus\":null,\"product\":{\"name\":null,\"div_code\":null,\"dept_code\":null,\"class_code\":null,\"size\":0,\"csc\":4006,\"price\":0.0,\"lcboPrice\":0.0,\"status\":null,\"tags\":null,\"creationTimestamp\":";
		
		public static void doGenericProcessorTest(Processor channels, MessageCollector collector, ProductInventoryData pid, String testResult) {
			channels.input().send(new GenericMessage<ProductInventoryData>(pid));
			assertThat(collector.forChannel(channels.output()), receivesPayloadThat(containsString(testResult)));
		}
		
		@Test
	    @Output(Processor.OUTPUT)
		public void test() {
			StoreInventory storeInventory = new StoreInventory();
			storeInventory.setStoreAddress("1255 West North Temple");
			storeInventory.setStoreCity("Salt Lake City");
			storeInventory.setStoreID("0039");

			Product p = new Product();
			p.setCsc(4006);
			
			ProductInventoryData pid = new ProductInventoryData();
			pid.setStoreInventory(storeInventory);
			pid.setProduct(p);
			
			doGenericProcessorTest(channels, collector, pid, RESULT_SUBSTRING);
		}
	}

	@SpringBootTest("utah.inventory.geocoder.googleMapsApiKey=<API_KEY>")
	public static class UsingPropsIntegrationTests extends UtahInventoryGeocoderProcessorIntegrationTests {
		private static final String RESULT_SUBSTRING = "{\"warehouseInventoryQty\":0,\"warehouseOnOrderQty\":0,\"productStatus\":null,\"product\":{\"name\":null,\"div_code\":null,\"dept_code\":null,\"class_code\":null,\"size\":0,\"csc\":4006,\"price\":0.0,\"lcboPrice\":0.0,\"status\":null,\"tags\":null,\"creationTimestamp\":";
		
		@Test
		public void test() {
			StoreInventory storeInventory = new StoreInventory();
			storeInventory.setStoreAddress("255 Park Ave S");
			storeInventory.setStoreCity("New York City");
			storeInventory.setStoreID("0099");

			Product p = new Product();
			p.setCsc(4006);
			
			ProductInventoryData pid = new ProductInventoryData();
			pid.setStoreInventory(storeInventory);
			pid.setProduct(p);
			
			UsingNothingIntegrationTests.doGenericProcessorTest(channels, collector, pid, RESULT_SUBSTRING);
		}
	}

	@SpringBootApplication
	public static class UtahInventoryGeocoderProcessorApplication {
		public static void main(String[] args) {
			SpringApplication.run(UtahInventoryGeocoderProcessorApplication.class, args);
		}
	}

}
