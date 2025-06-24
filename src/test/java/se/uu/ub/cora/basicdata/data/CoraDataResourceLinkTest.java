/**
 * Copyright 2015, 2016, 2023, 2025 Uppsala University Library
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

import java.util.Collection;
import java.util.Optional;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.Action;
import se.uu.ub.cora.data.DataAttribute;
import se.uu.ub.cora.data.DataLink;
import se.uu.ub.cora.data.DataMissingException;
import se.uu.ub.cora.data.DataResourceLink;

public class CoraDataResourceLinkTest {

	private static final String SOME_NAME_IN_DATA = "someNameInData";
	private static final String SOME_MIME_TYPE = "someMimeType";
	DataResourceLink resourceLink;

	@BeforeMethod
	public void setUp() {
		resourceLink = CoraDataResourceLink.withNameInDataAndTypeAndIdAndMimeType(SOME_NAME_IN_DATA,
				"someType", "someId", SOME_MIME_TYPE);

	}

	@Test
	public void testConstructWithNameInData() {
		assertTrue(resourceLink instanceof DataLink);
		assertTrue(resourceLink instanceof DataResourceLink);
		assertEquals(resourceLink.getNameInData(), SOME_NAME_IN_DATA);
		assertEquals(resourceLink.getMimeType(), SOME_MIME_TYPE);
	}

	@Test
	public void testGetType() {
		assertEquals(resourceLink.getType(), "someType");
	}

	@Test
	public void testGetId() {
		assertEquals(resourceLink.getId(), "someId");
	}

	@Test
	public void testInitWithRepeatId() {
		resourceLink.setRepeatId("hugh");
		assertEquals(resourceLink.getRepeatId(), "hugh");
	}

	@Test
	public void testHasRepeatIdNotSet() {
		assertFalse(resourceLink.hasRepeatId());
	}

	@Test
	public void testHasRepeatIdSetToEmpty() {
		resourceLink.setRepeatId("");
		assertFalse(resourceLink.hasRepeatId());
	}

	@Test
	public void testHasRepeatIdSet() {
		resourceLink.setRepeatId("3");
		assertTrue(resourceLink.hasRepeatId());
	}

	@Test
	public void testHasReadActionsNoReadAction() {
		assertFalse(resourceLink.hasReadAction());

	}

	@Test
	public void testHasReadActionsReadAction() {
		resourceLink.addAction(Action.READ);

		assertTrue(resourceLink.hasReadAction());
	}

	@Test
	public void testMimeType() {
		resourceLink.setMimeType("someMimeType");
		assertEquals(resourceLink.getMimeType(), "someMimeType");
	}

	@Test
	public void testAddAttribute() {
		resourceLink.addAttributeByIdWithValue("someAttributeName", "value");
		Collection<DataAttribute> attributes = resourceLink.getAttributes();
		DataAttribute next = attributes.iterator().next();
		assertEquals(next.getNameInData(), "someAttributeName");
		assertEquals(next.getValue(), "value");
	}

	@Test
	public void testAddAttributeWithSameNameInDataOverwrites() {
		resourceLink.addAttributeByIdWithValue("someAttributeName", "value");
		resourceLink.addAttributeByIdWithValue("someAttributeName", "someOtherValue");

		Collection<DataAttribute> attributes = resourceLink.getAttributes();
		assertEquals(attributes.size(), 1);
		DataAttribute next = attributes.iterator().next();
		assertEquals(next.getValue(), "someOtherValue");
	}

	@Test
	public void testHasAttributes() {
		assertFalse(resourceLink.hasAttributes());
		resourceLink.addAttributeByIdWithValue("attributeId", "attributeValue");
		assertTrue(resourceLink.hasAttributes());
	}

	@Test
	public void testGetAttribute() {
		resourceLink.addAttributeByIdWithValue("attributeId", "attributeValue");
		assertEquals(resourceLink.getAttribute("attributeId").getValue(), "attributeValue");
	}

	@Test(expectedExceptions = DataMissingException.class, expectedExceptionsMessageRegExp = ""
			+ "Attribute with id someAttributeId not found.")
	public void testGetAttributeDoesNotExist() {
		resourceLink.getAttribute("someAttributeId");
	}

	@Test
	public void testGetAttributeValueNoAttribute() {
		Optional<String> attributeValue = resourceLink.getAttributeValue("attributeNameInData");

		assertTrue(attributeValue.isEmpty());
	}

	@Test
	public void testGetAttributeValueAttributeExists() {
		resourceLink.addAttributeByIdWithValue("someAttributeName2", "someValue");
		resourceLink.addAttributeByIdWithValue("someAttributeName3", "someValue");
		resourceLink.addAttributeByIdWithValue("someAttributeName", "someValue");

		Optional<String> attributeValue = resourceLink.getAttributeValue("someAttributeName");

		assertTrue(attributeValue.isPresent());
		assertEquals(attributeValue.get(), "someValue");
	}

}
