package br.com.i2e.shop9.adapter.service;

import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;

import br.com.i2e.common.dto.PeriodParamenterDTO;
import br.com.i2e.common.enums.I2EStatus;
import br.com.i2e.common.enums.I2EType;
import br.com.i2e.common.model.Cliente;
import br.com.i2e.common.model.I2EMessage;
import br.com.i2e.common.util.JsonUtils;
import br.com.i2e.shop9.adapter.client.CustomerClient;
import br.com.i2e.shop9.adapter.repository.I2EMessageRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service@Transactional
public class CustomerService {

	public final String I2E_RESPONSE_QUEUE = "i2e.response.queue";
	
	@Autowired
	CustomerClient customerClient;
	
	@Autowired
	I2EMessageRepository msgRepository;
	
	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	@Autowired
	private ModelMapper modelMapper; 
	
	public void requestCustomerByPeriod(I2EMessage msg) {
		
		try {
			
			var period = JsonUtils.fromJson( msg.getParameters(), PeriodParamenterDTO.class );
			var clientes = customerClient.fetchClientes( period.getInitialDate(), period.getFinalalDate() );
			clientes.stream().forEach( c -> {
			
				try {
					
					var customer = modelMapper.map( c, Cliente.class );
					var child = I2EMessage.getChild( msg, I2EType.CUSTOMER_DETAIL );
					child.setStatus( I2EStatus.DELIVERED );
					child.setResponse( JsonUtils.toJson( customer ) );
					msgRepository.save( child );
					
					rabbitTemplate.convertAndSend( I2E_RESPONSE_QUEUE, JsonUtils.toJson( child ) );
				} catch ( JsonProcessingException e ) {
					
					log.error( "Erro ao converter Response ", e );
				}
			} );
			
			msg.setStatus( I2EStatus.DELIVERED );
			msgRepository.save( msg );
			
			rabbitTemplate.convertAndSend( I2E_RESPONSE_QUEUE, JsonUtils.toJson( msg ) );
		} catch ( JsonProcessingException e ) {
		
			log.error( "Erro ao extrair parâmetros ", e );
			msg.setError( e.toString() );
			msgRepository.save( msg );
		}
	}
}
