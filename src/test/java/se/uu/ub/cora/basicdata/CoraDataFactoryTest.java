/*
 * Copyright 2019, 2022 Uppsala University Library
 * Copyright 2022 Olov McKie
 * 
 * This file is part of Cora.
 *
 *     Cora is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Cora is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Cora.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.uu.ub.cora.basicdata;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.util.Collection;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.basicdata.data.CoraDataAtomic;
import se.uu.ub.cora.basicdata.data.CoraDataAttribute;
import se.uu.ub.cora.basicdata.data.CoraDataChildFilter;
import se.uu.ub.cora.basicdata.data.CoraDataGroup;
import se.uu.ub.cora.basicdata.data.CoraDataList;
import se.uu.ub.cora.basicdata.data.CoraDataRecord;
import se.uu.ub.cora.basicdata.data.CoraDataRecordGroup;
import se.uu.ub.cora.basicdata.data.CoraDataRecordLink;
import se.uu.ub.cora.basicdata.data.CoraDataResourceLink;
import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataAttribute;
import se.uu.ub.cora.data.DataFactory;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataList;
import se.uu.ub.cora.data.DataRecord;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.data.DataRecordLink;
import se.uu.ub.cora.data.DataResourceLink;

public class CoraDataFactoryTest {
	private DataFactory dataFactory;
	private String containDataOfType = "someType";
	private String nameInData = "nameInData";
	private String mimeType = "someMimeType";
	private String recordType = "someRecordType";
	private String recordId = "someRecordId";
	private String value = "someValue";

	@BeforeMethod
	public void setUp() {
		dataFactory = new CoraDataFactory();
	}

	@Test
	public void testFactorListUsingNameOfDataType() {
		DataList factoredDataList = dataFactory.factorListUsingNameOfDataType(containDataOfType);

		assertTrue(factoredDataList instanceof CoraDataList);
		assertEquals(factoredDataList.getContainDataOfType(), containDataOfType);
	}

	@Test
	public void testFactorRecordUsingDataRecordGroup() {
		DataRecordGroup dataRecordGroup = CoraDataRecordGroup.withNameInData("someNameInData");
		DataRecord factoredDataRecord = dataFactory
				.factorRecordUsingDataRecordGroup(dataRecordGroup);
		assertTrue(factoredDataRecord instanceof CoraDataRecord);
		assertSame(factoredDataRecord.getDataRecordGroup(), dataRecordGroup);
	}

	@Test
	public void testFactorRecordGroupUsingNameInData() {
		DataRecordGroup factoredDataRecordGroup = dataFactory
				.factorRecordGroupUsingNameInData(nameInData);
		assertTrue(factoredDataRecordGroup instanceof CoraDataRecordGroup);
		assertEquals(factoredDataRecordGroup.getNameInData(), nameInData);
	}

	@Test
	public void testFactorRecordGroupFromDataGroup() {
		DataGroup dataGroup = CoraDataGroup.withNameInData(nameInData);
		dataGroup.addChild(CoraDataAtomic.withNameInDataAndValue("atomic", "aValue"));
		dataGroup.addAttributeByIdWithValue("attribute", "atValue");

		DataRecordGroup factoredDataRecordGroup = dataFactory
				.factorRecordGroupFromDataGroup(dataGroup);

		assertTrue(factoredDataRecordGroup instanceof CoraDataRecordGroup);
		assertEquals(factoredDataRecordGroup.getNameInData(), nameInData);
		assertSame(factoredDataRecordGroup.getChildren().size(), dataGroup.getChildren().size());
		assertSame(factoredDataRecordGroup.getFirstChildWithNameInData("atomic"),
				dataGroup.getFirstChildWithNameInData("atomic"));
		Collection<DataAttribute> attributes = factoredDataRecordGroup.getAttributes();
		assertEquals(attributes.size(), 1);
		DataAttribute attribute = factoredDataRecordGroup.getAttribute("attribute");
		assertEquals(attribute.getValue(), "atValue");
	}

	@Test
	public void testFactorGroupFromDataRecordGroup() {
		DataRecordGroup dataRecordGroup = CoraDataRecordGroup.withNameInData(nameInData);
		dataRecordGroup.addChild(CoraDataAtomic.withNameInDataAndValue("atomic", "aValue"));
		dataRecordGroup.addAttributeByIdWithValue("attribute", "atValue");

		DataGroup factoredDataGroup = dataFactory.factorGroupFromDataRecordGroup(dataRecordGroup);

		assertTrue(factoredDataGroup instanceof CoraDataGroup);
		assertEquals(factoredDataGroup.getNameInData(), nameInData);
		assertSame(factoredDataGroup.getChildren().size(), dataRecordGroup.getChildren().size());
		assertSame(factoredDataGroup.getFirstChildWithNameInData("atomic"),
				dataRecordGroup.getFirstChildWithNameInData("atomic"));
		Collection<DataAttribute> attributes = factoredDataGroup.getAttributes();
		assertEquals(attributes.size(), 1);
		DataAttribute attribute = factoredDataGroup.getAttribute("attribute");
		assertEquals(attribute.getValue(), "atValue");
	}

	@Test
	public void testFactorGroupUsingNameInData() {
		DataGroup factoredDataGroup = dataFactory.factorGroupUsingNameInData(nameInData);
		assertTrue(factoredDataGroup instanceof CoraDataGroup);
		assertEquals(factoredDataGroup.getNameInData(), nameInData);
	}

	@Test
	public void testFactorRecordLinkUsingNameInData() {
		DataRecordLink factoredDataRecordLink = dataFactory
				.factorRecordLinkUsingNameInData(nameInData);

		assertTrue(factoredDataRecordLink instanceof CoraDataRecordLink);
		assertEquals(factoredDataRecordLink.getNameInData(), nameInData);
	}

	@Test
	public void testFactorRecordLinkUsingNameInDataAndTypeAndId() {
		DataRecordLink factoredDataRecordLink = dataFactory
				.factorRecordLinkUsingNameInDataAndTypeAndId(nameInData, recordType, recordId);
		assertEquals(factoredDataRecordLink.getNameInData(), nameInData);
		assertEquals(factoredDataRecordLink.getLinkedRecordType(), recordType);
		assertEquals(factoredDataRecordLink.getLinkedRecordId(), recordId);
	}

	@Test
	public void testFactorResourceLinkUsingNameInData() {
		DataResourceLink factoredDataResourceLink = dataFactory
				.factorResourceLinkUsingNameInDataAndMimeType(nameInData, mimeType);
		assertTrue(factoredDataResourceLink instanceof CoraDataResourceLink);
		assertEquals(factoredDataResourceLink.getNameInData(), nameInData);
		assertEquals(factoredDataResourceLink.getMimeType(), mimeType);
	}

	@Test
	public void testFactorAtomicUsingNameInDataAndValue() {
		DataAtomic factoredDataAtomic = dataFactory.factorAtomicUsingNameInDataAndValue(nameInData,
				value);
		assertCorrectBasicDataAtomic(factoredDataAtomic);
	}

	private void assertCorrectBasicDataAtomic(DataAtomic factoredDataAtomic) {
		assertTrue(factoredDataAtomic instanceof CoraDataAtomic);
		assertEquals(factoredDataAtomic.getNameInData(), nameInData);
		assertEquals(factoredDataAtomic.getValue(), value);
	}

	@Test
	public void testFactorAtomciUsingNameInDataAndValueAndRepeatId() {
		String repeatId = "r1";
		DataAtomic factoredDataAtomic = dataFactory
				.factorAtomicUsingNameInDataAndValueAndRepeatId(nameInData, value, repeatId);
		assertCorrectBasicDataAtomic(factoredDataAtomic);
		assertEquals(factoredDataAtomic.getRepeatId(), repeatId);
	}

	@Test
	public void testFactorAttributeUsingNameInDataAndValue() {
		DataAttribute factoredDataAttribute = dataFactory
				.factorAttributeUsingNameInDataAndValue(nameInData, value);

		assertTrue(factoredDataAttribute instanceof CoraDataAttribute);
		assertEquals(factoredDataAttribute.getNameInData(), nameInData);
		assertEquals(factoredDataAttribute.getValue(), value);
	}

	@Test
	public void testFactorDataChildFilterUsingNameInData() {
		CoraDataChildFilter childFilter = (CoraDataChildFilter) dataFactory
				.factorDataChildFilterUsingNameInData(nameInData);

		assertEquals(childFilter.onlyForTestGetChildNameInData(), nameInData);

	}

}
