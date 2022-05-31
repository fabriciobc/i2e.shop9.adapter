package br.com.i2e.shop9.adapter.processor;

import br.com.i2e.shop9.model.AuxiliaryRegistry;

public class MarcaProcessor {
	
	private AuxiliaryRegistry aux;
	public MarcaProcessor(AuxiliaryRegistry aux) {
		this.aux = aux;
	}
	
	public br.com.i2e.common.model.catalog.Marca process() {
		
		return new br.com.i2e.common.model.catalog.Marca( aux.getCodigo(), aux.getNome() );
	}
}