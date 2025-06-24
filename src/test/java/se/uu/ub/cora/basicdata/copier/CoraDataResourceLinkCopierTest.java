/*
 * Copyright 2019, 2023, 2025 Uppsala University Library
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

import se.uu.ub.cora.basicdata.data.CoraDataResourceLink;

public class CoraDataResourceLinkCopierTest {

	private CoraDataResourceLinkCopier dataResourceLinkCopier;
	private CoraDataResourceLink originalResourceLink;

	@BeforeMethod
	public void setUp() {
		originalResourceLink = CoraDataResourceLink.withNameInDataAndTypeAndIdAndMimeType("master",
				"someType", "somId", "someMimeType");
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
	public void testCopyDataResourceLinkSameNameInDataAndMimeType() {
		CoraDataResourceLink resourceLinkCopy = (CoraDataResourceLink) dataResourceLinkCopier
				.copy();
		assertEquals(resourceLinkCopy.getNameInData(), originalResourceLink.getNameInData());
		assertEquals(resourceLinkCopy.getType(), originalResourceLink.getType());
		assertEquals(resourceLinkCopy.getId(), originalResourceLink.getId());
		assertEquals(resourceLinkCopy.getMimeType(), originalResourceLink.getMimeType());
		assertEquals(resourceLinkCopy.getMimeType(), originalResourceLink.getMimeType());
	}

	@Test
	public void testCopyDataAssertNoRepeatId() {
		CoraDataResourceLink dataGroupCopy = (CoraDataResourceLink) dataResourceLinkCopier.copy();
		assertNull(dataGroupCopy.getRepeatId());
	}

	@Test
	public void testCopyDataGroupWithRepeatId() {
		originalResourceLink.setRepeatId("1");

		CoraDataResourceLink copiedResourceLink = (CoraDataResourceLink) dataResourceLinkCopier
				.copy();

		assertEquals(copiedResourceLink.getRepeatId(), originalResourceLink.getRepeatId());
	}
}
