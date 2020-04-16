/*
 * Copyright 2015 Uppsala University Library
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

import se.uu.ub.cora.data.Action;
import se.uu.ub.cora.data.Data;
import se.uu.ub.cora.data.DataGroup;

public class CoraDataRecordTest {
	private CoraDataRecord dataRecord;

	@BeforeMethod
	public void beforeMethod() {
		DataGroup dataGroup = CoraDataGroup.withNameInData("nameInData");
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

}
