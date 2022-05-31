package br.com.i2e.shop9.adapter.client;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import br.com.i2e.shop9.model.AuxiliaryRegistry;
import br.com.i2e.shop9.model.Shop9AuxMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AuxiliaryRegistryClient {

	@Autowired
	private Shop9Client client;
	
	public CommandLineRunner getClasses() throws Exception {
		return args -> {
			Shop9AuxMessage classes = getAuxiliaryRegistry( AuxiliaryRegistry.Type.CLASSES 
					, LocalDate.of( 2000, 01, 01 ), LocalDate.now() 
			);
			log.debug( "Loading {}: {} ", classes.getTipo(), classes.getDados() );
		};
	}
	
	public CommandLineRunner getSubClasses() throws Exception {
		return args -> {
			Shop9AuxMessage subClasses = getAuxiliaryRegistry( AuxiliaryRegistry.Type.SUBCLASSES 
					, LocalDate.of( 2000, 01, 01 ), LocalDate.now() 
			);
			log.debug( "Loading {}: {} ", subClasses.getTipo(), subClasses.getDados() );
		};
	}
	
	public CommandLineRunner getGrupos() throws Exception {
		return args -> {
			Shop9AuxMessage grupos = getAuxiliaryRegistry( AuxiliaryRegistry.Type.GRUPOS 
					, LocalDate.of( 2000, 01, 01 ), LocalDate.now() 
			);
			log.debug( "Loading {}: {} ", grupos.getTipo(), grupos.getDados() );
		};
	}
	
	public List<AuxiliaryRegistry> getFamilias() {
		
		return getFamilias( LocalDate.of( 2000, 01, 01 ), LocalDate.now() );
	}
	
	public List<AuxiliaryRegistry> getFamilias( LocalDate dataDe, LocalDate dataAte ) {
		
		Shop9AuxMessage familias = getAuxiliaryRegistry( 
				AuxiliaryRegistry.Type.FAMILIAS, dataDe, dataAte );
		
		log.info( "Loading {}: {} ", familias.getTipo(), familias.getDados() );

		if ( familias.getDados() != null ) {
			familias.getDados().stream().forEach( f ->  {
				f.setType( AuxiliaryRegistry.Type.FAMILIAS );
			} );
			
			return familias.getDados();
		} else {
			
			return new ArrayList<AuxiliaryRegistry>(); 
		}
	}
	
	public CommandLineRunner getFabricantes() throws Exception {
		return args -> {
			Shop9AuxMessage fabricantes = getAuxiliaryRegistry( AuxiliaryRegistry.Type.FABRICANTES 
					, LocalDate.of( 2000, 01, 01 ), LocalDate.now() 
			);
			log.debug( "Loading {}: {} ", fabricantes.getTipo(), fabricantes.getDados() );
		};
	}
	
	public CommandLineRunner getUnidadesVenda() throws Exception {
		return args -> {
			Shop9AuxMessage unidadesVenda = getAuxiliaryRegistry( AuxiliaryRegistry.Type.UNIDADES_VENDA 
					, LocalDate.of( 2000, 01, 01 ), LocalDate.now() 
			);
			log.debug( "Loading {}: {} ", unidadesVenda.getTipo(), unidadesVenda.getDados() );
		};
	}
	
	public CommandLineRunner getCores() throws Exception {
		return args -> {
			Shop9AuxMessage cores = getAuxiliaryRegistry( AuxiliaryRegistry.Type.CORES );
			log.debug( "Loading {}: {} ", cores.getTipo(), cores.getDados() );
		};
	}
	
	public CommandLineRunner getTamanhos() throws Exception {
		return args -> {
			Shop9AuxMessage tamanhos = getAuxiliaryRegistry( AuxiliaryRegistry.Type.TAMANHOS );
			log.debug( "Loading {}: {} ", tamanhos.getTipo(), tamanhos.getDados() );
		};
	}

	public CommandLineRunner getMoedas() throws Exception {
		return args -> {
			Shop9AuxMessage moedas = getAuxiliaryRegistry( AuxiliaryRegistry.Type.MOEDAS );
			log.debug( "Loading {}: {} ", moedas.getTipo(), moedas.getDados() );
		};
	}

	public CommandLineRunner getPesquisa1() throws Exception {
		return args -> {
			Shop9AuxMessage pesquisa1 = getAuxiliaryRegistry( AuxiliaryRegistry.Type.PESQUISA_1 );
			log.debug( "Loading {}: {} ", pesquisa1.getTipo(), pesquisa1.getDados() );
		};
	}

	public CommandLineRunner getPesquisa2() throws Exception {
		return args -> {
			Shop9AuxMessage pesquisa2 = getAuxiliaryRegistry( AuxiliaryRegistry.Type.PESQUISA_2 );
			log.debug( "Loading {}: {} ", pesquisa2.getTipo(),  pesquisa2.getDados() );
		};
	}
	
	public CommandLineRunner getPesquisa3() throws Exception {
		return args -> {
			Shop9AuxMessage pesquisa3 = getAuxiliaryRegistry( AuxiliaryRegistry.Type.PESQUISA_3 );
			log.debug( "Loading {}: {} ", pesquisa3.getTipo(), pesquisa3.getDados() );
		};
	}
	
	private Shop9AuxMessage getAuxiliaryRegistry( AuxiliaryRegistry.Type type ) {

		return client.get( uriBuilder -> uriBuilder.path( "/aux/" + type.name().toLowerCase() ).build(),
				Optional.empty(),
			    Shop9AuxMessage.class );
	}
	
	private Shop9AuxMessage getAuxiliaryRegistry( AuxiliaryRegistry.Type type, LocalDate dateFrom, LocalDate dateTo ) {

		return client.get(	uriBuilder -> uriBuilder.path( "/aux/" + type.name().toLowerCase() )
						.queryParam( "datade", "{datade}" )
						.queryParam( "dataate", "{dataate}" )
						.build( dateFrom.format( DateTimeFormatter.ofPattern( "yyyy-MM-dd" ) ),
								dateTo.format( DateTimeFormatter.ofPattern( "yyyy-MM-dd" ) ) ),
				Optional.empty(), Shop9AuxMessage.class );
	}
}
