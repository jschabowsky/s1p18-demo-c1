/*
 * 
 * Copyright (c) 2018 Solace Corp.
 * 
 */
package org.springframework.cloud.stream.app.utah.pricelist.loader.processor;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the pricelist loader processor.
 *
 * @author Solace Corp.
 */
@ConfigurationProperties("utah.pricelist.loader")
public class UtahPricelistLoaderProcessorProperties {
	public static final String DEFAULT_PROCESSOR_URL = "https://webapps2.abc.utah.gov/Production/OnlinePriceList/DisplayPriceList.aspx";
	
	private String processorUrl = DEFAULT_PROCESSOR_URL;

	public void setProcessorUrl(String processorUrl) {
		this.processorUrl = processorUrl;
	}

	public String getProcessorUrl() {
		return processorUrl;
	}
}
