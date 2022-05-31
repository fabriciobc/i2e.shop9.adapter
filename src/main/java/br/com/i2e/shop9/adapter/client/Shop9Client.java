package br.com.i2e.shop9.adapter.client;



import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import javax.annotation.PostConstruct;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;

import br.com.i2e.shop9.model.AuthenticationToken;
import br.com.i2e.shop9.model.AuxiliaryRegistry;
import br.com.i2e.shop9.model.FotosProduto;
import br.com.i2e.shop9.model.ProdutoS9;
import br.com.i2e.shop9.model.ProdutoS9Foto;
import br.com.i2e.shop9.model.Shop9AuthMessage;
import br.com.i2e.shop9.model.Shop9AuxMessage;
import br.com.i2e.shop9.model.Shop9ClienteMessage;
import br.com.i2e.shop9.model.Shop9FotosProdutoMessage;
import br.com.i2e.shop9.model.Shop9Message;
import br.com.i2e.shop9.model.Shop9ProdutoDetalheMessage;
import br.com.i2e.shop9.model.Shop9ProdutoMessage;
import reactor.core.publisher.Mono;

@Component
public class Shop9Client {
	
	private static final Logger logger = LoggerFactory.getLogger( Shop9Client.class );
	
	private static int EXPIRE_OFFSET = 1000;	
	@Autowired
	private Environment env;
	@Autowired
	private SignatureUtil signatureUtil;
	
	private WebClient webClient;
	
	private Optional<AuthenticationToken> token = Optional.ofNullable( null );
	
	@PostConstruct
	private void init() {
		 SslContextFactory.Client sslContextFactory = new SslContextFactory.Client();
		 HttpClient httpClient = new HttpClient(sslContextFactory) {
			
		     @Override
		     public Request newRequest(URI uri) {
		         Request request = super.newRequest(uri);
		         return enhance(request);
		     }
		     
		     Request enhance(Request request) {
		    	    StringBuilder group = new StringBuilder();
		    	    request.onRequestBegin(theRequest -> {
		    	        // append request url and method to group
		    	    });
		    	    request.onRequestHeaders(theRequest -> {
//		    	        for (HttpField header : theRequest.getHeaders()) {
//		    	            // append request headers to group
//		    	        }
		    	    });
		    	    request.onRequestContent((theRequest, content) -> {
//		    	        logger.info(  "Body: " + content.array() );
		    	    });
		    	    request.onRequestSuccess(theRequest -> {
		    	        
		    	        group.delete(0, group.length());
		    	    });
		    	    group.append("\n");
		    	    request.onResponseBegin(theResponse -> {
		    	        // append response status to group
		    	    });
		    	    request.onResponseHeaders(theResponse -> {
//		    	        for (HttpField header : theResponse.getHeaders()) {
//		    	            logger.info(  header.getValue() );
//		    	        }
		    	    });
		    	    request.onResponseContent((theResponse, content) -> {
		    	        logger.info(  "Response: " + StandardCharsets.UTF_8.decode(content).toString() );
		    	    });
		    	    request.onResponseSuccess(theResponse -> {
		    	        
		    	    });
		    	    return request;
		    	}
		 };
		 webClient = WebClient.builder()
				 // TODO add property to parametrise that 
//				  	.clientConnector(new JettyClientHttpConnector(httpClient))
				  	.baseUrl( env.getProperty( "i2e.shop9.url" ) )
				  .build();
		 
	}
	
	public void fetchClientes() throws Exception {
		
		int pag = 1;
		while ( pag <= 1 ) {
			final String uri = "/clientes/" + pag; 
			var msg = get( uriBuilder -> uriBuilder.path( uri ).build(),
					Optional.empty(), Shop9ClienteMessage.class );
			
			if ( msg.isSucesso() ) {

				msg.getDados().stream().forEach( c ->  logger.info("Cliente: {}",  c.toString() ) );
				pag++;
			} else {
				
				logger.info( "Tipo msg: {}", msg.getTipo() );
				break;
			}
		}
	}
	
	public void getClasses() throws Exception {
		
		Shop9AuxMessage classes = getAuxiliaryRegistry( AuxiliaryRegistry.Type.CLASSES, 
				LocalDate.of( 2000, 01, 01 ), LocalDate.now() 
		);
		logger.info(">>>>>>>>>>>  Classes: {}", classes.getDados() );
	}
	
	public void getSubClasses() throws Exception {

		Shop9AuxMessage subClasses = getAuxiliaryRegistry( AuxiliaryRegistry.Type.SUBCLASSES 
				, LocalDate.of( 2000, 01, 01 ), LocalDate.now() 
		);
		logger.info(">>>>>>>>>>>  SubClasses: {}",   subClasses.getDados() );
	}
	
	public void getGrupos() throws Exception {
		Shop9AuxMessage grupos = getAuxiliaryRegistry( AuxiliaryRegistry.Type.GRUPOS 
				, LocalDate.of( 2000, 01, 01 ), LocalDate.now() 
		);
		logger.info(">>>>>>>>>>>  Grupos: {}", grupos.getDados() );
	}
	
	public List<AuxiliaryRegistry> fetchFamilias() {
		
		Shop9AuxMessage familias = getAuxiliaryRegistry( AuxiliaryRegistry.Type.FAMILIAS, 
				LocalDate.of( 2000, 01, 01 ), LocalDate.now() 
		);
		logger.info( ">>>>>>>>>>>  Familias: {}", familias.getDados() );
		
		return familias.getDados();
	}
	
	public void getFabricantes() throws Exception {
		Shop9AuxMessage fabricantes = getAuxiliaryRegistry( AuxiliaryRegistry.Type.FABRICANTES 
				, LocalDate.of( 2000, 01, 01 ), LocalDate.now() 
		);
		logger.info(">>>>>>>>>>>  Fabricantes: {}",   fabricantes.getDados() );
	}
	
	public void getUnidadesVenda() throws Exception {
		Shop9AuxMessage unidadesVenda = getAuxiliaryRegistry( AuxiliaryRegistry.Type.UNIDADES_VENDA 
				, LocalDate.of( 2000, 01, 01 ), LocalDate.now() 
		);
		logger.info(">>>>>>>>>>>  Unidades: {}",   unidadesVenda.getDados() );
	}
	
	public void getCores() throws Exception {
		Shop9AuxMessage cores = getAuxiliaryRegistry( AuxiliaryRegistry.Type.CORES );
		logger.info(">>>>>>>>>>>  Cores: {}", cores.getDados() );
	}
	
	public void getTamanhos() throws Exception {
		Shop9AuxMessage tamanhos = getAuxiliaryRegistry( AuxiliaryRegistry.Type.TAMANHOS );
		logger.info(">>>>>>>>>>>  Tamanhos: {}", tamanhos.getDados() );
	}

	public void getMoedas() throws Exception {
		Shop9AuxMessage moedas = getAuxiliaryRegistry( AuxiliaryRegistry.Type.MOEDAS );
		logger.info(">>>>>>>>>>>  Moedas: {}", moedas.getDados() );
	}

	public void getPesquisa1() throws Exception {
		Shop9AuxMessage pesquisa1 = getAuxiliaryRegistry( AuxiliaryRegistry.Type.PESQUISA_1 );
		logger.info( ">>>>>>>>>>>  Pesquisa1: {}", pesquisa1.getDados() );
	}

	public void getPesquisa2() throws Exception {
		Shop9AuxMessage pesquisa2 = getAuxiliaryRegistry( AuxiliaryRegistry.Type.PESQUISA_2 );
		logger.info(">>>>>>>>>>>  Pesquisa2: {}", pesquisa2.getDados() );
	}
	
	public void getPesquisa3() throws Exception {
		Shop9AuxMessage pesquisa3 = getAuxiliaryRegistry( AuxiliaryRegistry.Type.PESQUISA_3 );
		logger.info(">>>>>>>>>>>  Pesquisa3: {}", pesquisa3.getDados() );
	}
	
	private Shop9AuxMessage getAuxiliaryRegistry( AuxiliaryRegistry.Type type ) {

		return get( uriBuilder -> uriBuilder.path( "/aux/" + type.name().toLowerCase() ).build(),
				Optional.empty(),
			    Shop9AuxMessage.class );
	}
	
	private Shop9AuxMessage getAuxiliaryRegistry( AuxiliaryRegistry.Type type, LocalDate dateFrom, LocalDate dateTo ) {

		return get(	uriBuilder -> uriBuilder.path( "/aux/" + type.name().toLowerCase() )
						.queryParam( "datade", "{datade}" )
						.queryParam( "dataate", "{dataate}" )
						.build( dateFrom.format( DateTimeFormatter.ofPattern( "yyyy-MM-dd" ) ),
								dateTo.format( DateTimeFormatter.ofPattern( "yyyy-MM-dd" ) ) ),
				Optional.empty(), Shop9AuxMessage.class );
	}
	
	public <E extends Shop9Message> E get( Function<UriBuilder, URI> uri, Optional<String> payload, Class<E> messageType ) {
		var sig = signatureUtil.assign( "get", payload );
		
		Mono<E> resp = webClient.get().uri( uri )
				.header( "Signature", sig.getToken() )
				.header( "CodFilial", env.getProperty( "i2e.shop9.filial" ) )
				.header( "Authorization", "Token " + authenticationToken().getToken() )
				.header( "Timestamp", sig.getTimestamp() )
				.header( "Accept", "application/json;charset=UTF-8" )
				.retrieve().bodyToMono( messageType );
		
		var sm = resp.block();
		logger.debug( sm.getMensagem() );
		return sm;
	}
	
	public byte[] getImage( Function<UriBuilder, URI> uri ) {
		var sig = signatureUtil.assign( "get", Optional.empty() );
		
		Mono<byte[]> resp = webClient.get().uri( uri )
				.header( "Signature", sig.getToken() )
				.header( "CodFilial", env.getProperty( "i2e.shop9.filial" ) )
				.header( "Authorization", "Token " + authenticationToken().getToken() )
				.header( "Timestamp", sig.getTimestamp() )
				.retrieve().bodyToMono( byte[].class );
		
		var sm = resp.block();
//		logger.info(  sm.getMensagem() );
		return sm;
	}
	
	private <E extends Shop9Message> E post( Function<UriBuilder, URI> uri, Optional<String> payload, Class<E> messageType ) {
		var sig = signatureUtil.assign( "get", payload );
		
		Mono<E> resp = webClient.post().uri( uri )
				.header( "Signature", sig.getToken() )
				.header( "CodFilial", env.getProperty( "i2e.shop9.filial" ) )
				.header( "Authorization", "Token " + authenticationToken().getToken() )
				.header( "Timestamp", sig.getTimestamp() )
				.header( "Accept", "application/json;charset=UTF-8" )
				.bodyValue( payload )
				.retrieve().bodyToMono( messageType );
		
		var sm = resp.block();
		System.out.println( sm.getMensagem() );
		return sm;
	}

	private AuthenticationToken authenticationToken() {
		
		if ( token.isEmpty() || token.get().getExpireAt().isBefore( ZonedDateTime.now( ZoneId.of( "UTC" ) ).plusMinutes( EXPIRE_OFFSET ) ) ) {
			
			Mono<Shop9AuthMessage> resp = webClient.get().uri( uriBuilder -> uriBuilder
					    .path("/auth/")
					    .queryParam("serie", "{serie}")
					    .queryParam("codfilial", "{codfilial}")
					    .build( env.getProperty( "i2e.shop9.serie" ), env.getProperty( "i2e.shop9.filial" ) )					    
			   ).retrieve().bodyToMono( Shop9AuthMessage.class );
			
			var sam = resp.block();
			
			this.token = Optional.of( sam.getDados() ); 
		}
		
		return token.get();
	}
}