/*
 * Copyright 2015, 2019, 2021 Uppsala University Library
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

package se.uu.ub.cora.basicdata.converter.datatojson;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.basicdata.data.CoraDataGroup;
import se.uu.ub.cora.basicdata.data.CoraDataRecord;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.converter.DataToJsonConverter;
import se.uu.ub.cora.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.json.builder.org.OrgJsonBuilderFactoryAdapter;

public class DataRecordToJsonConverterTest {

	private CoraDataRecord dataRecord;
	private DataRecordToJsonConverter dataRecordToJsonConverter;
	JsonBuilderFactory builderFactory;
	private DataToJsonConverterFactorySpy converterFactory;
	private String baseUrl = "some/base/url";

	@BeforeMethod
	public void setUp() {
		builderFactory = new OrgJsonBuilderFactoryAdapter();
		DataGroup dataGroup = CoraDataGroup.withNameInData("groupNameInData");
		dataRecord = CoraDataRecord.withDataGroup(dataGroup);

		converterFactory = new DataToJsonConverterFactorySpy();

		dataRecordToJsonConverter = DataRecordToJsonConverter
				.usingConverterFactoryAndBuilderFactoryAndDataRecord(converterFactory,
						builderFactory, baseUrl, dataRecord);
	}

	@Test
	public void testConverterImplementsDataToJsonConverter() throws Exception {
		assertTrue(dataRecordToJsonConverter instanceof DataToJsonConverter);
	}

	@Test
	public void testConverterFactoryUsedToCreateConverterForMainDataGroupNoBaseUrl()
			throws Exception {
		dataRecordToJsonConverter = DataRecordToJsonConverter
				.usingConverterFactoryAndBuilderFactoryAndDataRecord(converterFactory,
						builderFactory, null, dataRecord);
		dataRecordToJsonConverter.toJsonObjectBuilder();

		converterFactory.MCR.assertMethodWasCalled("factorUsingConvertible");
		converterFactory.MCR.assertMethodNotCalled("factorUsingBaseUrlAndRecordUrlAndConvertible");
		converterFactory.MCR.assertParameters("factorUsingConvertible", 0,
				dataRecord.getDataGroup());
	}

	@Test
	public void testConverterFactoryUsedToCreateConverterForMainDataGroup() throws Exception {
		dataRecordToJsonConverter.toJsonObjectBuilder();

		converterFactory.MCR.assertMethodNotCalled("factorUsingConvertible");
		converterFactory.MCR.assertMethodWasCalled("factorUsingBaseUrlAndRecordUrlAndConvertible");
		// TODO: should not be null, fetch from record (new method in record)
		converterFactory.MCR.assertParameters("factorUsingBaseUrlAndRecordUrlAndConvertible", 0,
				baseUrl, null, dataRecord.getDataGroup());
	}

	@Test
	public void testToJson() {
		String jsonString = dataRecordToJsonConverter.toJson();

		assertEquals(jsonString, "{\"record\":{\"data\":{\"name\":\"groupNameInData\"}}}");
	}

	@Test
	public void testToJsonWithReadPermissions() {
		dataRecord.addReadPermission("readPermissionOne");
		dataRecord.addReadPermission("readPermissionTwo");

		String jsonString = dataRecordToJsonConverter.toJson();

		assertEquals(jsonString,
				"{\"record\":{\"data\":{\"name\":\"groupNameInData\"},\"permissions\":{\"read\":[\"readPermissionOne\",\"readPermissionTwo\"]}}}");
	}

	@Test
	public void testToJsonWithWritePermissions() {
		dataRecord.addWritePermission("writePermissionOne");
		dataRecord.addWritePermission("writePermissionTwo");

		String jsonString = dataRecordToJsonConverter.toJson();

		assertEquals(jsonString,
				"{\"record\":{\"data\":{\"name\":\"groupNameInData\"},\"permissions\":{\"write\":[\"writePermissionOne\",\"writePermissionTwo\"]}}}");
	}

	@Test
	public void testToJsonWithReadAndWritePermissions() {
		dataRecord.addReadPermission("readPermissionOne");
		dataRecord.addReadPermission("readPermissionTwo");
		dataRecord.addWritePermission("writePermissionOne");
		dataRecord.addWritePermission("writePermissionTwo");

		String jsonString = dataRecordToJsonConverter.toJson();

		assertEquals(jsonString,
				"{\"record\":{\"data\":{\"name\":\"groupNameInData\"},\"permissions\":{\"read\":[\"readPermissionOne\",\"readPermissionTwo\"],\"write\":[\"writePermissionOne\",\"writePermissionTwo\"]}}}");
	}

}
