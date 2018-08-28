/*
 * 
 * Copyright (c) 2018 Solace Corp.
 * 
 */
package org.springframework.cloud.stream.app.utah.pricelist.loader.processor;

import java.text.NumberFormat;
import java.text.ParseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.cloud.stream.reactive.FluxSender;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.annotation.StreamListener;

import org.springframework.web.client.RestTemplate;
import com.solace.demo.utahdabc.datamodel.Product;

import reactor.core.publisher.Flux;


/**
 * SCS processor - pricelist loader - loads HTML price data from a URL.
 *
 * @author Solace Corp
 */
@EnableBinding(Processor.class)
@EnableConfigurationProperties(UtahPricelistLoaderProcessorProperties.class)
public class UtahPricelistLoaderProcessorConfiguration {
	@Autowired
	private UtahPricelistLoaderProcessorProperties properties;
    
    private static final Logger log = LoggerFactory.getLogger(UtahPricelistLoaderProcessorConfiguration.class);
    
	@StreamListener
	public void process(@Input(Processor.INPUT) Flux<String> input, @Output(Processor.OUTPUT) FluxSender output) throws ParseException {
		for(Product p : getProductList(properties.getProcessorUrl())) {
			output.send(input.map(x -> p));
		}
	}
	
	private List<Product> getProductList(String url) {
		List<Product> productList = new ArrayList<Product>();
		RestTemplate restTemplate = new RestTemplate();
		
		try {
			String quote = restTemplate.getForObject(url, String.class);
			Document doc = Jsoup.parse(quote);
			Elements tables = doc.select("table");
			for (Element element : tables) {
				Elements trs = element.select("tr");
				for (Element tr : trs) {
					Product p = new Product();
					Elements tds = tr.select("td");
					if (tds.size() != 0) {
						p.setName(tds.get(0).text());
						p.setDiv_code(tds.get(1).text());
						p.setDept_code(tds.get(2).text());
						p.setClass_code(tds.get(3).text());
						p.setSize(Integer.parseInt(tds.get(4).text()));
						p.setCsc(Integer.parseInt(tds.get(5).text()));
						p.setPrice(NumberFormat.getCurrencyInstance(Locale.US).parse(tds.get(6).text()).doubleValue());
						p.setStatus(tds.get(7).text());
						p.setSPA(tds.get(8).text());
						productList.add(p);
						log.info("Added product: " + p.getName());
					}
				}
			}
		} catch (ParseException pe) {
			log.error(pe.toString());
		}
		
		return productList;
	}	
}
