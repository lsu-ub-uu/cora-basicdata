/*
 * Copyright 2019 Uppsala University Library
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
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.assertNull;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class CoraDataRecordLinkCopierTest {

	private DataRecordLinkCopier dataRecordLinkCopier;
	private CoraDataRecordLink originalRecordLink;

	@BeforeMethod
	public void setUp() {
		CoraDataGroup dataGroup = CoraDataGroup.asLinkWithNameInDataAndTypeAndId("someLinkNameInData",
				"someLinkType", "someLinkValue");
		originalRecordLink = CoraDataRecordLink.fromDataGroup(dataGroup);
		dataRecordLinkCopier = new DataRecordLinkCopier(originalRecordLink);

	}

	@Test
	public void testCopyDataRecordLinkNotSameObject() {
		CoraDataRecordLink recordLinkCopy = dataRecordLinkCopier.copy();
		assertNotNull(recordLinkCopy);
		assertNotSame(originalRecordLink, recordLinkCopy);
	}

	@Test
	public void testCopyDataRecordLinkSameNameInData() {
		CoraDataGroup recordLinkCopy = dataRecordLinkCopier.copy();
		assertEquals(recordLinkCopy.getNameInData(), originalRecordLink.getNameInData());
	}

	@Test
	public void testCopyRecordLinkTypeAndId() {
		CoraDataGroup recordLinkCopy = dataRecordLinkCopier.copy();
		CoraDataAtomic linkedRecordType = (CoraDataAtomic) recordLinkCopy
				.getFirstChildWithNameInData("linkedRecordType");
		assertEquals(linkedRecordType.getValue(),
				originalRecordLink.getFirstAtomicValueWithNameInData("linkedRecordType"));

		CoraDataAtomic linkedRecordId = (CoraDataAtomic) recordLinkCopy
				.getFirstChildWithNameInData("linkedRecordId");
		assertEquals(linkedRecordId.getValue(),
				originalRecordLink.getFirstAtomicValueWithNameInData("linkedRecordId"));

	}

	@Test
	public void testCopyDataAssertNoRepeatId() {
		CoraDataGroup dataGroupCopy = dataRecordLinkCopier.copy();
		assertNull(dataGroupCopy.getRepeatId());
	}

	@Test
	public void testCopyDataGroupWithRepeatId() {
		originalRecordLink.setRepeatId("1");
		CoraDataGroup dataGroupCopy = dataRecordLinkCopier.copy();
		assertEquals(dataGroupCopy.getRepeatId(), originalRecordLink.getRepeatId());
	}

	@Test
	public void testCopyDataGroupWithOneAttribute() {
		originalRecordLink.addAttributeByIdWithValue("type", "someTypeAttribute");
		CoraDataGroup dataGroupCopy = dataRecordLinkCopier.copy();
		assertEquals(dataGroupCopy.getAttribute("type"), "someTypeAttribute");
		assertEquals(dataGroupCopy.getAttributes().size(), 1);
	}

	@Test
	public void testCopyDataGroupWithTwoAttributes() {
		originalRecordLink.addAttributeByIdWithValue("type", "someTypeAttribute");
		originalRecordLink.addAttributeByIdWithValue("otherAttribute", "someOtherAttribute");
		CoraDataGroup dataGroupCopy = dataRecordLinkCopier.copy();
		assertEquals(dataGroupCopy.getAttribute("type"), "someTypeAttribute");
		assertEquals(dataGroupCopy.getAttribute("otherAttribute"), "someOtherAttribute");
		assertEquals(dataGroupCopy.getAttributes().size(), 2);
	}
}
