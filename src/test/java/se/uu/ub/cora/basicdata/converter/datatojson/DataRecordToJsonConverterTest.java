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

import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.basicdata.data.CoraDataRecord;
import se.uu.ub.cora.basicdata.data.DataGroupSpy;
import se.uu.ub.cora.basicdata.mcr.MethodCallRecorder;
import se.uu.ub.cora.data.Action;
import se.uu.ub.cora.data.converter.DataToJsonConverter;
import se.uu.ub.cora.json.builder.JsonObjectBuilder;

public class DataRecordToJsonConverterTest {

	private CoraDataRecord dataRecord;
	private DataRecordToJsonConverter dataRecordToJsonConverter;
	private JsonBuilderFactorySpy builderFactory;

	private DataToJsonConverterFactorySpy converterFactory;
	private String baseUrl = "some/base/url/";
	private DataGroupSpy dataGroup;

	@BeforeMethod
	public void setUp() {
		builderFactory = new JsonBuilderFactorySpy();
		dataGroup = new DataGroupSpy("groupNameInData");
		// dataRecord = new DataRecordSpy(dataGroup);
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

		builderFactory = new JsonBuilderFactorySpy();
		dataRecordToJsonConverter = DataRecordToJsonConverter
				.usingConverterFactoryAndBuilderFactoryAndDataRecord(converterFactory,
						builderFactory, null, dataRecord);

		dataRecordToJsonConverter.toJsonObjectBuilder();

		converterFactory.MCR.assertMethodNotCalled("factorUsingBaseUrlAndRecordUrlAndConvertible");
		converterFactory.MCR.assertParameters("factorUsingConvertible", 0,
				dataRecord.getDataGroup());

		DataToJsonConverterSpy dataGroupConverter = (DataToJsonConverterSpy) converterFactory.MCR
				.getReturnValue("factorUsingConvertible", 0);
		assertKeyDataAddedToRecordBuilderIsBuilderFromDataGroupConverter(dataGroupConverter);

		JsonObjectBuilderSpy recordBuilder = getRecordBuilderFromSpy();
		JsonObjectBuilderSpy rootWrappingBuilder = getRootWrappingBuilder();
		rootWrappingBuilder.MCR.assertParameters("addKeyJsonObjectBuilder", 0, "record",
				recordBuilder);
	}

	private JsonObjectBuilderSpy getRootWrappingBuilder() {
		return (JsonObjectBuilderSpy) builderFactory.MCR.getReturnValue("createObjectBuilder", 1);
	}

	private JsonObjectBuilderSpy getRecordBuilderFromSpy() {
		return (JsonObjectBuilderSpy) builderFactory.MCR.getReturnValue("createObjectBuilder", 0);
	}

	@Test
	public void testConverterFactoryUsedToCreateConverterForMainDataGroupWithBaseUrl()
			throws Exception {
		dataRecordToJsonConverter.toJsonObjectBuilder();

		converterFactory.MCR.assertMethodNotCalled("factorUsingConvertible");

		String recordUrl = baseUrl + dataRecord.getType() + "/" + dataRecord.getId();

		converterFactory.MCR.assertParameters("factorUsingBaseUrlAndRecordUrlAndConvertible", 0,
				baseUrl, recordUrl, dataRecord.getDataGroup());

		DataToJsonConverterSpy dataGroupConverter = (DataToJsonConverterSpy) converterFactory.MCR
				.getReturnValue("factorUsingBaseUrlAndRecordUrlAndConvertible", 0);
		assertKeyDataAddedToRecordBuilderIsBuilderFromDataGroupConverter(dataGroupConverter);

		JsonObjectBuilderSpy recordBuilder = getRecordBuilderFromSpy();
		JsonObjectBuilderSpy rootWrappingBuilder = getRootWrappingBuilder();
		rootWrappingBuilder.MCR.assertParameters("addKeyJsonObjectBuilder", 0, "record",
				recordBuilder);
	}

	private void assertKeyDataAddedToRecordBuilderIsBuilderFromDataGroupConverter(
			DataToJsonConverterSpy dataGroupConverter) {
		JsonObjectBuilderSpy dataGroupBuilder = (JsonObjectBuilderSpy) dataGroupConverter.MCR
				.getReturnValue("toJsonObjectBuilder", 0);

		JsonObjectBuilderSpy recordBuilder = getRecordBuilderFromSpy();
		recordBuilder.MCR.assertParameters("addKeyJsonObjectBuilder", 0, "data", dataGroupBuilder);
	}

	@Test
	public void testToJsonWithListOfReadPermissions() {
		String type = "read";
		dataRecord.addReadPermission("readPermissionOne");
		dataRecord.addReadPermission("readPermissionTwo");

		dataRecordToJsonConverter.toJsonObjectBuilder();
		assertTwoPermissionsAddedCorrectlyForType(type, 0);
	}

	private void assertTwoPermissionsAddedCorrectlyForType(String type, int postitionOfTypes) {
		JsonObjectBuilderSpy permissionBuilder = getPermissionBuilderFromSpy();
		JsonObjectBuilderSpy recordBuilder = getRecordBuilderFromSpy();

		JsonArrayBuilderSpy typePermissionBuilder = getTypePermissionArrayBuilderFromSpy(
				postitionOfTypes);

		typePermissionBuilder.MCR.assertParameters("addString", 0, type + "PermissionOne");
		typePermissionBuilder.MCR.assertParameters("addString", 1, type + "PermissionTwo");
		typePermissionBuilder.MCR.assertNumberOfCallsToMethod("addString", 2);

		permissionBuilder.MCR.assertParameters("addKeyJsonArrayBuilder", postitionOfTypes, type,
				typePermissionBuilder);

		recordBuilder.MCR.assertParameters("addKeyJsonObjectBuilder", 1, "permissions",
				permissionBuilder);
	}

	private JsonArrayBuilderSpy getTypePermissionArrayBuilderFromSpy(int postitionOfTypes) {
		return (JsonArrayBuilderSpy) builderFactory.MCR.getReturnValue("createArrayBuilder",
				postitionOfTypes);
	}

	private JsonObjectBuilderSpy getPermissionBuilderFromSpy() {
		return (JsonObjectBuilderSpy) builderFactory.MCR.getReturnValue("createObjectBuilder", 1);
	}

	@Test
	public void testToJsonWithWritePermissions() {
		String type = "write";
		dataRecord.addWritePermission("writePermissionOne");
		dataRecord.addWritePermission("writePermissionTwo");

		dataRecordToJsonConverter.toJsonObjectBuilder();
		assertTwoPermissionsAddedCorrectlyForType(type, 0);
	}

	@Test
	public void testToJsonWithReadAndWritePermissions() {
		dataRecord.addReadPermission("readPermissionOne");
		dataRecord.addReadPermission("readPermissionTwo");
		dataRecord.addWritePermission("writePermissionOne");
		dataRecord.addWritePermission("writePermissionTwo");

		dataRecordToJsonConverter.toJsonObjectBuilder();
		assertTwoPermissionsAddedCorrectlyForType("read", 0);
		assertTwoPermissionsAddedCorrectlyForType("write", 1);
	}

	@Test
	public void testToJson() {
		DataRecordToJsonConverterForTest forTest = new DataRecordToJsonConverterForTest();

		String jsonString = forTest.toJson();

		forTest.MCR.assertMethodWasCalled("toJsonObjectBuilder");
		JsonObjectBuilderSpy builderSpy = (JsonObjectBuilderSpy) forTest.MCR
				.getReturnValue("toJsonObjectBuilder", 0);
		builderSpy.MCR.assertReturn("toJsonFormattedPrettyString", 0, jsonString);
	}

	@Test
	public void testToJsonCompactFormat() {
		DataRecordToJsonConverterForTest forTest = new DataRecordToJsonConverterForTest();

		String jsonString = forTest.toJsonCompactFormat();

		forTest.MCR.assertMethodWasCalled("toJsonObjectBuilder");
		JsonObjectBuilderSpy builderSpy = (JsonObjectBuilderSpy) forTest.MCR
				.getReturnValue("toJsonObjectBuilder", 0);
		builderSpy.MCR.assertReturn("toJsonFormattedString", 0, jsonString);
	}

	class DataRecordToJsonConverterForTest extends DataRecordToJsonConverter {
		MethodCallRecorder MCR = new MethodCallRecorder();

		DataRecordToJsonConverterForTest() {
			super(null, builderFactory, null, null);
		}

		@Override
		public JsonObjectBuilder toJsonObjectBuilder() {
			MCR.addCall();
			JsonObjectBuilderSpy jsonObjectBuilderSpy = new JsonObjectBuilderSpy();
			MCR.addReturned(jsonObjectBuilderSpy);
			return jsonObjectBuilderSpy;
		}

	}

	@Test
	public void testConvertActionsNoActions() throws Exception {

		// TODO: Use a dataRecord spy.
		DataRecordSpy dataRecordSpy = setUpDataRecordSpy();

		dataRecordToJsonConverter.toJsonObjectBuilder();

		dataRecordSpy.MCR.assertMethodNotCalled("getActions");
	}

	@Test
	public void testConvertActionsOneAction() throws Exception {

		// TODO: Use a dataRecord spy.
		DataRecordSpy dataRecordSpy = setUpDataRecordSpy();

		addOneReadAction(dataRecordSpy);

		dataRecordToJsonConverter.toJsonObjectBuilder();

		dataRecordSpy.MCR.assertMethodWasCalled("getActions");
	}

	private void addOneReadAction(DataRecordSpy dataRecordSpy) {
		List<Action> actionList = new ArrayList<>();
		actionList.add(Action.READ);
		dataRecordSpy.actions = actionList;
	}

	private DataRecordSpy setUpDataRecordSpy() {
		DataRecordSpy dataRecordSpy = new DataRecordSpy(dataGroup);
		dataRecordToJsonConverter = DataRecordToJsonConverter
				.usingConverterFactoryAndBuilderFactoryAndDataRecord(converterFactory,
						builderFactory, baseUrl, dataRecordSpy);
		return dataRecordSpy;
	}
}
