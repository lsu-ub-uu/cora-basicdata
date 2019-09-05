package se.uu.ub.cora.basicdata;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataGroup;

public class CoraDataGroupFactoryTest {
	String nameInData = "someNameInData";

	@Test
	public void testFactorWithNameInData() {
		DataGroupFactory factory = new CoraDataGroupFactory();
		DataGroup dataGroup = factory.factorUsingNameInData(nameInData);
		assertTrue(dataGroup instanceof CoraDataGroup);
		assertEquals(dataGroup.getNameInData(), nameInData);
	}

	@Test
	public void testFactorAsLinkWithNameInDataAndTypeAndId() {
		DataGroupFactory factory = new CoraDataGroupFactory();
		String recordType = "someRecordType";
		String recordId = "someRecordId";

		DataGroup dataGroup = factory.factorAsLinkWithNameInDataTypeAndId(nameInData, recordType,
				recordId);
		assertTrue(dataGroup instanceof CoraDataGroup);

		String linkedRecordType = dataGroup.getFirstAtomicValueWithNameInData("linkedRecordType");
		String linkedRecordId = dataGroup.getFirstAtomicValueWithNameInData("linkedRecordId");

		assertEquals(dataGroup.getNameInData(), nameInData);
		assertEquals(linkedRecordType, recordType);
		assertEquals(linkedRecordId, recordId);

	}
}
