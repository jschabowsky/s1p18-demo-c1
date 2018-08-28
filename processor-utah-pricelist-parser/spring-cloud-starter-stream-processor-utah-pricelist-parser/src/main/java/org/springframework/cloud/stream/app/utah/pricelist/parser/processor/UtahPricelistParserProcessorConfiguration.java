/*
 * 
 * Copyright (c) 2018 Solace Corp.
 * 
 */
package org.springframework.cloud.stream.app.utah.pricelist.parser.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

import org.springframework.cloud.stream.binding.BinderAwareChannelResolver;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import reactor.core.publisher.Flux;

import com.solace.demo.utahdabc.datamodel.Product;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * SCS processor - pricelist parser - splits an HTML pricelist into individual products and outputs as JSON or a user-defined content type.
 *
 * @author Solace Corp
 */
@EnableBinding(Processor.class)
@EnableConfigurationProperties(UtahPricelistParserProcessorProperties.class)
public class UtahPricelistParserProcessorConfiguration {
	@Autowired
	private UtahPricelistParserProcessorProperties properties;

	@Autowired
    private BinderAwareChannelResolver resolver;
	
    private static final Logger log = LoggerFactory.getLogger(UtahPricelistParserProcessorConfiguration.class);

	@StreamListener
	@Output(Processor.OUTPUT)
    public Flux<Product> process(@Input(Processor.INPUT) Flux<Product> input) {
		return input.map(p -> {
			String classCode = p.getClass_code(); 
			if (classCode != null && !classCode.isEmpty()) {
				char[] codeArray = classCode.toCharArray();
				String target = properties.getPublishTopicPrefix();

				for (int i = 0; i < codeArray.length; i++) {
					target += Character.toString(codeArray[i]);
					if (i < codeArray.length - 1) {
						target += "/";
					}
				}
				
				log.info(target + ": " + p.getName());
				resolver.resolveDestination(target).send(MessageBuilder.withPayload(p).build());
			} else {
				log.error("No class code for product: " + p.getName() + " CSC: " + p.getCsc());
			}
			
			return p;
		});
    }
}
 