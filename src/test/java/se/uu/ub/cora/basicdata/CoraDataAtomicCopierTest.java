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
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.assertNull;

import org.testng.annotations.Test;

import se.uu.ub.cora.basicdata.data.CoraDataAtomic;

public class CoraDataAtomicCopierTest {

	@Test
	public void testCopyDataAtomic() {
		CoraDataAtomic dataAtomic = CoraDataAtomic.withNameInDataAndValue("someNameInData",
				"someAtomicValue");
		CoraDataAtomicCopier dataAtomicCopier = CoraDataAtomicCopier.usingDataAtomic(dataAtomic);

		CoraDataAtomic dataAtomicCopy = (CoraDataAtomic) dataAtomicCopier.copy();
		assertNotSame(dataAtomic, dataAtomicCopy);
		assertEquals(dataAtomic.getNameInData(), dataAtomicCopy.getNameInData());
		assertEquals(dataAtomic.getValue(), dataAtomicCopy.getValue());
		assertNull(dataAtomic.getRepeatId());
	}

	@Test
	public void testCopyDataAtomicWithRepeatId() {
		CoraDataAtomic dataAtomic = CoraDataAtomic.withNameInDataAndValueAndRepeatId("someNameInData",
				"someAtomicValue", "22");
		CoraDataAtomicCopier dataAtomicCopier = CoraDataAtomicCopier.usingDataAtomic(dataAtomic);

		CoraDataAtomic dataAtomicCopy = (CoraDataAtomic) dataAtomicCopier.copy();
		assertNotSame(dataAtomic, dataAtomicCopy);
		assertEquals(dataAtomic.getNameInData(), dataAtomicCopy.getNameInData());
		assertEquals(dataAtomic.getValue(), dataAtomicCopy.getValue());
		assertEquals(dataAtomic.getRepeatId(), dataAtomicCopy.getRepeatId());
	}

}
