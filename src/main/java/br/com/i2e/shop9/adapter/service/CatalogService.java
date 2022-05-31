package br.com.i2e.shop9.adapter.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

import br.com.i2e.common.enums.I2EStatus;
import br.com.i2e.common.enums.I2EType;
import br.com.i2e.common.dto.PeriodParamenterDTO;
import br.com.i2e.common.model.I2EMessage;
import br.com.i2e.common.model.catalog.Marca;
import br.com.i2e.common.util.JsonUtils;
import br.com.i2e.shop9.adapter.client.CatalogClient;
import br.com.i2e.shop9.adapter.processor.ProdutoProcessor;
import br.com.i2e.shop9.adapter.repository.I2EMessageRepository;
import br.com.i2e.shop9.model.AuxiliaryRegistry;
import br.com.i2e.shop9.model.ProdutoS9;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CatalogService {
	
	public final String I2E_RESPONSE_QUEUE = "i2e.response.queue";
	
	private Map<String, AuxiliaryRegistry> cacheFamilias;

	@Autowired
	CatalogClient catalogClient;
	
	@Autowired
	I2EMessageRepository msgRepository;
	
	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	public void receiveProduto( ProdutoS9 s9Produto ) {
		var i2eProduto = new ProdutoProcessor( s9Produto ).process();
		processaMarca( i2eProduto, s9Produto );
		log.info( "Processando Produto: {} -> {}",  s9Produto, i2eProduto );
		try {
			
			rabbitTemplate.convertAndSend( I2E_RESPONSE_QUEUE, JsonUtils.toJson( i2eProduto ) );
		} catch ( JsonProcessingException | AmqpException e ) {
			e.printStackTrace();
		}
	}

	private void processaMarca( br.com.i2e.common.model.catalog.Produto i2eProduto, ProdutoS9 s9Produto ) {
		
		
		var familia = cacheFamilias.get( String.valueOf( s9Produto.getCodigoFamilia() ) );
		var marca = new Marca();
		marca.setCodigo( familia.getCodigo() );
		marca.setNome( familia.getNome() );
		i2eProduto.setMarca( marca );
		
		log.info( "Processando Marca: {} -> {}",  s9Produto.getCodigoFamilia(), marca );
	}

	@PostConstruct
	public void postInitialized() {
		requestFamilias();
	}
	
	private void requestFamilias() {
		
//		var msg = new Shop9IntegrationMessage( CATEGORIA.FAMILIA );
//		try {
//			
//			rabbitTemplate.convertAndSend( SHOP9_REQUEST_QUEUE, JsonUtils.toJson( msg ) );
//		} catch ( JsonProcessingException | AmqpException e ) {
//			e.printStackTrace();
//		}
	}

	public void receiveFamilias( List<Map<String, Object>> familias ) {
		this.cacheFamilias = new HashMap<>();
		
		log.info( "Faminias<<<<<<: {} {}", familias.size(), familias );
		
		for (Map<String, Object> f: familias) {
			log.info( "Familia: {} ", f );
			var a = new AuxiliaryRegistry();
			a.setCodigo( String.valueOf( f.get("codigo") ) );
			a.setNome( String.valueOf( f.get("nome") ) );
			cacheFamilias.put( a.getCodigo(), a );
		}
	}

	public void requestCatalogByPeriod( I2EMessage msg ) {
		
		try {
			
			var period = JsonUtils.fromJson( msg.getParameters(), PeriodParamenterDTO.class );
			var produtos = catalogClient.fetchProdutos( period.getInitialDate(), period.getFinalalDate() );
			produtos.stream().forEach( p -> {
			
				try {
					
					var child = I2EMessage.getChild( msg, I2EType.PRODUCT_DETAIL );
					child.setStatus( I2EStatus.DELIVERED );
					child.setResponse( JsonUtils.toJson( new ProdutoProcessor( p ).process() ) );
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
