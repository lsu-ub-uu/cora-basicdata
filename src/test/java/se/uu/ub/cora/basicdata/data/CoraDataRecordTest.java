/*
 * Copyright 2015, 2022 Uppsala University Library
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
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.basicdata.data.spy.DataGroupSpy;
import se.uu.ub.cora.data.Action;
import se.uu.ub.cora.data.Data;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataMissingException;

public class CoraDataRecordTest {
	private CoraDataRecord dataRecord;
	private DataGroup dataGroup;

	@BeforeMethod
	public void beforeMethod() {
		dataGroup = CoraDataGroup.withNameInData("nameInData");
		dataRecord = CoraDataRecord.withDataGroup(dataGroup);
	}

	@Test
	public void testRecordIsData() {
		assertTrue(dataRecord instanceof Data);
	}

	@Test
	public void testAddAction() {
		dataRecord.addAction(Action.READ);

		assertTrue(dataRecord.getActions().contains(Action.READ));
		assertFalse(dataRecord.getActions().contains(Action.DELETE));
		// small hack to get 100% coverage on enum
		Action.valueOf(Action.READ.toString());
	}

	@Test
	public void testGetDataGroup() {
		String nameInData = dataRecord.getDataGroup().getNameInData();
		assertEquals(nameInData, "nameInData");
	}

	@Test
	public void testDataGroup() {
		DataGroup dataGroup = CoraDataGroup.withNameInData("nameInData");
		dataRecord.setDataGroup(dataGroup);
		assertEquals(dataRecord.getDataGroup(), dataGroup);
	}

	@Test
	public void testGetReadPermissions() {
		dataRecord.addReadPermission("rating");
		dataRecord.addReadPermission("value");
		Set<String> readPermissions = dataRecord.getReadPermissions();
		assertTrue(readPermissions.contains("rating"));
		assertTrue(readPermissions.contains("value"));
	}

	@Test
	public void testAddReadPermissions() {
		Set<String> readPermissionsToSet = createSetWithValues("rating", "value");
		dataRecord.addReadPermissions(readPermissionsToSet);

		Set<String> readPermissions = dataRecord.getReadPermissions();

		assertEquals(readPermissions.size(), 2);
		assertSetContains(readPermissions, "rating", "value");

		Set<String> readPermissionsToSet2 = createSetWithValues("rating2", "value2");
		dataRecord.addReadPermissions(readPermissionsToSet2);

		readPermissions = dataRecord.getReadPermissions();

		assertEquals(readPermissions.size(), 4);
		assertSetContains(readPermissions, "rating", "value", "rating2", "value2");

	}

	private Set<String> createSetWithValues(String... values) {
		Set<String> permissions = new HashSet<>();
		for (String value : values) {
			permissions.add(value);
		}

		return permissions;
	}

	private void assertSetContains(Set<String> permissions, String... values) {
		for (String value : values) {
			assertTrue(permissions.contains(value));

		}
	}

	@Test
	public void testGetWritePermissions() {
		dataRecord.addWritePermission("title");
		dataRecord.addWritePermission("author");
		Set<String> writePermissions = dataRecord.getWritePermissions();
		assertTrue(writePermissions.contains("title"));
		assertTrue(writePermissions.contains("author"));
	}

	@Test
	public void testAddWritePermissions() {
		Set<String> writePermissionsToSet = createSetWithValues("rating", "value");
		dataRecord.addWritePermissions(writePermissionsToSet);

		Set<String> writePermissions = dataRecord.getWritePermissions();

		assertEquals(writePermissions.size(), 2);
		assertSetContains(writePermissions, "rating", "value");

		Set<String> writePermissionsToSet2 = createSetWithValues("rating2", "value2");
		dataRecord.addWritePermissions(writePermissionsToSet2);

		writePermissions = dataRecord.getWritePermissions();

		assertEquals(writePermissions.size(), 4);
		assertSetContains(writePermissions, "rating", "value", "rating2", "value2");
	}

	@Test(expectedExceptions = DataMissingException.class, expectedExceptionsMessageRegExp = ""
			+ "Record id not known")
	public void testGetIdNoDataGroup() throws Exception {
		dataRecord.setDataGroup(null);
		dataRecord.getId();
	}

	@Test(expectedExceptions = DataMissingException.class, expectedExceptionsMessageRegExp = ""
			+ "Record id not known")
	public void testGetIdNoRecordInfoDataGroup() throws Exception {
		DataGroupSpy dataGroup = new DataGroupSpy("nameInData");
		dataGroup.throwException = true;
		dataRecord = CoraDataRecord.withDataGroup(dataGroup);

		dataRecord.getId();
	}

	@Test(expectedExceptions = DataMissingException.class, expectedExceptionsMessageRegExp = ""
			+ "Record id not known")

	public void testGetIdNoIdDataGroup() throws Exception {
		CoraDataGroup dataGroup = CoraDataGroup.withNameInData("nameInData");
		dataRecord = CoraDataRecord.withDataGroup(dataGroup);

		dataRecord.getId();
	}

	@Test
	public void testGetId() throws Exception {
		DataGroupSpy dataGroup = new DataGroupSpy("nameInData");
		dataRecord = CoraDataRecord.withDataGroup(dataGroup);

		String recordId = dataRecord.getId();

		dataGroup.MCR.assertParameters("getFirstGroupWithNameInData", 0, "recordInfo");
		DataGroupSpy recordInfo = (DataGroupSpy) dataGroup.MCR
				.getReturnValue("getFirstGroupWithNameInData", 0);

		recordInfo.MCR.assertParameters("getFirstAtomicValueWithNameInData", 0, "id");

		recordInfo.MCR.assertReturn("getFirstAtomicValueWithNameInData", 0, recordId);

	}

	@Test
	public void testGetType() throws Exception {
		DataGroupSpy dataGroup = new DataGroupSpy("nameInData");
		dataRecord = CoraDataRecord.withDataGroup(dataGroup);

		String returnType = dataRecord.getType();

		dataGroup.MCR.assertParameters("getFirstGroupWithNameInData", 0, "recordInfo");
		DataGroupSpy recordInfo = (DataGroupSpy) dataGroup.MCR
				.getReturnValue("getFirstGroupWithNameInData", 0);

		recordInfo.MCR.assertParameters("getFirstGroupWithNameInData", 0, "type");

		DataGroupSpy typeGroup = (DataGroupSpy) recordInfo.MCR
				.getReturnValue("getFirstGroupWithNameInData", 0);
		typeGroup.MCR.assertParameters("getFirstAtomicValueWithNameInData", 0, "linkedRecordId");

		typeGroup.MCR.assertReturn("getFirstAtomicValueWithNameInData", 0, returnType);

	}

	@Test(expectedExceptions = DataMissingException.class, expectedExceptionsMessageRegExp = ""
			+ "Record type not known")
	public void testGetTypeNoDataGroup() throws Exception {
		dataRecord.setDataGroup(null);
		dataRecord.getType();
	}

	@Test(expectedExceptions = DataMissingException.class, expectedExceptionsMessageRegExp = ""
			+ "Record type not known")
	public void testGetTypeNoRecordInfoDataGroup() throws Exception {
		DataGroupSpy dataGroup = new DataGroupSpy("nameInData");
		dataGroup.throwException = true;
		dataRecord = CoraDataRecord.withDataGroup(dataGroup);

		dataRecord.getType();
	}

	@Test(expectedExceptions = DataMissingException.class, expectedExceptionsMessageRegExp = ""
			+ "Record type not known")

	public void testGetTypeNoLinkedTypeDataGroup() throws Exception {
		DataGroup dataGroup = CoraDataGroup.withNameInData("nameInData");
		dataRecord = CoraDataRecord.withDataGroup(dataGroup);

		dataRecord.getType();
	}

	@Test(expectedExceptions = DataMissingException.class, expectedExceptionsMessageRegExp = ""
			+ "Record type not known")

	public void testGetTypeNoLinkedRecordIdAtomicGroup() throws Exception {
		DataGroup dataGroup = CoraDataGroup.withNameInData("nameInData");
		DataGroup recordInfo = CoraDataGroup.withNameInData("recordInfo");
		recordInfo.addChild(CoraDataGroup.withNameInData("type"));

		dataGroup.addChild(recordInfo);

		dataRecord = CoraDataRecord.withDataGroup(dataGroup);

		dataRecord.getType();
	}

	@Test
	public void testHasActionsNoActions() throws Exception {
		assertFalse(dataRecord.hasActions());
	}

	@Test
	public void testHasActionsRecordHasActions() throws Exception {
		dataRecord.addAction(Action.CREATE);
		assertTrue(dataRecord.hasActions());
	}

	@Test
	public void testHasReadPremissionsNoReadPermissions() throws Exception {
		assertFalse(dataRecord.hasReadPermissions());
	}

	@Test
	public void testHasReadPremissionsHasReadPermissions() throws Exception {
		dataRecord.addReadPermission("ReadPermission");
		assertTrue(dataRecord.hasReadPermissions());
	}

	@Test
	public void testHasWritePremissionsNoWritePermissions() throws Exception {
		assertFalse(dataRecord.hasWritePermissions());
	}

	@Test
	public void testHasWritedPremissionsHasWritePermissions() throws Exception {
		dataRecord.addWritePermission("WritePermission");
		assertTrue(dataRecord.hasWritePermissions());
	}

	@Test(expectedExceptions = DataMissingException.class, expectedExceptionsMessageRegExp = ""
			+ "No searchId exists")
	public void testGetSearchIdNotSearchOrRecordType() {
		DataGroup dataGroup = createDataGroup("someOtherType", "someId");
		dataRecord = CoraDataRecord.withDataGroup(dataGroup);
		dataRecord.getSearchId();
	}

	@Test(expectedExceptions = DataMissingException.class, expectedExceptionsMessageRegExp = ""
			+ "Record id not known")
	public void testGetSearchIdForSearchButNoSearchId() {
		CoraDataGroup dataGroup = createDataGroup("search", "searchId");
		removeSearchId(dataGroup);

		dataRecord = CoraDataRecord.withDataGroup(dataGroup);
		dataRecord.getSearchId();
	}

	private void removeSearchId(CoraDataGroup dataGroup) {
		DataGroup recordInfo = dataGroup.getFirstGroupWithNameInData("recordInfo");
		recordInfo.removeFirstChildWithNameInData("id");
	}

	@Test(expectedExceptions = DataMissingException.class, expectedExceptionsMessageRegExp = ""
			+ "No searchId exists")
	public void testGetSearchIdForRecordTypeButNoSearchId() {
		CoraDataGroup dataGroup = createDataGroup("recordType", "someId");
		dataRecord = CoraDataRecord.withDataGroup(dataGroup);
		dataRecord.getSearchId();
	}

	@Test
	public void testGetSearchIdForSearch() {
		CoraDataGroup dataGroup = createDataGroup("search", "someSearchId");
		dataRecord = CoraDataRecord.withDataGroup(dataGroup);
		String searchId = dataRecord.getSearchId();
		assertEquals(searchId, "someSearchId");
	}

	@Test
	public void testGetSearchIdForRecordType() {
		DataGroup dataGroup = createDataGroup("recordType", "someRecordType");
		addSearchDataGroup(dataGroup);
		dataRecord = CoraDataRecord.withDataGroup(dataGroup);

		String searchId = dataRecord.getSearchId();
		assertEquals(searchId, "someLinkedSearch");
	}

	private void addSearchDataGroup(DataGroup dataGroup) {
		DataGroup searchDataGroup = CoraDataGroup.withNameInData("search");
		searchDataGroup.addChild(
				CoraDataAtomic.withNameInDataAndValue("linkedRecordId", "someLinkedSearch"));
		dataGroup.addChild(searchDataGroup);
	}

	private CoraDataGroup createDataGroup(String type, String id) {
		CoraDataGroup dataGroup = CoraDataGroup.withNameInData("searchOrRecordType");
		CoraDataGroup recordInfo = CoraDataGroup.withNameInData("recordInfo");
		recordInfo.addChild(CoraDataAtomic.withNameInDataAndValue("id", id));

		addType(type, recordInfo);
		dataGroup.addChild(recordInfo);
		return dataGroup;
	}

	private void addType(String type, CoraDataGroup recordInfo) {
		CoraDataGroup typeGroup = CoraDataGroup.withNameInData("type");
		typeGroup.addChild(CoraDataAtomic.withNameInDataAndValue("linkedRecordId", type));
		recordInfo.addChild(typeGroup);
	}
}
