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

import static org.hamcrest.Matchers.is;
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
		private static final String TEST_RESULT = "{\"warehouseInventoryQty\":0,\"warehouseOnOrderQty\":0,\"productStatus\":null,\"product\":{\"name\":null,\"div_code\":null,\"dept_code\":null,\"class_code\":null,\"size\":0,\"csc\":4006,\"price\":0.0,\"lcboPrice\":0.0,\"status\":null,\"tags\":null,\"spa\":null},\"storeInventory\":{\"storeID\":\"0039\",\"storeName\":null,\"productQty\":0,\"storeAddress\":\"1255 West North Temple\",\"storeGeoLat\":40.77105194863543,\"storeGeoLng\":-111.92751735448837,\"storeCity\":\"Salt Lake City\",\"storePhone\":null}}";
		
		public static void doGenericProcessorTest(Processor channels, MessageCollector collector, ProductInventoryData pid, String testResult) {
			channels.input().send(new GenericMessage<ProductInventoryData>(pid));
			assertThat(collector.forChannel(channels.output()), receivesPayloadThat(is(testResult)));
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
			
			doGenericProcessorTest(channels, collector, pid, TEST_RESULT);
		}
	}

	@SpringBootTest("utah.inventory.geocoder.lookupState=NY")
	public static class UsingPropsIntegrationTests extends UtahInventoryGeocoderProcessorIntegrationTests {
		private static final String TEST_RESULT = "{\"warehouseInventoryQty\":0,\"warehouseOnOrderQty\":0,\"productStatus\":null,\"product\":{\"name\":null,\"div_code\":null,\"dept_code\":null,\"class_code\":null,\"size\":0,\"csc\":4006,\"price\":0.0,\"lcboPrice\":0.0,\"status\":null,\"tags\":null,\"spa\":null},\"storeInventory\":{\"storeID\":\"0099\",\"storeName\":null,\"productQty\":0,\"storeAddress\":\"255 Park Ave S\",\"storeGeoLat\":40.738602448353575,\"storeGeoLng\":-73.98742407560349,\"storeCity\":\"New York City\",\"storePhone\":null}}";
		
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
			
			UsingNothingIntegrationTests.doGenericProcessorTest(channels, collector, pid, TEST_RESULT);
		}
	}

	@SpringBootApplication
	public static class UtahInventoryGeocoderProcessorApplication {
		public static void main(String[] args) {
			SpringApplication.run(UtahInventoryGeocoderProcessorApplication.class, args);
		}
	}

}