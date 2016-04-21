package de.hybris.platform.marketplaceintegration.service.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.marketplaceintegration.dao.MarketplaceintegrationDao;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.List;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.hybris.backoffice.model.MarketplaceModel;
import com.hybris.backoffice.model.MarketplaceStoreModel;


@IntegrationTest
public class MarketplaceintegrationServiceImplTest
{
	private Mock mock;

	@Mock
	private MarketplaceintegrationDao marketplaceintegrationDao;

	@Resource
	private MarketplaceintegrationServiceImpl mpService;

	@Mock
	private ModelService modelService;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		this.mpService = new MarketplaceintegrationServiceImpl();
		this.mpService.setMarketplaceintegrationDao(this.marketplaceintegrationDao);
		this.mpService.setModelService(this.modelService);
	}

	@Test
	public void testGetMarketplaceByPK()
	{
		Mockito.doReturn(null).when(this.marketplaceintegrationDao).getMarketplaceByPK("");
		final MarketplaceModel marketplace = this.mpService.getMarketplaceByPK("");
		Assert.assertEquals(null, marketplace);
	}

	@Test
	public void testGetMarketplaceStoreByPK()
	{
		Mockito.doReturn(null).when(this.marketplaceintegrationDao).getMarketplaceStoreByPK("");
		final MarketplaceStoreModel marketplaceStore = this.mpService.getMarketplaceStoreByPK("");
		Assert.assertEquals(null, marketplaceStore);
	}

	@Test
	public void testGetMarketplaceStoresByMarketplaceUrl()
	{
		Mockito.doReturn(null).when(this.marketplaceintegrationDao).getMarketplaceStoresByMarketplaceUrl("");
		final List marketplaceStores = this.mpService.getMarketplaceStoresByMarketplaceUrl("");
		Assert.assertEquals(null, marketplaceStores);
	}

	public MarketplaceintegrationDao getMarketplaceintegrationDao()
	{
		return this.marketplaceintegrationDao;
	}

	public void setMarketplaceintegrationDao(final MarketplaceintegrationDao marketplaceintegrationDao)
	{
		this.marketplaceintegrationDao = marketplaceintegrationDao;
	}

	public MarketplaceintegrationServiceImpl getMpService()
	{
		return this.mpService;
	}

	public void setMpService(final MarketplaceintegrationServiceImpl mpService)
	{
		this.mpService = mpService;
	}

	public ModelService getModelService()
	{
		return this.modelService;
	}

	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}
}