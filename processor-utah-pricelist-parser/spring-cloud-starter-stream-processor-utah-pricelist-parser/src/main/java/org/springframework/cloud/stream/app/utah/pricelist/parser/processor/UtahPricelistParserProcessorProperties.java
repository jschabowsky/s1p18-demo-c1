/*
 * 
 * Copyright (c) 2018 Solace Corp.
 * 
 */
package org.springframework.cloud.stream.app.utah.pricelist.parser.processor;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for UtahPricelistParserProcessorConfiguration
 *
 * @author Solace Corp.
 */
@ConfigurationProperties("utah.pricelist.parser")
public class UtahPricelistParserProcessorProperties {
	public static final String DEFAULT_PUBLISH_TOPIC_PREFIX = "product/";

	/**
	 * Topic prefix to use when publishing categorized products
	 */
	private String publishTopicPrefix = DEFAULT_PUBLISH_TOPIC_PREFIX;

	public String getPublishTopicPrefix() {
		return publishTopicPrefix;
	}

	public void setPublishTopicPrefix(String publishTopicPrefix) {
		this.publishTopicPrefix = publishTopicPrefix;
	}

}
