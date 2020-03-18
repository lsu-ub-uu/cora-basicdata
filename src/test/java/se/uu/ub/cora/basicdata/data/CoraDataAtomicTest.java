/*
 * Copyright 2015 Uppsala University Library
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

import org.testng.annotations.Test;

public class CoraDataAtomicTest {

	@Test
	public void testInit() {
		CoraDataAtomic dataAtomic = CoraDataAtomic.withNameInDataAndValue("nameInData", "value");
		assertEquals(dataAtomic.getNameInData(), "nameInData");
		assertEquals(dataAtomic.getValue(), "value");
	}

	@Test
	public void testInitWithRepeatId() {
		CoraDataAtomic dataAtomic = CoraDataAtomic.withNameInDataAndValueAndRepeatId("nameInData",
				"value", "2");
		assertEquals(dataAtomic.getNameInData(), "nameInData");
		assertEquals(dataAtomic.getValue(), "value");
		assertEquals(dataAtomic.getRepeatId(), "2");
	}

	@Test
	public void testSetRepeatId() {
		CoraDataAtomic dataAtomic = CoraDataAtomic.withNameInDataAndValue("nameInData", "value");
		dataAtomic.setRepeatId("3");
		assertEquals(dataAtomic.getNameInData(), "nameInData");
		assertEquals(dataAtomic.getValue(), "value");
		assertEquals(dataAtomic.getRepeatId(), "3");
	}

	// @Test
	// public void testGetAttributesInDataElementInterface() {
	// CoraDataAtomic dataAtomic = CoraDataAtomic.withNameInDataAndValue("nameInData",
	// "value");
	// assertTrue(dataAtomic.getAttributes().isEmpty());
	//
	// }
}
