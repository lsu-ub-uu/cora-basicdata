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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.Action;
import se.uu.ub.cora.data.Data;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataMissingException;
import se.uu.ub.cora.data.spies.DataGroupSpy;
import se.uu.ub.cora.data.spies.DataRecordGroupSpy;

public class CoraDataRecordTest {
	private CoraDataRecord dataRecord;
	private DataRecordGroupSpy dataRecordGroup;
	private DataGroupSpy recordInfoGroup;
	private DataGroupSpy typeLinkedGroup;
	private DataGroupSpy searchLinkedGroup;

	@BeforeMethod
	public void beforeMethod() {

		searchLinkedGroup = new DataGroupSpy();

		typeLinkedGroup = new DataGroupSpy();
		recordInfoGroup = new DataGroupSpy();
		recordInfoGroup.MRV.setSpecificReturnValuesSupplier("getFirstGroupWithNameInData",
				(Supplier<DataGroup>) () -> typeLinkedGroup, "type");

		dataRecordGroup = new DataRecordGroupSpy();
		dataRecordGroup.MRV.setSpecificReturnValuesSupplier("getFirstGroupWithNameInData",
				(Supplier<DataGroup>) () -> recordInfoGroup, "recordInfo");

		dataRecordGroup.MRV.setSpecificReturnValuesSupplier("getFirstGroupWithNameInData",
				(Supplier<DataGroup>) () -> searchLinkedGroup, "search");
		dataRecordGroup.MRV.setSpecificReturnValuesSupplier("containsChildWithNameInData",
				(Supplier<Boolean>) () -> true, "search");

		dataRecord = CoraDataRecord.withDataRecordGroup(dataRecordGroup);

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
		assertEquals(dataRecord.getDataRecordGroup(), dataRecordGroup);
	}

	@Test
	public void testSetDataGroup() {
		dataRecord.setDataRecordGroup(dataRecordGroup);

		assertEquals(dataRecord.getDataRecordGroup(), dataRecordGroup);
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
		dataRecord.setDataRecordGroup(null);
		dataRecord.getId();
	}

	@Test(expectedExceptions = DataMissingException.class, expectedExceptionsMessageRegExp = ""
			+ "Record id not known")
	public void testGetIdNoRecordInfoDataGroup() throws Exception {
		dataRecordGroup.MRV.setThrowException("getFirstGroupWithNameInData",
				new DataMissingException("DME from Spy"), "recordInfo");

		dataRecord.getId();
	}

	@Test(expectedExceptions = DataMissingException.class, expectedExceptionsMessageRegExp = ""
			+ "Record id not known")
	public void testGetIdNoIdDataGroup() throws Exception {
		recordInfoGroup.MRV.setThrowException("getFirstAtomicValueWithNameInData",
				new DataMissingException("DME from Spy"), "id");

		dataRecord.getId();
	}

	@Test
	public void testGetId() throws Exception {
		String recordId = dataRecord.getId();

		assertIdFetchedFromIdInRecordInfo(recordId);
	}

	private void assertIdFetchedFromIdInRecordInfo(String recordId) {
		dataRecordGroup.MCR.assertParameters("getFirstGroupWithNameInData", 0, "recordInfo");
		recordInfoGroup.MCR.assertParameters("getFirstAtomicValueWithNameInData", 0, "id");
		recordInfoGroup.MCR.assertReturn("getFirstAtomicValueWithNameInData", 0, recordId);
	}

	@Test
	public void testGetType() throws Exception {

		String returnType = dataRecord.getType();

		dataRecordGroup.MCR.assertParameters("getFirstGroupWithNameInData", 0, "recordInfo");
		recordInfoGroup.MCR.assertParameters("getFirstGroupWithNameInData", 0, "type");

		DataGroupSpy typeGroup = (DataGroupSpy) recordInfoGroup.MCR
				.getReturnValue("getFirstGroupWithNameInData", 0);
		typeGroup.MCR.assertParameters("getFirstAtomicValueWithNameInData", 0, "linkedRecordId");
		typeGroup.MCR.assertReturn("getFirstAtomicValueWithNameInData", 0, returnType);

	}

	@Test(expectedExceptions = DataMissingException.class, expectedExceptionsMessageRegExp = ""
			+ "Record type not known")
	public void testGetTypeNoDataGroup() throws Exception {
		dataRecord.setDataRecordGroup(null);
		dataRecord.getType();
	}

	@Test(expectedExceptions = DataMissingException.class, expectedExceptionsMessageRegExp = ""
			+ "Record type not known")
	public void testGetTypeNoRecordInfoDataGroup() throws Exception {
		dataRecordGroup.MRV.setThrowException("getFirstGroupWithNameInData",
				new DataMissingException("DME from Spy"), "recordInfo");

		dataRecord.getType();
	}

	@Test(expectedExceptions = DataMissingException.class, expectedExceptionsMessageRegExp = ""
			+ "Record type not known")

	public void testGetTypeNoLinkedTypeDataGroup() throws Exception {
		recordInfoGroup.MRV.setThrowException("getFirstGroupWithNameInData",
				new DataMissingException("DME from Spy"), "type");

		dataRecord.getType();
	}

	@Test(expectedExceptions = DataMissingException.class, expectedExceptionsMessageRegExp = ""
			+ "Record type not known")

	public void testGetTypeNoLinkedRecordIdAtomicGroup() throws Exception {
		typeLinkedGroup.MRV.setThrowException("getFirstAtomicValueWithNameInData",
				new DataMissingException("DME from Spy"), "linkedRecordId");

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
		dataRecordGroup.MRV.setThrowException("getFirstGroupWithNameInData",
				new DataMissingException("DME from Spy"), "linkedRecordId");

		dataRecord.getSearchId();
	}

	@Test(expectedExceptions = DataMissingException.class, expectedExceptionsMessageRegExp = ""
			+ "Record id not known")
	public void testGetIdForSearchButNoId() {
		typeLinkedGroup.MRV.setSpecificReturnValuesSupplier("getFirstAtomicValueWithNameInData",
				(Supplier<String>) () -> "search", "linkedRecordId");
		recordInfoGroup.MRV.setThrowException("getFirstAtomicValueWithNameInData",
				new DataMissingException("DME from Spy"), "id");

		dataRecord.getSearchId();
	}

	@Test(expectedExceptions = DataMissingException.class, expectedExceptionsMessageRegExp = ""
			+ "No searchId exists")
	public void testGetSearchIdForRecordTypeButNoSearchId() {
		recordInfoGroup.MRV.setThrowException("getFirstAtomicValueWithNameInData",
				new DataMissingException("DME from Spy"), "id");

		dataRecord.getSearchId();
	}

	@Test
	public void testGetSearchIdForSearch() {
		typeLinkedGroup.MRV.setSpecificReturnValuesSupplier("getFirstAtomicValueWithNameInData",
				(Supplier<String>) () -> "search", "linkedRecordId");

		String searchId = dataRecord.getSearchId();

		assertIdFetchedFromIdInRecordInfo(searchId);
	}

	private void assertSearchIdFetchedFromIdInSearchGroup(String searchId) {
		dataRecordGroup.MCR.assertParameters("containsChildWithNameInData", 0, "search");
		dataRecordGroup.MCR.assertParameters("getFirstGroupWithNameInData", 1, "search");
		searchLinkedGroup.MCR.assertParameters("getFirstAtomicValueWithNameInData", 0,
				"linkedRecordId");
		searchLinkedGroup.MCR.assertReturn("getFirstAtomicValueWithNameInData", 0, searchId);
	}

	@Test
	public void testGetSearchIdForRecordType() {
		typeLinkedGroup.MRV.setSpecificReturnValuesSupplier("getFirstAtomicValueWithNameInData",
				(Supplier<String>) () -> "recordType", "linkedRecordId");

		String searchId = dataRecord.getSearchId();
		assertSearchIdFetchedFromIdInSearchGroup(searchId);
	}

	@Test(expectedExceptions = DataMissingException.class, expectedExceptionsMessageRegExp = ""
			+ "No searchId exists")
	public void testGetSearchIdForRecordTypeNoSearch() {
		dataRecordGroup.MRV.setSpecificReturnValuesSupplier("containsChildWithNameInData",
				(Supplier<Boolean>) () -> false, "search");
		typeLinkedGroup.MRV.setSpecificReturnValuesSupplier("getFirstAtomicValueWithNameInData",
				(Supplier<String>) () -> "recordType", "linkedRecordId");

		dataRecord.getSearchId();
	}

	@Test
	public void testGetProtocolsNoProtocolsAdded() throws Exception {
		Set<String> protocols = dataRecord.getProtocols();
		assertEquals(protocols, Collections.emptyList());
	}

	@Test
	public void testGetProtocolsSeveralProtocolsAdded() throws Exception {

		dataRecord.addProtocol("someProtocol");
		dataRecord.addProtocol("someOtherProtocol");

		Set<String> protocols = dataRecord.getProtocols();
		assertEquals(protocols.size(), 2);
		assertTrue(protocols.contains("someProtocol"));
		assertTrue(protocols.contains("someOtherProtocol"));
	}

	@Test
	public void testAddProtocolsCannotAddedDuplicatedProtocols() throws Exception {
		dataRecord.addProtocol("sameProtocol");
		dataRecord.addProtocol("sameProtocol");

		Set<String> protocols = dataRecord.getProtocols();
		assertEquals(protocols.size(), 1);
	}
}