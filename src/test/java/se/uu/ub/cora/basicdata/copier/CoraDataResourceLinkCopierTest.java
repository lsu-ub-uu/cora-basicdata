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
package se.uu.ub.cora.basicdata.copier;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.assertNull;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.basicdata.data.CoraDataAtomic;
import se.uu.ub.cora.basicdata.data.CoraDataGroup;
import se.uu.ub.cora.basicdata.data.CoraDataResourceLink;
import se.uu.ub.cora.data.DataGroup;

public class CoraDataResourceLinkCopierTest {

	private CoraDataResourceLinkCopier dataResourceLinkCopier;
	private CoraDataResourceLink originalResourceLink;

	@BeforeMethod
	public void setUp() {
		DataGroup dataGroup = CoraDataGroup.withNameInData("master");
		dataGroup.addChild(CoraDataAtomic.withNameInDataAndValue("streamId", "binary:456"));
		dataGroup.addChild(CoraDataAtomic.withNameInDataAndValue("filename", "someImage.png"));
		dataGroup.addChild(CoraDataAtomic.withNameInDataAndValue("filesize", "44196"));
		dataGroup.addChild(
				CoraDataAtomic.withNameInDataAndValue("mimeType", "application/octet-stream"));
		originalResourceLink = CoraDataResourceLink.fromDataGroup(dataGroup);
		dataResourceLinkCopier = new CoraDataResourceLinkCopier(originalResourceLink);

	}

	@Test
	public void testCopyDataResourceLinkNotSameObject() {
		CoraDataResourceLink resourceLinkCopy = (CoraDataResourceLink) dataResourceLinkCopier
				.copy();
		assertNotNull(resourceLinkCopy);
		assertNotSame(originalResourceLink, resourceLinkCopy);
	}

	@Test
	public void testCopyDataResourceLinkSameNameInData() {
		CoraDataResourceLink resourceLinkCopy = (CoraDataResourceLink) dataResourceLinkCopier
				.copy();
		assertEquals(resourceLinkCopy.getNameInData(), originalResourceLink.getNameInData());
	}

	@Test
	public void testCopyCompleteResourceLink() {
		CoraDataResourceLink resourceLinkCopy = (CoraDataResourceLink) dataResourceLinkCopier
				.copy();
		CoraDataAtomic streamId = (CoraDataAtomic) resourceLinkCopy
				.getFirstChildWithNameInData("streamId");
		assertEquals(streamId.getValue(),
				originalResourceLink.getFirstAtomicValueWithNameInData("streamId"));

		CoraDataAtomic filename = (CoraDataAtomic) resourceLinkCopy
				.getFirstChildWithNameInData("filename");
		assertEquals(filename.getValue(),
				originalResourceLink.getFirstAtomicValueWithNameInData("filename"));

		CoraDataAtomic filesize = (CoraDataAtomic) resourceLinkCopy
				.getFirstChildWithNameInData("filesize");
		assertEquals(filesize.getValue(),
				originalResourceLink.getFirstAtomicValueWithNameInData("filesize"));

		CoraDataAtomic mimeType = (CoraDataAtomic) resourceLinkCopy
				.getFirstChildWithNameInData("mimeType");
		assertEquals(mimeType.getValue(),
				originalResourceLink.getFirstAtomicValueWithNameInData("mimeType"));

	}

	@Test
	public void testCopyDataAssertNoRepeatId() {
		CoraDataResourceLink dataGroupCopy = (CoraDataResourceLink) dataResourceLinkCopier.copy();
		assertNull(dataGroupCopy.getRepeatId());
	}

	@Test
	public void testCopyDataGroupWithRepeatId() {
		originalResourceLink.setRepeatId("1");
		CoraDataResourceLink dataGroupCopy = (CoraDataResourceLink) dataResourceLinkCopier.copy();
		assertEquals(dataGroupCopy.getRepeatId(), originalResourceLink.getRepeatId());
	}

	@Test
	public void testCopyDataAssertNoAttributes() {
		CoraDataResourceLink dataGroupCopy = (CoraDataResourceLink) dataResourceLinkCopier.copy();
		assertEquals(dataGroupCopy.getAttributes().size(), 0);
	}

	@Test
	public void testCopyDataGroupWithOneAttribute() {
		originalResourceLink.addAttributeByIdWithValue("type", "someTypeAttribute");
		CoraDataResourceLink dataGroupCopy = (CoraDataResourceLink) dataResourceLinkCopier.copy();
		assertEquals(dataGroupCopy.getAttribute("type").getValue(),
				originalResourceLink.getAttribute("type").getValue());
		assertEquals(dataGroupCopy.getAttributes().size(), 1);
	}

	@Test
	public void testCopyDataGroupWithTwoAttributes() {
		originalResourceLink.addAttributeByIdWithValue("type", "someTypeAttribute");
		originalResourceLink.addAttributeByIdWithValue("otherAttribute", "someOtherAttribute");
		CoraDataResourceLink dataGroupCopy = (CoraDataResourceLink) dataResourceLinkCopier.copy();
		assertEquals(dataGroupCopy.getAttribute("type").getValue(),
				originalResourceLink.getAttribute("type").getValue());

		assertEquals(dataGroupCopy.getAttribute("otherAttribute").getValue(),
				originalResourceLink.getAttribute("otherAttribute").getValue());
		assertEquals(dataGroupCopy.getAttributes().size(), 2);
	}
}
