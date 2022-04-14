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

import se.uu.ub.cora.basicdata.data.CoraDataAtomic;
import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataAtomicFactory;

public class CoraDataAtomicFactoryTest {
	private String nameInData = "someNameInDatae";
	private String value = "someValue";
	private DataAtomicFactory atomicFactory;

	@BeforeMethod
	public void setUp() {
		atomicFactory = new CoraDataAtomicFactory();
	}

	@Test
	public void testFactorUsingNameInDataAndValue() {
		DataAtomic factoredDataAtomic = atomicFactory.factorUsingNameInDataAndValue(nameInData,
				value);
		assertCorrectBasicDataAtomic(factoredDataAtomic);
	}

	private void assertCorrectBasicDataAtomic(DataAtomic factoredDataAtomic) {
		assertTrue(factoredDataAtomic instanceof CoraDataAtomic);
		assertEquals(factoredDataAtomic.getNameInData(), nameInData);
		assertEquals(factoredDataAtomic.getValue(), value);
	}

	@Test
	public void testFactorUsingNameInDataAndValueAndRepeatId() {
		String repeatId = "r1";
		DataAtomic factoredDataAtomic = atomicFactory
				.factorUsingNameInDataAndValueAndRepeatId(nameInData, value, repeatId);
		assertCorrectBasicDataAtomic(factoredDataAtomic);
		assertEquals(factoredDataAtomic.getRepeatId(), repeatId);
	}

}
