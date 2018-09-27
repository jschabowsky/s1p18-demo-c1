/*
 * 
 * Copyright (c) 2018 Solace Corp.
 * 
 */
package org.springframework.cloud.stream.app.utah.inventory.lookup.processor;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for UtahInventoryLookupProcessorConfiguration
 *
 * @author Solace Corp.
 */
@ConfigurationProperties("utah.inventory.lookup")
public class UtahInventoryLookupProcessorProperties {	
	public static final String DEFAULT_INVENTORY_QUERY_URL = "https://webapps2.abc.utah.gov/Production/OnlineInventoryQuery/IQ/InventoryQuery.aspx";
	public static final String DEFAULT_PUBLISH_TOPIC_PREFIX = "inventory/";
	
	/**
	 * URL used to perform the lookup for a Utah DABC product
	 */
	private String inventoryQueryUrl = DEFAULT_INVENTORY_QUERY_URL;
	
	/**
	 * Topic prefix on which to publish inventory info about the product, store and quantity (and additional details)
	 */
	private String publishTopicPrefix = DEFAULT_PUBLISH_TOPIC_PREFIX;

	public String getInventoryQueryUrl() {
		return inventoryQueryUrl;
	}

	public void setInventoryQueryUrl(String inventoryQueryUrl) {
		this.inventoryQueryUrl = inventoryQueryUrl;
	}

	public String getPublishTopicPrefix() {
		return publishTopicPrefix;
	}

	public void setPublishTopicPrefix(String publishTopicPrefix) {
		this.publishTopicPrefix = publishTopicPrefix;
	}

}
