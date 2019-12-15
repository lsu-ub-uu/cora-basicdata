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

import se.uu.ub.cora.data.DataRecordLink;

public class CoraDataRecordLinkFactoryTest {

	private CoraDataRecordLinkFactory dataRecordLinkFactory;
	private String nameInData = "someDataRecordLinkNameInData";
	private String recordType = "someRecordType";
	private String recordId = "someRecordId";

	@BeforeMethod
	public void setUp() {
		dataRecordLinkFactory = new CoraDataRecordLinkFactory();
	}

	@Test
	public void testFactorUsingNameInData() {
		DataRecordLink factoredDataRecordLink = dataRecordLinkFactory.factorUsingNameInData(nameInData);
		assertEquals(factoredDataRecordLink.getNameInData(), nameInData);
	}

	@Test
	public void testFactorAsLink() {
		DataRecordLink factoredDataRecordLink = dataRecordLinkFactory
				.factorAsLinkWithNameInDataTypeAndId(nameInData, recordType, recordId);
		assertEquals(factoredDataRecordLink.getNameInData(), nameInData);
		assertEquals(factoredDataRecordLink.getChildren().size(), 2);
		assertEquals(factoredDataRecordLink.getFirstAtomicValueWithNameInData("linkedRecordType"),
				recordType);
		assertEquals(factoredDataRecordLink.getFirstAtomicValueWithNameInData("linkedRecordId"),
				recordId);
	}
}
