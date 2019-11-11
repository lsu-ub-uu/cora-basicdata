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

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataGroupFactory;

public class CoraDataGroupFactoryTest {

	private DataGroupFactory dataGroupFactory;
	private String nameInData = "someDataGroupNameInData";
	private String recordType = "someRecordType";
	private String recordId = "someRecordId";

	@BeforeMethod
	public void setUp() {
		dataGroupFactory = new CoraDataGroupFactory();
	}

	@Test
	public void testFactorUsingNameInData() {
		DataGroup factoredDataGroup = (DataGroup) dataGroupFactory
				.factorUsingNameInData(nameInData);
		assertEquals(factoredDataGroup.getNameInData(), nameInData);
	}

	@Test
	public void testFactorAsLink() {
		DataGroup factoredDataGroup = (DataGroup) dataGroupFactory
				.factorAsLinkWithNameInDataTypeAndId(nameInData, recordType, recordId);
		assertEquals(factoredDataGroup.getNameInData(), nameInData);
		assertEquals(factoredDataGroup.getChildren().size(), 2);
		assertEquals(factoredDataGroup.getFirstAtomicValueWithNameInData("linkedRecordType"),
				recordType);
		assertEquals(factoredDataGroup.getFirstAtomicValueWithNameInData("linkedRecordId"),
				recordId);
	}
}
