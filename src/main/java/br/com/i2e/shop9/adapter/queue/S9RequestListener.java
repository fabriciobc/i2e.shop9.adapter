package br.com.i2e.shop9.adapter.queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import br.com.i2e.common.model.I2EMessage;
import br.com.i2e.common.util.JsonUtils;
import br.com.i2e.shop9.adapter.service.S9MessageService;

@Component
public class S9RequestListener {

	private static final Logger logger = LoggerFactory.getLogger( S9RequestListener.class ); 

	private static final String I2E_BACKEND_REQUEST_QUEUE = "i2e.request.queue"; 
	
    @Autowired
    private S9MessageService messageService; 
    
	@RabbitListener( queues = I2E_BACKEND_REQUEST_QUEUE )
	public void onMessage( @Payload String jsonMessage ) {
		
		var msg =  JsonUtils.fromJson( jsonMessage, I2EMessage.class );
		messageService.receive( msg );
	}
}
