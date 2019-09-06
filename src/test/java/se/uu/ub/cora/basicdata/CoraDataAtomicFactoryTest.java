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

import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataAtomic;

public class CoraDataAtomicFactoryTest {

	@Test
	public void testFactorWithNameInData() {
		DataAtomicFactory factory = new CoraDataAtomicFactory();
		String nameInData = "someNameInData";
		String value = "someValue";
		DataAtomic dataAtomic = factory.factorUsingNameInDataAndValue(nameInData, value);
		assertTrue(dataAtomic instanceof CoraDataAtomic);

		assertEquals(dataAtomic.getNameInData(), nameInData);
		assertEquals(dataAtomic.getValue(), value);
	}

}
