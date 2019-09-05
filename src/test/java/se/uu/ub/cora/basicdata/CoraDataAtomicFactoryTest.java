package se.uu.ub.cora.basicdata;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataAtomic;

public class CoraDataAtomicFactoryTest {

	@Test
	public void testFactorWithNameInData() {
		DataAtomicFactory factory = new CoraDataAtomicFactory();
		String nameInData = "someNameInData";
		String value = "someValue";
		DataAtomic dataAtomic = factory.factorUsingNameInDataAndValue(nameInData, value);
		assertTrue(dataAtomic instanceof CoraDataAtomic);

		assertEquals(dataAtomic.getNameInData(), nameInData);
		assertEquals(dataAtomic.getValue(), value);
	}

}
