/*
 * 
 * Copyright (c) 2018 Solace Corp.
 * 
 */
package org.springframework.cloud.stream.app.utah.inventory.geocoder.processor;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.context.annotation.Bean;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import reactor.core.publisher.Flux;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;

import com.solace.demo.utahdabc.datamodel.ProductInventoryData;
import com.solace.demo.utahdabc.datamodel.StoreInventory;

/**
 * See README.adoc
 *
 * @author Solace Corp
 */
@EnableBinding(Processor.class)
@EnableConfigurationProperties(UtahInventoryGeocoderProcessorProperties.class)
public class UtahInventoryGeocoderProcessorConfiguration {
    private static final Logger log = LoggerFactory.getLogger(UtahInventoryGeocoderProcessorConfiguration.class);
    
	private static final String STATE_GEO_CACHE_KEY = "UT";
    private static final String INVENTORY_CACHE_KEY = "UT_INVENTORY";
    
	@Autowired
	private UtahInventoryGeocoderProcessorProperties properties;

    // Google Maps Geocoder API Context
    private GeoApiContext geoContext;
    
	@Autowired
	private RedisOperations<String, Object> redisOps;
	
	@Bean
	public RedisOperations<String, Object> redisTemplate(RedisConnectionFactory rcf) {
		final RedisTemplate<String, Object> template =  new RedisTemplate<String, Object>();
		template.setConnectionFactory(rcf);
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
		template.setHashKeySerializer(new StringRedisSerializer());

		return template;
	}
	
	@StreamListener
	@Output(Processor.OUTPUT)
    public Flux<ProductInventoryData> resolveAddressToLatLng(@Input(Processor.INPUT) Flux<ProductInventoryData> input) {
		return input.map(pid -> resolveLatLong(pid));
    }
	
	private ProductInventoryData resolveLatLong(ProductInventoryData pid) {
		StoreInventory storeInventory = pid.getStoreInventory();
		String storeID = storeInventory.getStoreID();
		
		if (storeInventory == null || storeID == null) {
			log.error("Invalid store for product CSC " + pid.getProduct().getCsc());
			return null;
		}

		List<Point> positions = redisOps.opsForGeo().position(STATE_GEO_CACHE_KEY, storeID);
		if (positions.isEmpty() || positions.get(0) == null) {
			String partialAddress = storeInventory.getStoreAddress();
			String city = storeInventory.getStoreCity();

			StringJoiner sj = new StringJoiner(",");
			String address = sj.add(partialAddress).add(city).add(STATE_GEO_CACHE_KEY).toString();
			
			GeocodingResult[] results = null;
			try {
				if (geoContext == null) {
					geoContext = new GeoApiContext.Builder().apiKey(properties.getGoogleMapsApiKey()).build();
				}
				
				results = GeocodingApi.geocode(geoContext, address).await();
			} catch (ApiException e) {
				log.error(e.toString());
				e.printStackTrace();
			} catch (InterruptedException e) {
				log.error(e.toString());
				e.printStackTrace();
			} catch (IOException e) {
				log.error(e.toString());
			}

			if (results != null) {
				storeInventory.getLocation().setLat(results[0].geometry.location.lat);
				storeInventory.getLocation().setLon(results[0].geometry.location.lng);
				
				log.info(address + " Lat/Lng: " + storeInventory.getLocation().getLat() + " / " + storeInventory.getLocation().getLon());			
			}
			
			redisOps.opsForGeo().add(STATE_GEO_CACHE_KEY, 
					new RedisGeoCommands.GeoLocation<Object>(storeID, 
							new Point(storeInventory.getLocation().getLon(), storeInventory.getLocation().getLat())));
		} else {
			Point pt = positions.get(0);
			storeInventory.getLocation().setLon(pt.getX());
			storeInventory.getLocation().setLat(pt.getY());
			
			log.info("Geocache hit for store: " + storeID + " @ " + pid.getStoreInventory().getStoreAddress());
		}

		// Cache the store inventory
		Map<String, ProductInventoryData> storeProducts = 
				(HashMap<String, ProductInventoryData>)redisOps.opsForHash().get(INVENTORY_CACHE_KEY, storeID);
		if (storeProducts == null) {
			storeProducts = new HashMap<String, ProductInventoryData>();
		}
			
		storeProducts.put(pid.getProduct().getName(), pid);
		redisOps.opsForHash().put(INVENTORY_CACHE_KEY, storeID, storeProducts);
		
		return pid;
	}
}
