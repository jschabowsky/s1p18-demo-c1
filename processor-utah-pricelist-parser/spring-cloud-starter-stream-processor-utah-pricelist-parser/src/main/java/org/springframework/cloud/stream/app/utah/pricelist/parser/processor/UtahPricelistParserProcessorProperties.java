/*
 * 
 * Copyright (c) 2018 Solace Corp.
 * 
 */
package org.springframework.cloud.stream.app.utah.pricelist.parser.processor;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the pricelist parser processor.
 *
 * @author Solace Corp.
 */
@ConfigurationProperties("utah.pricelist.parser")
public class UtahPricelistParserProcessorProperties {
	public static final String DEFAULT_PUBLISH_TOPIC_PREFIX = "product/";

	private String publishTopicPrefix = DEFAULT_PUBLISH_TOPIC_PREFIX;

	public String getPublishTopicPrefix() {
		return publishTopicPrefix;
	}

	public void setPublishTopicPrefix(String publishTopicPrefix) {
		this.publishTopicPrefix = publishTopicPrefix;
	}

}
