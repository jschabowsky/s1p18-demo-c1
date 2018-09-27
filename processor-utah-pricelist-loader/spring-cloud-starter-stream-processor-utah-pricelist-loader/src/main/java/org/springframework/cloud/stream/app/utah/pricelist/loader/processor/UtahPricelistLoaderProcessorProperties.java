/*
 * 
 * Copyright (c) 2018 Solace Corp.
 * 
 */
package org.springframework.cloud.stream.app.utah.pricelist.loader.processor;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for UtahPricelistLoaderProcessorConfiguration
 *
 * @author Solace Corp.
 */
@ConfigurationProperties("utah.pricelist.loader")
public class UtahPricelistLoaderProcessorProperties {
	public static final String DEFAULT_PROCESSOR_URL = "https://webapps2.abc.utah.gov/Production/OnlinePriceList/DisplayPriceList.aspx";
	public static final String DEFAULT_PUBLISH_TOPIC = "product/utah/all";
	
	/**
	 * URL of Utah DABC product / price list
	 */
	private String processorUrl = DEFAULT_PROCESSOR_URL;
	
	/**
	 * Topic on which to publish all products
	 */
	private String publishTopic = DEFAULT_PUBLISH_TOPIC;
	
	public void setProcessorUrl(String processorUrl) {
		this.processorUrl = processorUrl;
	}

	public String getProcessorUrl() {
		return processorUrl;
	}

	public String getPublishTopic() {
		return publishTopic;
	}

	public void setPublishTopic(String publishTopic) {
		this.publishTopic = publishTopic;
	}
}
