package br.com.i2e.shop9.adapter.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.i2e.common.enums.I2EStatus;
import br.com.i2e.common.model.I2EMessage;
import br.com.i2e.shop9.adapter.repository.I2EMessageRepository;

@Service( "messageService" )
public class S9MessageService {

	@Autowired
	private CatalogService catalogoService;
	@Autowired
	private CustomerService customerService;
	@Autowired
	private AuxiliaryRegistryService auxService;
	@Autowired
	private I2EMessageRepository msgRepository;

	public void receive( I2EMessage msg ) {

		msg.setStatus( I2EStatus.PROCESSING );
		msgRepository.save( msg );

		switch ( msg.getType() ) {
		case REFRESH_CATALOG_BY_PERIOD:
			catalogoService.requestCatalogByPeriod( msg );
			break;
		case PRODUCT_DETAIL:
			receiveProduto( msg );
			break;
		 case BRANDS_BY_PERIOD:
			 auxService.requestMarcasByPeriod( msg );
			 break;
		case CUSTOMER_BY_PERIOD:
			customerService.requestCustomerByPeriod( msg );
			break;
		default:
			break;
		}
	}

	private void receiveProduto( I2EMessage msg ) {

		// var prd = JsonUtils.fromJson( msg.getInfo(), Produto.class );
		// catalogoService.receiveProduto( prd );
	}
}
