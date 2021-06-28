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
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
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
	private RecordActionsToJsonConverterSpy actionsConverterSpy;

	@BeforeMethod
	public void setUp() {
		converterFactory = new DataToJsonConverterFactorySpy();
		actionsConverterSpy = new RecordActionsToJsonConverterSpy();
		builderFactory = new JsonBuilderFactorySpy();

		dataGroup = new DataGroupSpy("groupNameInData");
		dataRecord = CoraDataRecord.withDataGroup(dataGroup);

	}

	private void createDataRecordToJsonConverter() {
		dataRecordToJsonConverter = DataRecordToJsonConverter
				.usingConverterFactoryAndActionsConverterAndBuilderFactoryAndBaseUrlAndDataRecord(
						converterFactory, actionsConverterSpy, builderFactory, baseUrl, dataRecord);
	}

	@Test
	public void testConverterImplementsDataToJsonConverter() throws Exception {
		assertTrue(dataRecordToJsonConverter instanceof DataToJsonConverter);
	}

	@Test
	public void testConverterFactoryUsedToCreateConverterForMainDataGroupNoBaseUrl()
			throws Exception {

		// builderFactory = new JsonBuilderFactorySpy();
		dataRecordToJsonConverter = DataRecordToJsonConverter
				.usingConverterFactoryAndActionsConverterAndBuilderFactoryAndBaseUrlAndDataRecord(
						converterFactory, actionsConverterSpy, builderFactory, null, dataRecord);

		JsonObjectBuilder returnedJsonObjectBuilder = dataRecordToJsonConverter
				.toJsonObjectBuilder();

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
		assertSame(returnedJsonObjectBuilder, rootWrappingBuilder);
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
		createDataRecordToJsonConverter();

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
		createDataRecordToJsonConverter();

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
		createDataRecordToJsonConverter();

		String type = "write";
		dataRecord.addWritePermission("writePermissionOne");
		dataRecord.addWritePermission("writePermissionTwo");

		dataRecordToJsonConverter.toJsonObjectBuilder();
		assertTwoPermissionsAddedCorrectlyForType(type, 0);
	}

	@Test
	public void testToJsonWithReadAndWritePermissions() {
		createDataRecordToJsonConverter();

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
		DataRecordToJsonConverterForTest forTest = new DataRecordToJsonConverterForTest(
				builderFactory);

		String jsonString = forTest.toJson();

		forTest.MCR.assertMethodWasCalled("toJsonObjectBuilder");
		JsonObjectBuilderSpy builderSpy = (JsonObjectBuilderSpy) forTest.MCR
				.getReturnValue("toJsonObjectBuilder", 0);
		builderSpy.MCR.assertReturn("toJsonFormattedString", 0, jsonString);
	}

	@Test
	public void testToJsonCompactFormat() {
		DataRecordToJsonConverterForTest forTest = new DataRecordToJsonConverterForTest(
				builderFactory);

		String jsonString = forTest.toJsonCompactFormat();

		forTest.MCR.assertMethodWasCalled("toJsonObjectBuilder");
		JsonObjectBuilderSpy builderSpy = (JsonObjectBuilderSpy) forTest.MCR
				.getReturnValue("toJsonObjectBuilder", 0);
		builderSpy.MCR.assertReturn("toJsonFormattedString", 0, jsonString);
	}

	class DataRecordToJsonConverterForTest extends DataRecordToJsonConverter {
		MethodCallRecorder MCR = new MethodCallRecorder();

		DataRecordToJsonConverterForTest(JsonBuilderFactorySpy builderFactory) {
			super(null, null, builderFactory, null, null);
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
		DataRecordSpy dataRecordSpy = createDataRecordToJsonConverterUsingDataRecordSpy();

		dataRecordToJsonConverter.toJsonObjectBuilder();

		dataRecordSpy.MCR.assertMethodNotCalled("getActions");
	}

	@Test
	public void testConvertActionsAllTypes() throws Exception {
		// builderFactory = new JsonBuilderFactorySpy();
		DataRecordSpy dataRecordSpy = createDataRecordToJsonConverterUsingDataRecordSpy();

		addActionsToDataRecordSpy(dataRecordSpy);

		dataRecordToJsonConverter.toJsonObjectBuilder();

		actionsConverterSpy.MCR.assertParameters("toJsonObjectBuilder", 0);
		assertActionConverterData(dataRecordSpy);

		JsonObjectBuilderSpy actionLinksBuilder = (JsonObjectBuilderSpy) actionsConverterSpy.MCR
				.getReturnValue("toJsonObjectBuilder", 0);
		JsonObjectBuilderSpy recordBuilder = (JsonObjectBuilderSpy) builderFactory.MCR
				.getReturnValue("createObjectBuilder", 0);

		recordBuilder.MCR.assertNumberOfCallsToMethod("addKeyJsonObjectBuilder", 2);
		recordBuilder.MCR.assertParameters("addKeyJsonObjectBuilder", 1, "actionLinks",
				actionLinksBuilder);

	}

	private void assertActionConverterData(DataRecordSpy dataRecordSpy) {
		ActionsConverterData actionConverter = (ActionsConverterData) actionsConverterSpy.MCR
				.getValueForMethodNameAndCallNumberAndParameterName("toJsonObjectBuilder", 0,
						"actionsConverterData");
		assertEquals(actionConverter.recordType, dataRecordSpy.getType());
		assertEquals(actionConverter.recordId, dataRecordSpy.getId());
		assertEquals(actionConverter.actions, dataRecordSpy.getActions());
		assertNull(actionConverter.searchRecordId);
	}

	private void addActionsToDataRecordSpy(DataRecordSpy dataRecordSpy) {
		List<Action> actionList = new ArrayList<>();
		actionList.add(Action.READ);
		actionList.add(Action.UPDATE);
		dataRecordSpy.actions = actionList;
	}

	private DataRecordSpy createDataRecordToJsonConverterUsingDataRecordSpy() {
		DataRecordSpy dataRecordSpy = new DataRecordSpy(dataGroup);
		dataRecordToJsonConverter = DataRecordToJsonConverter
				.usingConverterFactoryAndActionsConverterAndBuilderFactoryAndBaseUrlAndDataRecord(
						converterFactory, actionsConverterSpy, builderFactory, baseUrl,
						dataRecordSpy);
		return dataRecordSpy;
	}

	@Test
	public void testConvertSearchActionForRecordTypeAndSearchRecordId() throws Exception {
		DataRecordSpy dataRecordSpy = createDataRecordToJsonConverterUsingDataRecordSpy();
		dataRecordSpy.type = "recordType";
		addSearchActionToDataRecordSpy(dataRecordSpy);
		dataGroup.searchGroupDefined = true;

		dataRecordToJsonConverter.toJsonObjectBuilder();

		assertSearchRecordIdIsFromDataGroupRecord(dataRecordSpy);

	}

	private void assertSearchRecordIdIsFromDataGroupRecord(DataRecordSpy dataRecordSpy) {
		dataRecordSpy.MCR.assertNumberOfCallsToMethod("getDataGroup", 2);
		DataGroupSpy dataGroup = (DataGroupSpy) dataRecordSpy.MCR.getReturnValue("getDataGroup", 1);
		dataGroup.MCR.assertParameters("containsChildWithNameInData", 0, "search");
		DataGroupSpy searchGroup = (DataGroupSpy) dataGroup.MCR
				.getReturnValue("getFirstGroupWithNameInData", 0);
		searchGroup.MCR.assertParameters("getFirstAtomicValueWithNameInData", 0, "linkedRecordId");

		String searchId = (String) searchGroup.MCR
				.getReturnValue("getFirstAtomicValueWithNameInData", 0);

		ActionsConverterData actionConverter = (ActionsConverterData) actionsConverterSpy.MCR
				.getValueForMethodNameAndCallNumberAndParameterName("toJsonObjectBuilder", 0,
						"actionsConverterData");
		assertSame(actionConverter.searchRecordId, searchId);
	}

	private void addSearchActionToDataRecordSpy(DataRecordSpy dataRecordSpy) {
		List<Action> actionList = new ArrayList<>();
		actionList.add(Action.SEARCH);
		dataRecordSpy.actions = actionList;
	}

	@Test
	public void testConvertSearchActionForRecordTypeAndSearchRecordIdOnlyForRecordType()
			throws Exception {
		DataRecordSpy dataRecordSpy = createDataRecordToJsonConverterUsingDataRecordSpy();
		dataRecordSpy.type = "otherThanRecordType";
		addSearchActionToDataRecordSpy(dataRecordSpy);
		dataGroup.searchGroupDefined = true;

		dataRecordToJsonConverter.toJsonObjectBuilder();

		assertSearchRecordIdNotSet();
	}

	private void assertSearchRecordIdNotSet() {
		ActionsConverterData actionConverter = (ActionsConverterData) actionsConverterSpy.MCR
				.getValueForMethodNameAndCallNumberAndParameterName("toJsonObjectBuilder", 0,
						"actionsConverterData");
		assertSame(actionConverter.searchRecordId, null);
	}

	@Test
	public void testConvertSearchActionForRecordTypeAndSearchRecordIdButNoSearchDefinedInDataGroup()
			throws Exception {
		DataRecordSpy dataRecordSpy = createDataRecordToJsonConverterUsingDataRecordSpy();
		dataRecordSpy.type = "recordType";
		dataGroup.searchGroupDefined = false;
		addSearchActionToDataRecordSpy(dataRecordSpy);

		dataRecordToJsonConverter.toJsonObjectBuilder();

		assertSearchRecordIdNotSet();
	}
}
