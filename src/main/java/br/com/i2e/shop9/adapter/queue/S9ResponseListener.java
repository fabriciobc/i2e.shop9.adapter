package br.com.i2e.shop9.adapter.queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import br.com.i2e.common.util.JsonUtils;
import br.com.i2e.shop9.adapter.service.S9MessageService;
import br.com.i2e.shop9.model.Shop9IntegrationMessage;

@Component
public class S9ResponseListener {

	private static final Logger logger = LoggerFactory.getLogger( S9ResponseListener.class ); 

	public final String SHOP9_RESPONSE_QUEUE = "shop9.response.queue";
	
    @Autowired
    private S9MessageService messageService; 
    
//	@RabbitListener( queues = SHOP9_RESPONSE_QUEUE )
	public void onMessage( @Payload String jsonMessage ) {
		
		var msg =  JsonUtils.fromJson( jsonMessage, Shop9IntegrationMessage.class );
//		messageService.receive( msg );
	}
}
