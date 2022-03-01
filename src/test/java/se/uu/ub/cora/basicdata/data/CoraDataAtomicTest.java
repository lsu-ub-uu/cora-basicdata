/*
 * Copyright 2015, 2022 Uppsala University Library
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

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataAttribute;
import se.uu.ub.cora.data.DataMissingException;

public class CoraDataAtomicTest {

	private CoraDataAtomic dataAtomic;

	@BeforeMethod
	public void setUp() {
		dataAtomic = CoraDataAtomic.withNameInDataAndValue("nameInData", "value");

	}

	@Test
	public void testInit() {
		assertEquals(dataAtomic.getNameInData(), "nameInData");
		assertEquals(dataAtomic.getValue(), "value");
	}

	@Test
	public void testInitWithRepeatId() {
		dataAtomic = CoraDataAtomic.withNameInDataAndValueAndRepeatId("nameInData", "value", "2");
		assertEquals(dataAtomic.getNameInData(), "nameInData");
		assertEquals(dataAtomic.getValue(), "value");
		assertEquals(dataAtomic.getRepeatId(), "2");
	}

	@Test
	public void testSetRepeatId() {
		dataAtomic.setRepeatId("3");
		assertEquals(dataAtomic.getNameInData(), "nameInData");
		assertEquals(dataAtomic.getValue(), "value");
		assertEquals(dataAtomic.getRepeatId(), "3");
	}

	@Test
	public void testAddAttribute() {
		dataAtomic.addAttributeByIdWithValue("someAttributeName", "value");
		Collection<DataAttribute> attributes = dataAtomic.getAttributes();
		DataAttribute next = attributes.iterator().next();
		assertEquals(next.getNameInData(), "someAttributeName");
		assertEquals(next.getValue(), "value");
	}

	@Test
	public void testAddAttributeWithSameNameInDataOverwrites() {
		dataAtomic.addAttributeByIdWithValue("someAttributeName", "value");
		dataAtomic.addAttributeByIdWithValue("someAttributeName", "someOtherValue");

		Collection<DataAttribute> attributes = dataAtomic.getAttributes();
		assertEquals(attributes.size(), 1);
		DataAttribute next = attributes.iterator().next();
		assertEquals(next.getValue(), "someOtherValue");
	}

	@Test
	public void testHasAttributes() {
		assertFalse(dataAtomic.hasAttributes());
		dataAtomic.addAttributeByIdWithValue("attributeId", "attributeValue");
		assertTrue(dataAtomic.hasAttributes());
	}

	@Test
	public void testGetAttribute() {
		dataAtomic.addAttributeByIdWithValue("someOtherAttributeId", "attributeValue");
		dataAtomic.addAttributeByIdWithValue("attributeId", "attributeValue");
		assertEquals(dataAtomic.getAttribute("attributeId").getValue(), "attributeValue");
	}

	@Test(expectedExceptions = DataMissingException.class, expectedExceptionsMessageRegExp = ""
			+ "Attribute with id someAttributeId not found.")
	public void testGetAttributeDoesNotExist() {
		dataAtomic.getAttribute("someAttributeId");
	}

}
