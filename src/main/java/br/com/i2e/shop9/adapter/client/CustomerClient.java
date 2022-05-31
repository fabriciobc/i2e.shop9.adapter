package br.com.i2e.shop9.adapter.client;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.i2e.shop9.model.ClienteS9;
import br.com.i2e.shop9.model.Shop9ClienteMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CustomerClient {
	@Autowired
	private Shop9Client shop9Client;
	
//	public List<AuxiliariRegistry> getAuxiliariRegistry() {
//		
//	}
	
	public List<ClienteS9> fetchClientes( LocalDate dataDe, LocalDate dataAte ) {
		
		var clientes = new ArrayList<ClienteS9>();
		int pag = 1;
		while ( pag <= 1 ) {
			final String uri = "/clientes/" + pag; 
			var msg = shop9Client.get( uriBuilder -> uriBuilder.path( uri ).build(),
					Optional.empty(), Shop9ClienteMessage.class );
			
			if ( "FIM_DE_PAGINA".equals( msg.getTipo() ) ) {
				
				break;
			}
			
			if ( msg.isSucesso() ) {

				clientes.addAll( msg.getDados() );
				pag++;
			} else {
				
				log.error( "Erro ao processar requisição {} ", msg.getTipo() );
				break;
			}
		}
		
		return clientes;
	}
}
