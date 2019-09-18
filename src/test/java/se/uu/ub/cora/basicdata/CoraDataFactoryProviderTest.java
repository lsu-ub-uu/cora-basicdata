package se.uu.ub.cora.basicdata;

import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataAtomicFactory;
import se.uu.ub.cora.data.DataFactoryProvider;
import se.uu.ub.cora.data.DataGroupFactory;
import se.uu.ub.cora.data.DataRecordFactory;
import se.uu.ub.cora.data.DataRecordLinkFactory;

public class CoraDataFactoryProviderTest {

	private DataFactoryProvider factoryProvider;

	@BeforeMethod
	public void setUp() {
		factoryProvider = new CoraDataFactoryProvider();
	}

	@Test
	public void testDataRecordFactory() {
		DataRecordFactory dataRecordFactory = factoryProvider.getDataRecordFactory();
		assertTrue(dataRecordFactory instanceof CoraDataRecordFactory);
	}

	@Test
	public void testDataGroupFactory() {
		DataGroupFactory dataGroupFactory = factoryProvider.getDataGroupFactory();
		assertTrue(dataGroupFactory instanceof CoraDataGroupFactory);
	}

	@Test
	public void testDataRecordLinkFactory() {
		DataRecordLinkFactory dataRecordLinkFactory = factoryProvider.getDataRecordLinkFactory();
		assertTrue(dataRecordLinkFactory instanceof CoraDataRecordLinkFactory);
	}

	@Test
	public void testDataAtomicFactory() {
		DataAtomicFactory dataAtomicFactory = factoryProvider.getDataAtomicFactory();
		assertTrue(dataAtomicFactory instanceof CoraDataAtomicFactory);
	}

}
