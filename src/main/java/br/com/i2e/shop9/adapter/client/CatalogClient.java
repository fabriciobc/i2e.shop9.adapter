package br.com.i2e.shop9.adapter.client;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.i2e.shop9.model.FotosProduto;
import br.com.i2e.shop9.model.ProdutoS9;
import br.com.i2e.shop9.model.ProdutoS9Foto;
import br.com.i2e.shop9.model.Shop9FotosProdutoMessage;
import br.com.i2e.shop9.model.Shop9ProdutoDetalheMessage;
import br.com.i2e.shop9.model.Shop9ProdutoMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CatalogClient {
	@Autowired
	private Shop9Client shop9Client;
	
//	public List<AuxiliariRegistry> getAuxiliariRegistry() {
//		
//	}
	
	public List<ProdutoS9> fetchProdutos( LocalDate dataDe, LocalDate dataAte ) {
		
		int pag = 1;
		List<ProdutoS9> produtos = new ArrayList<>();
		while ( true ) {
			log.info( ">>>>>>>>>>>  Processando pag {} ", pag ); 
			final String uri = "/produtos/" + pag; 
			var msg = shop9Client.get( uriBuilder -> uriBuilder.path( uri ).build(),
					Optional.empty(), Shop9ProdutoMessage.class );
			
			if ( "FIM_DE_PAGINA".equals( msg.getTipo() ) ) {
				
				break;
			}
			
			if ( msg.isSucesso() ) {

				msg.getDados().stream().forEach( p ->  { 
					
					log.info( ">>>>>>>>>>>  Produto: {}" , p ); 
					produtos.add( p );
				});
				pag++;
			} else {
				
				log.info( "Tipo msg: {}", msg.getTipo() );
				break;
			}
		}
		
		produtos.stream().forEach( p -> fetchFotos( p ) );
		
		return produtos;
	}
	
	public ProdutoS9 fetchDetalheProduto( String codigo ) {
		// TODO
		// Definir um response writer para escrever na fila de resposta o resultado de cada consulta
		// Esse ResposnseWirter deve ser passado para o método get que não bloqueará a request
		
		final String uri = "/produtos/detalhes/" + codigo; 
		var msg = shop9Client.get( uriBuilder -> uriBuilder.path( uri ).build(),
				Optional.empty(), Shop9ProdutoDetalheMessage.class );
		ProdutoS9 p = msg.getDados();
		if ( msg.isSucesso() ) {
			
			fetchFotos( p );
		}
		
		log.info( ">>>>>>>>>>> Fetched Produto: {} - {}" , p.getCodigo(), p.getNome() );
		return p;
	}
	 
	private void fetchFotos( ProdutoS9 p ) {
		
		var mfp = shop9Client.get( uriBuilder -> uriBuilder.path( "/fotos/" + p.getCodigo() ).build(),
				Optional.empty(), Shop9FotosProdutoMessage.class );
		
		if ( mfp.isSucesso() ) {

			List<ProdutoS9Foto> fotos = new ArrayList<>();
			FotosProduto fp = mfp.getDados();
			fp.getFotos().forEach( pos -> {
				
				byte[] mf = shop9Client.getImage( uriBuilder -> uriBuilder.path( "/fotos/" + fp.getCodigo() + "/" + pos.getPosicao() ).build() );
				fotos.add( new ProdutoS9Foto( mf, pos.getPosicao(), pos.getPrincipal() ) );
				log.info( "Foto produto {}, pos {}", p.getNome(), pos.getPosicao() );
			} );
			p.setFotos( fotos );
		}
	}	
}
