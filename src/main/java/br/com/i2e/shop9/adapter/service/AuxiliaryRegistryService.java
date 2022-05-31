package br.com.i2e.shop9.adapter.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;

import br.com.i2e.common.dto.PeriodParamenterDTO;
import br.com.i2e.common.enums.I2EStatus;
import br.com.i2e.common.enums.I2EType;
import br.com.i2e.common.model.I2EMessage;
import br.com.i2e.common.util.JsonUtils;
import br.com.i2e.shop9.adapter.client.AuxiliaryRegistryClient;
import br.com.i2e.shop9.adapter.processor.MarcaProcessor;
import br.com.i2e.shop9.adapter.processor.ProdutoProcessor;
import br.com.i2e.shop9.adapter.repository.I2EMessageRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service@Transactional
public class AuxiliaryRegistryService {

	public final String I2E_RESPONSE_QUEUE = "i2e.response.queue";
	
	@Autowired
	private AuxiliaryRegistryClient auxClient;
	@Autowired
	private I2EMessageRepository msgRepository;
	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	public void requestMarcasByPeriod( I2EMessage msg ) {
		try {
			
			var period = JsonUtils.fromJson( msg.getParameters(), PeriodParamenterDTO.class );
			var familias = auxClient.getFamilias( period.getInitialDate(), period.getFinalalDate() );
			familias.stream().forEach( f -> {
			
				try {
					
					var child = I2EMessage.getChild( msg, I2EType.BRAND_DETAIL );
					child.setStatus( I2EStatus.DELIVERED );
					child.setResponse( JsonUtils.toJson( new MarcaProcessor( f ).process() ) );
					msgRepository.save( child );
					
					rabbitTemplate.convertAndSend( I2E_RESPONSE_QUEUE, JsonUtils.toJson( child ) );
				} catch ( JsonProcessingException e ) {
					
					log.error( "Erro ao converter Response ", e );
				}
			});
			
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