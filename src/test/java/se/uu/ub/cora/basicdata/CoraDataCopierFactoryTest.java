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

import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataElement;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.copier.DataCopier;
import se.uu.ub.cora.data.copier.DataCopierFactory;

public class CoraDataCopierFactoryTest {

	@Test
	public void testFactorDataAtomicCopier() {
		DataElement dataAtomic = CoraDataAtomic.withNameInDataAndValue("aName", "aValue");
		DataCopierFactory dataCopierFactoryImp = new DataCopierFactoryImp();
		DataCopier dataCopier = dataCopierFactoryImp.factorForDataElement(dataAtomic);
		assertTrue(dataCopier instanceof CoraDataAtomicCopier);

	}

	@Test
	public void testFactorDataGroupCopier() {
		DataGroup dataGroup = CoraDataGroup.withNameInData("someDataGroup");
		dataGroup.addChild(CoraDataAtomic.withNameInDataAndValue("aName", "aValue"));
		DataCopierFactory dataCopierFactoryImp = new DataCopierFactoryImp();
		DataCopier dataCopier = dataCopierFactoryImp.factorForDataElement(dataGroup);
		assertTrue(dataCopier instanceof CoraDataGroupCopier);

		CoraDataGroupCopier dataGroupCopier = (CoraDataGroupCopier) dataCopier;
		assertTrue(dataGroupCopier.getCopierFactory() instanceof DataCopierFactoryImp);

	}

	@Test
	public void testFactorDataRecordLinkCopier() {
		DataGroup dataGroup = CoraDataGroup.asLinkWithNameInDataAndTypeAndId(
				"someLinkNameInData", "someLinkType", "someLinkValue");
		CoraDataRecordLink dataRecordLink = CoraDataRecordLink.fromDataGroup(dataGroup);

		DataCopierFactory dataCopierFactoryImp = new DataCopierFactoryImp();
		DataCopier dataCopier = dataCopierFactoryImp.factorForDataElement(dataRecordLink);
		assertTrue(dataCopier instanceof DataRecordLinkCopier);
	}
}
