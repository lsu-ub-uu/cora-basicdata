/*
 * Copyright 2015, 2016, 2019, 2025 Uppsala University Library
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
package se.uu.ub.cora.basicdata.data;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.Collection;
import java.util.Optional;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.Action;
import se.uu.ub.cora.data.DataAttribute;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataLink;
import se.uu.ub.cora.data.DataMissingException;
import se.uu.ub.cora.data.DataRecordLink;
import se.uu.ub.cora.data.spies.DataGroupSpy;

public class CoraDataRecordLinkTest {

	CoraDataRecordLink recordLink;

	@BeforeMethod
	public void setUp() {
		recordLink = CoraDataRecordLink.withNameInData("nameInData");

		CoraDataAtomic linkedRecordType = CoraDataAtomic.withNameInDataAndValue("linkedRecordType",
				"myLinkedRecordType");
		recordLink.addChild(linkedRecordType);

		CoraDataAtomic linkedRecordId = CoraDataAtomic.withNameInDataAndValue("linkedRecordId",
				"myLinkedRecordId");
		recordLink.addChild(linkedRecordId);

	}

	@Test
	public void testCorrectType() {
		assertTrue(recordLink instanceof DataLink);
		assertTrue(recordLink instanceof DataRecordLink);
	}

	@Test
	public void testInit() {
		assertEquals(recordLink.getNameInData(), "nameInData");
		assertNotNull(recordLink.getAttributes());
		assertNotNull(recordLink.getChildren());
		assertEquals(recordLink.getFirstAtomicValueWithNameInData("linkedRecordType"),
				"myLinkedRecordType");
		assertEquals(recordLink.getFirstAtomicValueWithNameInData("linkedRecordId"),
				"myLinkedRecordId");
	}

	@Test
	public void testInitWithRepeatId() {
		recordLink.setRepeatId("hugh");
		assertEquals(recordLink.getRepeatId(), "hugh");
	}

	@Test
	public void testAddAttribute() {
		recordLink = CoraDataRecordLink.withNameInData("nameInData");
		recordLink.addAttributeByIdWithValue("someId", "someValue");

		Collection<DataAttribute> attributes = recordLink.getAttributes();
		DataAttribute attribute = attributes.iterator().next();
		assertEquals(attribute.getNameInData(), "someId");
		assertEquals(attribute.getValue(), "someValue");
	}

	@Test
	public void testInitWithLinkedPath() {
		DataGroup dataGroup = CoraDataGroup.withNameInData("linkedPath");
		recordLink.addChild(dataGroup);
		assertNotNull(recordLink.getFirstChildWithNameInData("linkedPath"));
	}

	@Test
	public void testFromDataGroup() {
		DataGroup dataGroupRecordLink = createRecordLinkAsDataGroup();

		CoraDataRecordLink dataRecordLink = CoraDataRecordLink.fromDataGroup(dataGroupRecordLink);

		assertCorrectFromDataRecordLink(dataRecordLink);
		assertNull(dataRecordLink.getRepeatId());
	}

	private DataGroup createRecordLinkAsDataGroup() {
		DataGroup dataGroupRecordLink = CoraDataGroup.withNameInData("nameInData");

		CoraDataAtomic linkedRecordType = CoraDataAtomic.withNameInDataAndValue("linkedRecordType",
				"someLinkedRecordType");
		dataGroupRecordLink.addChild(linkedRecordType);

		CoraDataAtomic linkedRecordId = CoraDataAtomic.withNameInDataAndValue("linkedRecordId",
				"someLinkedRecordId");
		dataGroupRecordLink.addChild(linkedRecordId);
		return dataGroupRecordLink;
	}

	private void assertCorrectFromDataRecordLink(CoraDataRecordLink recordLink) {
		assertEquals(recordLink.getNameInData(), "nameInData");

		CoraDataAtomic convertedRecordType = (CoraDataAtomic) recordLink
				.getFirstChildWithNameInData("linkedRecordType");
		assertEquals(convertedRecordType.getValue(), "someLinkedRecordType");

		CoraDataAtomic convertedRecordId = (CoraDataAtomic) recordLink
				.getFirstChildWithNameInData("linkedRecordId");
		assertEquals(convertedRecordId.getValue(), "someLinkedRecordId");
	}

	@Test
	public void testFromDataGroupWithRepeatId() {
		DataGroup dataGroupRecordLink = createRecordLinkAsDataGroup();
		dataGroupRecordLink.setRepeatId("1");

		CoraDataRecordLink dataRecordLink = CoraDataRecordLink.fromDataGroup(dataGroupRecordLink);

		assertCorrectFromDataRecordLink(dataRecordLink);
		assertEquals(dataRecordLink.getRepeatId(), "1");
	}

	@Test
	public void testHasReadActionsNoReadAction() {
		assertFalse(recordLink.hasReadAction());

	}

	@Test
	public void testHasReadActionsReadAction() {
		recordLink.addAction(Action.READ);

		assertTrue(recordLink.hasReadAction());

	}

	@Test
	public void testGetLinkedRecordType() {
		assertEquals(recordLink.getLinkedRecordType(), "myLinkedRecordType");
	}

	@Test(expectedExceptions = DataMissingException.class)
	public void testGetLinkedRecordTypeMissing() {
		CoraDataRecordLink withNameInData = CoraDataRecordLink.withNameInData("nameInData");
		withNameInData.getLinkedRecordType();
	}

	@Test
	public void testGetLinkedRecordId() {
		assertEquals(recordLink.getLinkedRecordId(), "myLinkedRecordId");
	}

	@Test(expectedExceptions = DataMissingException.class)
	public void testGetLinkedRecordIdMissing() {
		CoraDataRecordLink withNameInData = CoraDataRecordLink.withNameInData("nameInData");
		withNameInData.getLinkedRecordId();
	}

	@Test
	public void testGetLinkedRecordEmpty() {
		CoraDataRecordLink link = CoraDataRecordLink.withNameInData("nameInData");
		Optional<DataGroup> linkedRecord = link.getLinkedRecord();
		assertTrue(linkedRecord.isEmpty());
	}

	@Test
	public void testSetAndGetLinkedRecord() {
		CoraDataRecordLink link = CoraDataRecordLink.withNameInData("nameInData");
		DataGroup group = new DataGroupSpy();
		link.setLinkedRecord(group);
		Optional<DataGroup> linkedRecord = link.getLinkedRecord();
		assertTrue(linkedRecord.isPresent());
		assertEquals(linkedRecord.get(), group);
	}

	@Test
	public void testCreateLinkUsingNameInDataAndTypeAndId() {
		CoraDataRecordLink link = CoraDataRecordLink.usingNameInDataAndTypeAndId("someNameInData",
				"someType", "someId");

		assertEquals(link.getNameInData(), "someNameInData");
		assertEquals(link.getLinkedRecordType(), "someType");
		assertEquals(link.getLinkedRecordId(), "someId");
		assertEquals(link.getLinkedRecord(), Optional.empty());

	}

}
