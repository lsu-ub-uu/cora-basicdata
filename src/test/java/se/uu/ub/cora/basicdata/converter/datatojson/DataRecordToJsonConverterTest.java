/*
 * Copyright 2015, 2019, 2021, 2022 Uppsala University Library
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
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.Action;
import se.uu.ub.cora.data.converter.DataToJsonConverter;
import se.uu.ub.cora.data.converter.ExternalUrls;
import se.uu.ub.cora.data.spies.DataGroupSpy;
import se.uu.ub.cora.data.spies.DataRecordGroupSpy;
import se.uu.ub.cora.data.spies.DataRecordSpy;
import se.uu.ub.cora.json.builder.JsonObjectBuilder;
import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;

public class DataRecordToJsonConverterTest {
	private DataRecordSpy dataRecord;
	private DataRecordToJsonConverter dataRecordToJsonConverter;
	private JsonBuilderFactorySpy builderFactory;

	private DataToJsonConverterFactorySpy converterFactory;
	private DataRecordGroupSpy dataRecordGroup;
	private RecordActionsToJsonConverterSpy actionsConverterSpy;
	private String baseUrl = "some/base/url/";
	private String iiifBaseUrl = "someIiifBaseUrl";
	private Optional<ExternalUrls> externalUrls;

	@BeforeMethod
	public void setUp() {
		converterFactory = new DataToJsonConverterFactorySpy();
		actionsConverterSpy = new RecordActionsToJsonConverterSpy();
		builderFactory = new JsonBuilderFactorySpy();
		dataRecordGroup = new DataRecordGroupSpy();
		dataRecord = new DataRecordSpy();
		dataRecord.MRV.setDefaultReturnValuesSupplier("getDataRecordGroup", () -> dataRecordGroup);

		externalUrls = createExternalUrls();
	}

	private Optional<ExternalUrls> createExternalUrls() {
		ExternalUrls externalUrls = new ExternalUrls();
		externalUrls.setBaseUrl(baseUrl);
		externalUrls.setIfffUrl(iiifBaseUrl);
		return Optional.of(externalUrls);
	}

	private void createDataRecordToJsonConverter() {
		dataRecordToJsonConverter = DataRecordToJsonConverter
				.usingConverterFactoryAndActionsConverterAndBuilderFactoryAndExternalUrls(
						dataRecord, converterFactory, actionsConverterSpy, builderFactory,
						externalUrls);
	}

	@Test
	public void testConverterImplementsDataToJsonConverter() throws Exception {
		createDataRecordToJsonConverter();
		assertTrue(dataRecordToJsonConverter instanceof DataToJsonConverter);
	}

	@Test
	public void testConverterFactoryUsedToCreateConverterForMainDataGroupNoBaseUrl()
			throws Exception {
		dataRecordToJsonConverter = DataRecordToJsonConverter
				.usingConverterFactoryAndActionsConverterAndBuilderFactoryAndExternalUrls(
						dataRecord, converterFactory, actionsConverterSpy, builderFactory,
						Optional.empty());

		JsonObjectBuilder returnedJsonObjectBuilder = dataRecordToJsonConverter
				.toJsonObjectBuilder();

		converterFactory.MCR.assertMethodNotCalled("factorUsingBaseUrlAndRecordUrlAndConvertible");
		converterFactory.MCR.assertParameters("factorUsingConvertible", 0,
				dataRecord.getDataRecordGroup());

		DataToJsonConverterSpy dataGroupConverter = (DataToJsonConverterSpy) converterFactory.MCR
				.getReturnValue("factorUsingConvertible", 0);
		assertKeyDataAddedToRecordBuilderIsBuilderFromDataGroupConverter(dataGroupConverter);

		JsonObjectBuilderSpy recordBuilder = getRecordBuilderFromSpy();
		JsonObjectBuilderSpy rootWrappingBuilder = getRootWrappingBuilder();
		rootWrappingBuilder.MCR.assertParameters("addKeyJsonObjectBuilder", 0, "record",
				recordBuilder);
		assertSame(returnedJsonObjectBuilder, rootWrappingBuilder);
	}

	private void assertKeyDataAddedToRecordBuilderIsBuilderFromDataGroupConverter(
			DataToJsonConverterSpy dataGroupConverter) {
		JsonObjectBuilderSpy dataGroupBuilder = (JsonObjectBuilderSpy) dataGroupConverter.MCR
				.getReturnValue("toJsonObjectBuilder", 0);

		JsonObjectBuilderSpy recordBuilder = getRecordBuilderFromSpy();
		recordBuilder.MCR.assertParameters("addKeyJsonObjectBuilder", 0, "data", dataGroupBuilder);
	}

	private JsonObjectBuilderSpy getRootWrappingBuilder() {
		return (JsonObjectBuilderSpy) builderFactory.MCR.getReturnValue("createObjectBuilder", 1);
	}

	private JsonObjectBuilderSpy getRecordBuilderFromSpy() {
		return getRecordJsonObjectBuilder();
	}

	@Test
	public void testConverterFactoryUsedToCreateConverterForMainDataGroupWithBaseUrl()
			throws Exception {
		createDataRecordToJsonConverter();

		dataRecordToJsonConverter.toJsonObjectBuilder();

		converterFactory.MCR.assertMethodNotCalled("factorUsingConvertible");

		String recordUrl = baseUrl + dataRecord.getType() + "/" + dataRecord.getId();

		converterFactory.MCR.assertParameters("factorUsingBaseUrlAndRecordUrlAndConvertible", 0,
				baseUrl, recordUrl, dataRecord.getDataRecordGroup());

		DataToJsonConverterSpy dataGroupConverter = (DataToJsonConverterSpy) converterFactory.MCR
				.getReturnValue("factorUsingBaseUrlAndRecordUrlAndConvertible", 0);
		assertKeyDataAddedToRecordBuilderIsBuilderFromDataGroupConverter(dataGroupConverter);

		JsonObjectBuilderSpy recordBuilder = getRecordBuilderFromSpy();
		JsonObjectBuilderSpy rootWrappingBuilder = getRootWrappingBuilder();
		rootWrappingBuilder.MCR.assertParameters("addKeyJsonObjectBuilder", 0, "record",
				recordBuilder);
	}

	@Test
	public void testToJsonWithListOfReadPermissions() {
		dataRecord.MRV.setDefaultReturnValuesSupplier("hasReadPermissions",
				(Supplier<Boolean>) () -> true);

		Set<String> readPermissions = Set.of("readPermissionOne", "readPermissionTwo");
		dataRecord.MRV.setDefaultReturnValuesSupplier("getReadPermissions",
				(Supplier<Set<String>>) () -> readPermissions);

		createDataRecordToJsonConverter();

		dataRecordToJsonConverter.toJsonObjectBuilder();

		assertTwoPermissionsAddedCorrectlyForType("read", 0);
	}

	private void assertTwoPermissionsAddedCorrectlyForType(String type, int postitionOfTypes) {
		JsonObjectBuilderSpy permissionBuilder = getPermissionBuilderFromSpy();
		JsonObjectBuilderSpy recordBuilder = getRecordBuilderFromSpy();

		JsonArrayBuilderSpy typePermissionBuilder = getTypePermissionArrayBuilderFromSpy(
				postitionOfTypes);

		assertPermissionsCalledAddString(type, typePermissionBuilder);

		typePermissionBuilder.MCR.assertNumberOfCallsToMethod("addString", 2);

		permissionBuilder.MCR.assertParameters("addKeyJsonArrayBuilder", postitionOfTypes, type,
				typePermissionBuilder);

		recordBuilder.MCR.assertParameters("addKeyJsonObjectBuilder", 1, "permissions",
				permissionBuilder);
	}

	private void assertPermissionsCalledAddString(String type,
			JsonArrayBuilderSpy typePermissionBuilder) {
		List<Object> values = getAllParameterValuesOnCallAddString(typePermissionBuilder);

		assertTrue(
				values.contains(type + "PermissionOne") && values.contains(type + "PermissionTwo"));
	}

	private List<Object> getAllParameterValuesOnCallAddString(
			JsonArrayBuilderSpy typePermissionBuilder) {
		List<Object> values = new ArrayList<>();
		for (int i = 0; i <= 1; i++) {
			Object value = getValueFromAParameter(typePermissionBuilder, i);
			values.add(value);
		}
		return values;
	}

	private Object getValueFromAParameter(JsonArrayBuilderSpy typePermissionBuilder, int i) {
		Map<String, Object> parameters = typePermissionBuilder.MCR
				.getParametersForMethodAndCallNumber("addString", i);
		Object value = parameters.get("value");
		return value;
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
		dataRecord.MRV.setDefaultReturnValuesSupplier("hasWritePermissions",
				(Supplier<Boolean>) () -> true);

		Set<String> writePermissions = Set.of("writePermissionOne", "writePermissionTwo");
		dataRecord.MRV.setDefaultReturnValuesSupplier("getWritePermissions",
				(Supplier<Set<String>>) () -> writePermissions);

		createDataRecordToJsonConverter();

		dataRecordToJsonConverter.toJsonObjectBuilder();
		assertTwoPermissionsAddedCorrectlyForType("write", 0);
	}

	@Test
	public void testToJsonWithReadAndWritePermissions() {
		dataRecord.MRV.setDefaultReturnValuesSupplier("hasReadPermissions",
				(Supplier<Boolean>) () -> true);
		dataRecord.MRV.setDefaultReturnValuesSupplier("hasWritePermissions",
				(Supplier<Boolean>) () -> true);

		Set<String> readPermissions = Set.of("readPermissionOne", "readPermissionTwo");
		dataRecord.MRV.setDefaultReturnValuesSupplier("getReadPermissions",
				(Supplier<Set<String>>) () -> readPermissions);
		Set<String> writePermissions = Set.of("writePermissionOne", "writePermissionTwo");
		dataRecord.MRV.setDefaultReturnValuesSupplier("getWritePermissions",
				(Supplier<Set<String>>) () -> writePermissions);

		createDataRecordToJsonConverter();

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
		builderSpy.MCR.assertReturn("toJsonFormattedPrettyString", 0, jsonString);
		builderSpy.MCR.assertMethodWasCalled("toJsonFormattedPrettyString");
	}

	private class DataRecordToJsonConverterForTest extends DataRecordToJsonConverter {
		MethodCallRecorder MCR = new MethodCallRecorder();

		DataRecordToJsonConverterForTest(JsonBuilderFactorySpy builderFactory) {
			super(null, null, builderFactory, Optional.empty(), null);
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
	public void testToJsonCompactFormat() {
		DataRecordToJsonConverterForTest forTest = new DataRecordToJsonConverterForTest(
				builderFactory);

		String jsonString = forTest.toJsonCompactFormat();

		forTest.MCR.assertMethodWasCalled("toJsonObjectBuilder");
		JsonObjectBuilderSpy builderSpy = (JsonObjectBuilderSpy) forTest.MCR
				.getReturnValue("toJsonObjectBuilder", 0);
		builderSpy.MCR.assertReturn("toJsonFormattedString", 0, jsonString);
	}

	@Test
	public void testConvertActionsNoActions() throws Exception {
		createDataRecordToJsonConverter();

		dataRecordToJsonConverter.toJsonObjectBuilder();

		dataRecord.MCR.assertMethodNotCalled("getActions");
	}

	@Test
	public void testConvertActionsAllTypes() throws Exception {
		createDataRecordToJsonConverter();
		dataRecord.MRV.setDefaultReturnValuesSupplier("hasActions", (Supplier<Boolean>) () -> true);

		addActionsToDataRecordSpy(dataRecord);

		dataRecordToJsonConverter.toJsonObjectBuilder();

		actionsConverterSpy.MCR.assertParameters("toJsonObjectBuilder", 0);
		assertActionConverterData(dataRecord);

		JsonObjectBuilderSpy actionLinksBuilder = (JsonObjectBuilderSpy) actionsConverterSpy.MCR
				.getReturnValue("toJsonObjectBuilder", 0);
		JsonObjectBuilderSpy recordBuilder = getRecordJsonObjectBuilder();

		recordBuilder.MCR.assertNumberOfCallsToMethod("addKeyJsonObjectBuilder", 2);
		recordBuilder.MCR.assertParameters("addKeyJsonObjectBuilder", 1, "actionLinks",
				actionLinksBuilder);
	}

	private void assertActionConverterData(DataRecordSpy dataRecordSpy) {
		ActionsConverterData actionConverter = (ActionsConverterData) actionsConverterSpy.MCR
				.getParameterForMethodAndCallNumberAndParameter("toJsonObjectBuilder", 0,
						"actionsConverterData");
		assertEquals(actionConverter.recordType, dataRecordSpy.getType());
		assertEquals(actionConverter.recordId, dataRecordSpy.getId());
		assertEquals(actionConverter.actions, dataRecordSpy.getActions());
		assertNull(actionConverter.searchRecordId);
	}

	private void addActionsToDataRecordSpy(DataRecordSpy dataRecordSpy) {
		List<Action> actionList = List.of(Action.READ, Action.UPDATE);

		dataRecordSpy.MRV.setDefaultReturnValuesSupplier("getActions", () -> actionList);
	}

	@Test
	public void testConvertSearchActionForRecordTypeAndSearchRecordId() throws Exception {
		createDataRecordToJsonConverter();
		dataRecord.MRV.setDefaultReturnValuesSupplier("hasActions", () -> true);
		dataRecord.MRV.setDefaultReturnValuesSupplier("getType", () -> "recordType");
		dataRecordGroup.MRV.setSpecificReturnValuesSupplier("containsChildWithNameInData",
				() -> true, "search");
		DataGroupSpy searchGroup = new DataGroupSpy();
		searchGroup.MRV.setSpecificReturnValuesSupplier("getFirstAtomicValueWithNameInData",
				() -> "someSearchId", "linkedRecordId");
		dataRecordGroup.MRV.setSpecificReturnValuesSupplier("getFirstGroupWithNameInData",
				() -> searchGroup, "search");

		dataRecordToJsonConverter.toJsonObjectBuilder();

		assertSearchRecordIdIsFromDataGroupRecord(dataRecord);
	}

	private void assertSearchRecordIdIsFromDataGroupRecord(DataRecordSpy dataRecordSpy) {
		dataRecordSpy.MCR.assertNumberOfCallsToMethod("getDataRecordGroup", 2);
		DataGroupSpy searchGroup = (DataGroupSpy) dataRecordGroup.MCR
				.getReturnValue("getFirstGroupWithNameInData", 0);
		searchGroup.MCR.assertParameters("getFirstAtomicValueWithNameInData", 0, "linkedRecordId");

		String searchId = (String) searchGroup.MCR
				.getReturnValue("getFirstAtomicValueWithNameInData", 0);

		ActionsConverterData actionConverter = (ActionsConverterData) actionsConverterSpy.MCR
				.getParameterForMethodAndCallNumberAndParameter("toJsonObjectBuilder", 0,
						"actionsConverterData");
		assertSame(actionConverter.searchRecordId, searchId);
	}

	@Test
	public void testConvertSearchActionForRecordTypeAndSearchRecordIdOnlyForRecordType()
			throws Exception {
		createDataRecordToJsonConverter();
		dataRecord.MRV.setDefaultReturnValuesSupplier("hasActions", () -> true);
		dataRecord.MRV.setDefaultReturnValuesSupplier("getType", () -> "otherThanRecordType");

		dataRecordToJsonConverter.toJsonObjectBuilder();

		assertSearchRecordIdNotSet();
	}

	private void assertSearchRecordIdNotSet() {
		ActionsConverterData actionConverter = (ActionsConverterData) actionsConverterSpy.MCR
				.getParameterForMethodAndCallNumberAndParameter("toJsonObjectBuilder", 0,
						"actionsConverterData");
		assertSame(actionConverter.searchRecordId, null);
	}

	@Test
	public void testConvertSearchActionForRecordTypeAndSearchRecordIdButNoSearchDefinedInDataGroup()
			throws Exception {
		createDataRecordToJsonConverter();
		dataRecord.MRV.setDefaultReturnValuesSupplier("hasActions", () -> true);
		dataRecord.MRV.setDefaultReturnValuesSupplier("getType", () -> "recordType");

		dataRecordToJsonConverter.toJsonObjectBuilder();

		assertSearchRecordIdNotSet();
	}

	@Test
	public void testHasProtocolsIIIF() throws Exception {
		createDataRecordToJsonConverter();

		dataRecord.MRV.setDefaultReturnValuesSupplier("getId", () -> "someId");
		dataRecord.MRV.setDefaultReturnValuesSupplier("getProtocols", () -> Set.of("iiif"));

		dataRecordToJsonConverter.toJsonObjectBuilder();

		builderFactory.MCR.assertNumberOfCallsToMethod("createObjectBuilder", 4);
		JsonObjectBuilderSpy iiifBody = assertIIIFBodyAndReturnIt();
		JsonObjectBuilderSpy iiifProtocol = assertIIIFProtocolAndReturnIt(iiifBody);
		assertOtherProtocols(iiifProtocol);
	}

	private JsonObjectBuilderSpy assertIIIFBodyAndReturnIt() {
		JsonObjectBuilderSpy iiifBody = (JsonObjectBuilderSpy) builderFactory.MCR
				.getReturnValue("createObjectBuilder", 1);
		iiifBody.MCR.assertParameters("addKeyString", 0, "server", iiifBaseUrl);
		iiifBody.MCR.assertParameters("addKeyString", 1, "identifier", "someId");

		return iiifBody;
	}

	private JsonObjectBuilderSpy assertIIIFProtocolAndReturnIt(JsonObjectBuilderSpy iiifBody) {
		JsonObjectBuilderSpy iiifProtocol = (JsonObjectBuilderSpy) builderFactory.MCR
				.getReturnValue("createObjectBuilder", 2);
		iiifProtocol.MCR.assertParameters("addKeyJsonObjectBuilder", 0, "iiif", iiifBody);
		return iiifProtocol;
	}

	private void assertOtherProtocols(JsonObjectBuilderSpy iiifHeader) {
		JsonObjectBuilderSpy recordJsonObjectBuilder = getRecordJsonObjectBuilder();
		recordJsonObjectBuilder.MCR.assertParameters("addKeyJsonObjectBuilder", 1, "otherProtocols",
				iiifHeader);
	}

	private JsonObjectBuilderSpy getRecordJsonObjectBuilder() {
		return (JsonObjectBuilderSpy) builderFactory.MCR.getReturnValue("createObjectBuilder", 0);
	}

	@Test
	public void testDoNotAddprotocolsIfDoNotExist() throws Exception {
		createDataRecordToJsonConverter();

		dataRecord.MRV.setDefaultReturnValuesSupplier("getId", () -> "someId");

		dataRecordToJsonConverter.toJsonObjectBuilder();

		builderFactory.MCR.assertNumberOfCallsToMethod("createObjectBuilder", 2);
	}

	@Test
	public void testProtocolsDifferentThanIIIFOtherProtocolsNotAdded() throws Exception {
		createDataRecordToJsonConverter();

		dataRecord.MRV.setDefaultReturnValuesSupplier("getId", () -> "someId");
		dataRecord.MRV.setDefaultReturnValuesSupplier("getProtocols", () -> Set.of("someProtocol"));

		dataRecordToJsonConverter.toJsonObjectBuilder();

		JsonObjectBuilderSpy recordJsonObjectBuilder = getRecordJsonObjectBuilder();
		recordJsonObjectBuilder.MCR.assertNumberOfCallsToMethod("addKeyJsonArrayBuilder", 0);
	}
}
