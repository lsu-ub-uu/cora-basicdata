/**
 * Copyright 2015, 2016, 2023 Uppsala University Library
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
import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.Action;
import se.uu.ub.cora.data.DataLink;
import se.uu.ub.cora.data.DataResourceLink;

public class CoraDataResourceLinkTest {

	private static final String NOT_YET_IMPLEMENTED = "Not yet implemented.";
	private static final String SOME_NAME_IN_DATA = "someNameInData";
	private static final String SOME_MIME_TYPE = "someMimeType";
	CoraDataResourceLink resourceLink;

	@BeforeMethod
	public void setUp() {
		resourceLink = CoraDataResourceLink.withNameInDataAndMimeType(SOME_NAME_IN_DATA,
				SOME_MIME_TYPE);

	}

	@Test
	public void testConstructWithNameInData() {
		assertTrue(resourceLink instanceof DataLink);
		assertTrue(resourceLink instanceof DataResourceLink);
		assertEquals(resourceLink.getNameInData(), SOME_NAME_IN_DATA);
		assertEquals(resourceLink.getMimeType(), SOME_MIME_TYPE);
	}

	@Test
	public void testInitWithRepeatId() {
		resourceLink.setRepeatId("hugh");
		assertEquals(resourceLink.getRepeatId(), "hugh");
	}

	@Test
	public void testHasRepeatIdNotSet() throws Exception {
		assertFalse(resourceLink.hasRepeatId());
	}

	@Test
	public void testHasRepeatIdSetToEmpty() throws Exception {
		resourceLink.setRepeatId("");
		assertFalse(resourceLink.hasRepeatId());
	}

	@Test
	public void testHasRepeatIdSet() throws Exception {
		resourceLink.setRepeatId("3");
		assertTrue(resourceLink.hasRepeatId());
	}

	@Test
	public void testHasReadActionsNoReadAction() throws Exception {
		assertFalse(resourceLink.hasReadAction());

	}

	@Test
	public void testHasReadActionsReadAction() throws Exception {
		resourceLink.addAction(Action.READ);

		assertTrue(resourceLink.hasReadAction());
	}

	@Test
	public void testMimeType() throws Exception {
		resourceLink.setMimeType("someMimeType");
		assertEquals(resourceLink.getMimeType(), "someMimeType");
	}

	@Test
	public void testHasAttributes() throws Exception {
		assertFalse(resourceLink.hasAttributes());
	}

	@Test(expectedExceptions = UnsupportedOperationException.class, expectedExceptionsMessageRegExp = NOT_YET_IMPLEMENTED)
	public void testGetAttribute() {
		resourceLink.getAttribute("someAttribute");
	}

	@Test(expectedExceptions = UnsupportedOperationException.class, expectedExceptionsMessageRegExp = NOT_YET_IMPLEMENTED)
	public void testAddAttributeByIdWithValue() {
		resourceLink.addAttributeByIdWithValue("someNameInData", "someValue");
	}

	@Test(expectedExceptions = UnsupportedOperationException.class, expectedExceptionsMessageRegExp = NOT_YET_IMPLEMENTED)
	public void testAttributes() throws Exception {
		resourceLink.getAttributes();
	}

	@Test(expectedExceptions = UnsupportedOperationException.class, expectedExceptionsMessageRegExp = NOT_YET_IMPLEMENTED)
	public void testAttributeValue() throws Exception {
		resourceLink.getAttributeValue("someValue");
	}
}
