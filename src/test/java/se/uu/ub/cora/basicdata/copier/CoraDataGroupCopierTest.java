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
package se.uu.ub.cora.basicdata.copier;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.basicdata.data.CoraDataAtomic;
import se.uu.ub.cora.basicdata.data.CoraDataCopierSpy;
import se.uu.ub.cora.basicdata.data.CoraDataGroup;
import se.uu.ub.cora.data.DataElement;
import se.uu.ub.cora.data.DataGroup;

public class CoraDataGroupCopierTest {

	private DataGroup originalDataGroup;
	private CoraDataGroupCopier dataGroupCopier;
	private CoraDataCopierFactorySpy copierFactory;

	@BeforeMethod
	public void setUp() {
		originalDataGroup = CoraDataGroup.withNameInData("someDataGroup");
		copierFactory = new CoraDataCopierFactorySpy();
		dataGroupCopier = CoraDataGroupCopier.usingDataGroupAndCopierFactory(originalDataGroup,
				copierFactory);
	}

	@Test
	public void testGetCopierFactory() {
		assertEquals(dataGroupCopier.getCopierFactory(), copierFactory);
	}

	@Test
	public void testCopyDataGroupNotSameObject() {
		DataGroup dataGroupCopy = dataGroupCopier.copy();
		assertNotNull(dataGroupCopy);
		assertNotSame(originalDataGroup, dataGroupCopy);
	}

	@Test
	public void testCopyDataGroupSameNameInData() {
		DataGroup dataGroupCopy = dataGroupCopier.copy();
		assertEquals(dataGroupCopy.getNameInData(), originalDataGroup.getNameInData());
	}

	@Test
	public void testCopyDataAssertNoRepeatId() {
		DataGroup dataGroupCopy = dataGroupCopier.copy();
		assertNull(dataGroupCopy.getRepeatId());
	}

	@Test
	public void testCopyDataGroupWithRepeatId() {
		originalDataGroup.setRepeatId("1");
		DataGroup dataGroupCopy = dataGroupCopier.copy();
		assertEquals(dataGroupCopy.getRepeatId(), originalDataGroup.getRepeatId());
	}

	@Test
	public void testCopyDataGroupWithOneAttribute() {
		originalDataGroup.addAttributeByIdWithValue("type", "someTypeAttribute");
		DataGroup dataGroupCopy = dataGroupCopier.copy();
		assertEquals(dataGroupCopy.getAttribute("type").getValue(),
				originalDataGroup.getAttribute("type").getValue());
		assertEquals(dataGroupCopy.getAttributes().size(), 1);
	}

	@Test
	public void testCopyDataGroupWithTwoAttributes() {
		originalDataGroup.addAttributeByIdWithValue("type", "someTypeAttribute");
		originalDataGroup.addAttributeByIdWithValue("otherAttribute", "someOtherAttribute");
		DataGroup dataGroupCopy = dataGroupCopier.copy();

		assertEquals(dataGroupCopy.getAttribute("type").getValue(),
				originalDataGroup.getAttribute("type").getValue());

		assertEquals(dataGroupCopy.getAttribute("otherAttribute").getValue(),
				originalDataGroup.getAttribute("otherAttribute").getValue());
		assertEquals(dataGroupCopy.getAttributes().size(), 2);
	}

	@Test
	public void testCopyDataGroupOneChildDataAtomicIsCopied() {
		createAndAddAtomicChildToOrginalDataGroup("someAtomicChild", "someAtomicValue");

		DataGroup dataGroupCopy = dataGroupCopier.copy();
		assertEquals(dataGroupCopy.getNameInData(), originalDataGroup.getNameInData());

		assertChildIsSentToCopierUsingIndex(0);
		assertChildReturnedFromCopierIsAddedToGroupUsingIndex(dataGroupCopy, 0);
		assertEquals(dataGroupCopy.getChildren().size(), 1);
	}

	private void createAndAddAtomicChildToOrginalDataGroup(String nameInData, String value) {
		CoraDataAtomic atomicChild = CoraDataAtomic.withNameInDataAndValue(nameInData, value);
		originalDataGroup.addChild(atomicChild);
	}

	private void assertChildIsSentToCopierUsingIndex(int index) {
		DataElement dataElementSentToCopierFactory = copierFactory.dataElements.get(index);
		DataElement firstChildInOrignalDataGroup = originalDataGroup.getChildren().get(index);

		assertSame(dataElementSentToCopierFactory, firstChildInOrignalDataGroup);
		assertNotNull(copierFactory.dataElements.get(index));
	}

	private void assertChildReturnedFromCopierIsAddedToGroupUsingIndex(DataGroup dataGroupCopy,
			int index) {
		CoraDataCopierSpy factoredCopier = (CoraDataCopierSpy) copierFactory.factoredDataCopiers
				.get(index);
		assertTrue(factoredCopier.copyWasCalled);

		DataElement firstChildInCopiedGroup = dataGroupCopy.getChildren().get(index);
		CoraDataCopierSpy dataCopier = (CoraDataCopierSpy) copierFactory.factoredDataCopiers
				.get(index);
		DataElement elementReturnedFromCopier = dataCopier.returnedElement;

		assertSame(firstChildInCopiedGroup, elementReturnedFromCopier);
	}

	@Test
	public void testCopyDataGroupTwoChildrenDataAtomicsAreCopied() {
		createAndAddAtomicChildToOrginalDataGroup("someAtomicChild", "someAtomicValue");
		createAndAddAtomicChildToOrginalDataGroup("anotherAtomicChild", "anotherAtomicValue");

		DataGroup copiedDataGroup = dataGroupCopier.copy();

		assertChildIsSentToCopierUsingIndex(0);
		assertChildReturnedFromCopierIsAddedToGroupUsingIndex(copiedDataGroup, 0);
		assertChildIsSentToCopierUsingIndex(1);
		assertChildReturnedFromCopierIsAddedToGroupUsingIndex(copiedDataGroup, 1);
		assertEquals(copiedDataGroup.getChildren().size(), 2);
	}

	@Test
	public void testCopyDataGroupThreeChildrenDataAtomicsAndGroupAreCopied() {
		createAndAddAtomicChildToOrginalDataGroup("someAtomicChild", "someAtomicValue");
		createAndAddAtomicChildToOrginalDataGroup("anotherAtomicChild", "anotherAtomicValue");
		DataGroup childGroup = CoraDataGroup.withNameInData("childGroup");
		childGroup.addChild(
				CoraDataAtomic.withNameInDataAndValue("grandChldNameInData", "grandChildValue"));
		originalDataGroup.addChild(childGroup);

		DataGroup copiedDataGroup = dataGroupCopier.copy();

		assertChildIsSentToCopierUsingIndex(0);
		assertChildReturnedFromCopierIsAddedToGroupUsingIndex(copiedDataGroup, 0);
		assertChildIsSentToCopierUsingIndex(1);
		assertChildReturnedFromCopierIsAddedToGroupUsingIndex(copiedDataGroup, 1);
		assertChildReturnedFromCopierIsAddedToGroupUsingIndex(copiedDataGroup, 2);
		assertEquals(copiedDataGroup.getChildren().size(), 3);
	}
}
