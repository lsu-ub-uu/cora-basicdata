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
import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataAttribute;
import se.uu.ub.cora.data.DataAttributeFactory;

public class CoraDataAttributeFactoryTest {
	private String nameInData = "someNameInDatae";
	private String value = "someValue";
	private DataAttributeFactory attributeFactory;

	@BeforeMethod
	public void setUp() {
		attributeFactory = new CoraDataAttributeFactory();
	}

	@Test
	public void testFactorUsingNameInDataAndValue() {
		DataAttribute factoredDataAttribute = attributeFactory
				.factorUsingNameInDataAndValue(nameInData, value);

		assertTrue(factoredDataAttribute instanceof CoraDataAttribute);
		assertEquals(factoredDataAttribute.getNameInData(), nameInData);
		assertEquals(factoredDataAttribute.getValue(), value);
	}

}
