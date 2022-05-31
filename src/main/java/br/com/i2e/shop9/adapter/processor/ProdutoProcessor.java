package br.com.i2e.shop9.adapter.processor;

import java.util.ArrayList;
import br.com.i2e.common.model.catalog.Foto;
import br.com.i2e.common.model.catalog.Marca;
import br.com.i2e.shop9.model.ProdutoS9;

public class ProdutoProcessor {

	private ProdutoS9 s9Produto;

	public ProdutoProcessor( ProdutoS9 s9Produto ) {
		this.s9Produto = s9Produto;
	}

	public br.com.i2e.common.model.catalog.Produto process() {
		br.com.i2e.common.model.catalog.Produto i2eProduto = new br.com.i2e.common.model.catalog.Produto();
		i2eProduto.setNome(s9Produto.getNome());
		i2eProduto.setDescricao(s9Produto.getObservacao1());
		i2eProduto.setDescricaoResumida(s9Produto.getNome());
		i2eProduto.setCodigoSKU(s9Produto.getCodigo());
		i2eProduto.setCodigoBarras(s9Produto.getCodigoBarras());
		i2eProduto.setPeso(s9Produto.getPesoBruto());
//		i2eProduto.setNcm(s9Produto.getn);
		i2eProduto.setComprimento( s9Produto.getComprimento());
		i2eProduto.setAltura( s9Produto.getAltura());
		i2eProduto.setLargura( s9Produto.getLargura());
		i2eProduto.setMarca( s9Produto.getCodigoFamilia() == null ? 
				null : new Marca( String.valueOf( s9Produto.getCodigoFamilia() ) ) );
//		i2eProduto.setfFornecedor(s9Produto.get);
		
		if ( s9Produto.getFotos() != null ) {
			
			i2eProduto.setFotos( new ArrayList<Foto>() );
			s9Produto.getFotos().stream().forEach( f -> {
				
				i2eProduto.getFotos().add( new Foto( null, f.getPosicao(), f.isPrincipal(),  f.getFoto(), i2eProduto ) ); 
			});
		}
		
		return i2eProduto;
	}
}
