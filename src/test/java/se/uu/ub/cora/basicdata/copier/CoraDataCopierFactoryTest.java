/*
 * Copyright 2019, 2023 Uppsala University Library
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

import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import se.uu.ub.cora.basicdata.data.CoraDataAtomic;
import se.uu.ub.cora.basicdata.data.CoraDataGroup;
import se.uu.ub.cora.basicdata.data.CoraDataRecordLink;
import se.uu.ub.cora.basicdata.data.CoraDataResourceLink;
import se.uu.ub.cora.data.DataChild;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.copier.DataCopier;
import se.uu.ub.cora.data.copier.DataCopierFactory;

public class CoraDataCopierFactoryTest {

	@Test
	public void testFactorDataAtomicCopier() {
		DataChild dataAtomic = CoraDataAtomic.withNameInDataAndValue("aName", "aValue");
		DataCopierFactory dataCopierFactoryImp = new CoraDataCopierFactoryImp();
		DataCopier dataCopier = dataCopierFactoryImp.factorForDataElement(dataAtomic);
		assertTrue(dataCopier instanceof CoraDataAtomicCopier);

	}

	@Test
	public void testFactorDataGroupCopier() {
		DataGroup dataGroup = CoraDataGroup.withNameInData("someDataGroup");
		dataGroup.addChild(CoraDataAtomic.withNameInDataAndValue("aName", "aValue"));
		DataCopierFactory dataCopierFactoryImp = new CoraDataCopierFactoryImp();
		DataCopier dataCopier = dataCopierFactoryImp.factorForDataElement(dataGroup);
		assertTrue(dataCopier instanceof CoraDataGroupCopier);

		CoraDataGroupCopier dataGroupCopier = (CoraDataGroupCopier) dataCopier;
		assertTrue(dataGroupCopier.getCopierFactory() instanceof CoraDataCopierFactoryImp);
	}

	@Test
	public void testFactorDataRecordLinkCopier() {
		CoraDataRecordLink dataRecordLink = CoraDataRecordLink
				.usingNameInDataAndTypeAndId("someLinkNameInData", "someLinkType", "someLinkValue");

		DataCopierFactory dataCopierFactoryImp = new CoraDataCopierFactoryImp();
		DataCopier dataCopier = dataCopierFactoryImp.factorForDataElement(dataRecordLink);
		assertTrue(dataCopier instanceof CoraDataRecordLinkCopier);
	}

	@Test
	public void testFactorDataResourceLinkCopier() {
		CoraDataResourceLink resourceLink = CoraDataResourceLink
				.withNameInDataAndTypeAndIdAndMimeType("thumbnail", null, null, "image/jpeg");

		DataCopierFactory dataCopierFactoryImp = new CoraDataCopierFactoryImp();
		DataCopier dataCopier = dataCopierFactoryImp.factorForDataElement(resourceLink);
		assertTrue(dataCopier instanceof CoraDataResourceLinkCopier);
	}
}
