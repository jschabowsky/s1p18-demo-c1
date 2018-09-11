/*
 * 
 * Copyright (c) 2018 Solace Corp.
 * 
 */
package org.springframework.cloud.stream.app.utah.inventory.geocoder.processor;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the inventory geocoder processor.
 *
 * @author Solace Corp.
 */
@ConfigurationProperties("utah.inventory.geocoder")
public class UtahInventoryGeocoderProcessorProperties {
	private String googleMapsApiKey;

	public String getGoogleMapsApiKey() {
		return googleMapsApiKey;
	}

	public void setGoogleMapsApiKey(String googleMapsApiKey) {
		this.googleMapsApiKey = googleMapsApiKey;
	}
}
