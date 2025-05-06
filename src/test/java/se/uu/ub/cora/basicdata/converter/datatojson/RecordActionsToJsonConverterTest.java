/*
 * Copyright 2021 Uppsala University Library
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

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.Action;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.json.builder.JsonObjectBuilder;

public class RecordActionsToJsonConverterTest {

	private static final String GET = "GET";
	private static final String POST = "POST";
	private RecordActionsToJsonConverter actionsConverter;
	private JsonBuilderFactorySpy builderFactory;
	private String baseUrl = "some/base/url/";
	private static final String APPLICATION_VND_CORA_RECORD_LIST_JSON = "application/vnd.cora.recordList+json";
	private static final String APPLICATION_VND_CORA_RECORD_JSON = "application/vnd.cora.record+json";
	private static final String APPLICATION_VND_CORA_RECORDGROUP_JSON = "application/vnd.cora.recordgroup+json";

	private DataToJsonConverterFactorySpy converterFactory;
	private ActionsConverterData actionsConverterData;

	@BeforeMethod
	public void beforeMethod() {
		builderFactory = new JsonBuilderFactorySpy();

		converterFactory = new DataToJsonConverterFactorySpy();
		actionsConverter = RecordActionsToJsonConverterImp
				.usingConverterFactoryAndBuilderFactoryAndBaseUrl(converterFactory, builderFactory,
						baseUrl);
		actionsConverterData = new ActionsConverterData();
		actionsConverterData.recordType = "someRecordType";
		actionsConverterData.recordId = "someRecordId";
	}

	@Test
	public void testToJsonObjectBuilder() {
		JsonObjectBuilder objectBuilder = actionsConverter
				.toJsonObjectBuilder(actionsConverterData);

		builderFactory.MCR.assertMethodWasCalled("createObjectBuilder");
		builderFactory.MCR.assertReturn("createObjectBuilder", 0, objectBuilder);
		builderFactory.MCR.assertNumberOfCallsToMethod("createObjectBuilder", 1);
	}

	@Test
	public void testReadAction() {
		Action action = Action.READ;
		actionsConverterData.actions.add(action);

		String lowerCaseAction = action.name().toLowerCase();
		String url = baseUrl + actionsConverterData.recordType + "/"
				+ actionsConverterData.recordId;

		String requestMethod = GET;
		String accept = APPLICATION_VND_CORA_RECORD_JSON;
		String contentType = null;

		actionsConverter.toJsonObjectBuilder(actionsConverterData);

		builderFactory.MCR.assertNumberOfCallsToMethod("createObjectBuilder", 2);
		assertMainAndInternalLinkBuilder(lowerCaseAction, url, requestMethod, accept, contentType);
	}

	private void assertMainAndInternalLinkBuilder(String action, String url, String requestMethod,
			String accept, String contentType) {
		JsonObjectBuilderSpy mainLinkBuilder = (JsonObjectBuilderSpy) builderFactory.MCR
				.getReturnValue("createObjectBuilder", 0);
		JsonObjectBuilderSpy internalLinkBuilder = (JsonObjectBuilderSpy) builderFactory.MCR
				.getReturnValue("createObjectBuilder", 1);
		int position = 3;
		internalLinkBuilder.MCR.assertParameters("addKeyString", 0, "rel", action);
		internalLinkBuilder.MCR.assertParameters("addKeyString", 1, "url", url);
		internalLinkBuilder.MCR.assertParameters("addKeyString", 2, "requestMethod", requestMethod);
		if (accept != null) {
			// int position =
			internalLinkBuilder.MCR.assertParameters("addKeyString", position, "accept", accept);
			position++;
		}
		if (contentType != null) {
			internalLinkBuilder.MCR.assertParameters("addKeyString", position, "contentType",
					contentType);
			position++;
		}
		int callsToAddkeyString = position;
		internalLinkBuilder.MCR.assertNumberOfCallsToMethod("addKeyString", callsToAddkeyString);

		mainLinkBuilder.MCR.assertParameters("addKeyJsonObjectBuilder", 0, action,
				internalLinkBuilder);
	}

	@Test
	public void testUpdateAction() {
		Action action = Action.UPDATE;
		actionsConverterData.actions.add(action);
		String lowerCaseAction = action.name().toLowerCase();
		String url = baseUrl + actionsConverterData.recordType + "/"
				+ actionsConverterData.recordId;

		String requestMethod = POST;
		String accept = APPLICATION_VND_CORA_RECORD_JSON;
		String contentType = APPLICATION_VND_CORA_RECORDGROUP_JSON;

		actionsConverter.toJsonObjectBuilder(actionsConverterData);

		builderFactory.MCR.assertNumberOfCallsToMethod("createObjectBuilder", 2);
		assertMainAndInternalLinkBuilder(lowerCaseAction, url, requestMethod, accept, contentType);
	}

	@Test
	public void testReadIncommingLinksAction() {
		Action action = Action.READ_INCOMING_LINKS;
		actionsConverterData.actions.add(action);
		String lowerCaseAction = action.name().toLowerCase();
		String url = baseUrl + actionsConverterData.recordType + "/" + actionsConverterData.recordId
				+ "/incomingLinks";

		String requestMethod = GET;
		String accept = APPLICATION_VND_CORA_RECORD_LIST_JSON;
		String contentType = null;

		actionsConverter.toJsonObjectBuilder(actionsConverterData);

		builderFactory.MCR.assertNumberOfCallsToMethod("createObjectBuilder", 2);
		assertMainAndInternalLinkBuilder(lowerCaseAction, url, requestMethod, accept, contentType);
	}

	@Test
	public void testDeleteAction() {
		Action action = Action.DELETE;
		actionsConverterData.actions.add(action);
		String lowerCaseAction = action.name().toLowerCase();
		String url = baseUrl + actionsConverterData.recordType + "/"
				+ actionsConverterData.recordId;

		String requestMethod = "DELETE";
		String accept = null;
		String contentType = null;

		actionsConverter.toJsonObjectBuilder(actionsConverterData);

		builderFactory.MCR.assertNumberOfCallsToMethod("createObjectBuilder", 2);
		assertMainAndInternalLinkBuilder(lowerCaseAction, url, requestMethod, accept, contentType);
	}

	@Test
	public void testIndexAction() {
		Action action = Action.INDEX;
		actionsConverterData.actions.add(action);

		String lowerCaseAction = action.name().toLowerCase();
		String url = baseUrl + "workOrder/";
		String requestMethod = POST;
		String accept = APPLICATION_VND_CORA_RECORD_JSON;
		String contentType = APPLICATION_VND_CORA_RECORD_JSON;

		actionsConverter.toJsonObjectBuilder(actionsConverterData);

		builderFactory.MCR.assertNumberOfCallsToMethod("createObjectBuilder", 2);
		assertMainAndInternalLinkBuilder(lowerCaseAction, url, requestMethod, accept, contentType);

		assertWorkOrderBuilderCreatedUsingConverterFactoryWithDataGroupAddedToInternalLinkBuilderAsBody();

		assertCreatedWorkOrderBodyDataStructureIsCorrect();
	}

	private void assertWorkOrderBuilderCreatedUsingConverterFactoryWithDataGroupAddedToInternalLinkBuilderAsBody() {
		JsonObjectBuilderSpy internalLinkBuilder = (JsonObjectBuilderSpy) builderFactory.MCR
				.getReturnValue("createObjectBuilder", 1);

		DataToJsonConverterSpy converter = (DataToJsonConverterSpy) converterFactory.MCR
				.getReturnValue("factorUsingConvertible", 0);
		var workOrderBuilder = converter.MCR.getReturnValue("toJsonObjectBuilder", 0);

		internalLinkBuilder.MCR.assertParameters("addKeyJsonObjectBuilder", 0, "body",
				workOrderBuilder);
	}

	private void assertCreatedWorkOrderBodyDataStructureIsCorrect() {
		DataGroup workOrder = (DataGroup) converterFactory.MCR
				.getParameterForMethodAndCallNumberAndParameter("factorUsingConvertible", 0,
						"convertible");
		assertEquals(workOrder.getNameInData(), "workOrder");
		DataGroup recordTypeGroup = workOrder.getFirstGroupWithNameInData("recordType");
		assertEquals(recordTypeGroup.getFirstAtomicValueWithNameInData("linkedRecordType"),
				"recordType");
		assertEquals(recordTypeGroup.getFirstAtomicValueWithNameInData("linkedRecordId"),
				actionsConverterData.recordType);
		assertEquals(workOrder.getFirstAtomicValueWithNameInData("recordId"),
				actionsConverterData.recordId);
		assertEquals(workOrder.getFirstAtomicValueWithNameInData("type"), "index");
	}

	@Test
	public void testSearchAction() {
		Action action = Action.SEARCH;
		actionsConverterData.actions.add(action);

		String lowerCaseAction = action.name().toLowerCase();
		String url = baseUrl + "searchResult/" + actionsConverterData.recordId;
		String requestMethod = GET;
		String accept = APPLICATION_VND_CORA_RECORD_LIST_JSON;
		String contentType = null;

		actionsConverter.toJsonObjectBuilder(actionsConverterData);

		builderFactory.MCR.assertNumberOfCallsToMethod("createObjectBuilder", 2);
		assertMainAndInternalLinkBuilder(lowerCaseAction, url, requestMethod, accept, contentType);
	}

	@Test
	public void testUploadAction() {
		Action action = Action.UPLOAD;
		actionsConverterData.actions.add(action);

		String lowerCaseAction = action.name().toLowerCase();
		String url = baseUrl + actionsConverterData.recordType + "/" + actionsConverterData.recordId
				+ "/master";
		String requestMethod = POST;
		String accept = null;
		String contentType = "multipart/form-data";

		actionsConverter.toJsonObjectBuilder(actionsConverterData);

		builderFactory.MCR.assertNumberOfCallsToMethod("createObjectBuilder", 2);
		assertMainAndInternalLinkBuilder(lowerCaseAction, url, requestMethod, accept, contentType);
	}

	@Test
	public void testSearchActionForRecordTypeWithoutSearchId() {
		Action action = Action.SEARCH;
		actionsConverterData.actions.add(action);

		String lowerCaseAction = action.name().toLowerCase();
		String url = baseUrl + "searchResult/" + actionsConverterData.recordId;
		String requestMethod = GET;
		String accept = APPLICATION_VND_CORA_RECORD_LIST_JSON;
		String contentType = null;

		actionsConverter.toJsonObjectBuilder(actionsConverterData);

		builderFactory.MCR.assertNumberOfCallsToMethod("createObjectBuilder", 2);
		assertMainAndInternalLinkBuilder(lowerCaseAction, url, requestMethod, accept, contentType);
	}

	@Test
	public void testSearchActionForRecordTypeTypeWithSearchId() {
		Action action = Action.SEARCH;
		actionsConverterData.actions.add(action);

		String lowerCaseAction = action.name().toLowerCase();
		actionsConverterData.searchRecordId = "someSearchRecordId";
		String url = baseUrl + "searchResult/" + actionsConverterData.searchRecordId;
		String requestMethod = GET;
		String accept = APPLICATION_VND_CORA_RECORD_LIST_JSON;
		String contentType = null;

		actionsConverter.toJsonObjectBuilder(actionsConverterData);

		builderFactory.MCR.assertNumberOfCallsToMethod("createObjectBuilder", 2);
		assertMainAndInternalLinkBuilder(lowerCaseAction, url, requestMethod, accept, contentType);
	}

	@Test
	public void testCreateAction() {
		Action action = Action.CREATE;
		actionsConverterData.actions.add(action);
		actionsConverterData.recordType = "recordType";

		String lowerCaseAction = action.name().toLowerCase();
		String url = baseUrl + actionsConverterData.recordId + "/";
		String requestMethod = POST;
		String accept = APPLICATION_VND_CORA_RECORD_JSON;
		String contentType = APPLICATION_VND_CORA_RECORDGROUP_JSON;

		actionsConverter.toJsonObjectBuilder(actionsConverterData);

		builderFactory.MCR.assertNumberOfCallsToMethod("createObjectBuilder", 2);
		assertMainAndInternalLinkBuilder(lowerCaseAction, url, requestMethod, accept, contentType);
	}

	@Test
	public void testListAction() {
		Action action = Action.LIST;
		actionsConverterData.actions.add(action);
		actionsConverterData.recordType = "recordType";

		String lowerCaseAction = action.name().toLowerCase();
		String url = baseUrl + actionsConverterData.recordId + "/";
		String requestMethod = GET;
		String accept = APPLICATION_VND_CORA_RECORD_LIST_JSON;
		String contentType = null;

		actionsConverter.toJsonObjectBuilder(actionsConverterData);

		builderFactory.MCR.assertNumberOfCallsToMethod("createObjectBuilder", 2);
		assertMainAndInternalLinkBuilder(lowerCaseAction, url, requestMethod, accept, contentType);
	}

	@Test
	public void testBatchIndexAction() {
		Action action = Action.BATCH_INDEX;
		actionsConverterData.actions.add(action);
		actionsConverterData.recordType = "recordType";

		String lowerCaseAction = action.name().toLowerCase();
		String url = baseUrl + "index/" + actionsConverterData.recordId + "/";
		String requestMethod = POST;
		String accept = APPLICATION_VND_CORA_RECORD_JSON;
		String contentType = APPLICATION_VND_CORA_RECORD_JSON;

		actionsConverter.toJsonObjectBuilder(actionsConverterData);

		builderFactory.MCR.assertNumberOfCallsToMethod("createObjectBuilder", 2);
		assertMainAndInternalLinkBuilder(lowerCaseAction, url, requestMethod, accept, contentType);
	}

	@Test
	public void testValidateAction() {
		Action action = Action.VALIDATE;
		actionsConverterData.actions.add(action);
		actionsConverterData.recordType = "recordType";

		String lowerCaseAction = action.name().toLowerCase();
		String url = baseUrl + "workOrder/";
		String requestMethod = POST;
		String accept = APPLICATION_VND_CORA_RECORD_JSON;
		String contentType = "application/vnd.cora.workorder+json";

		actionsConverter.toJsonObjectBuilder(actionsConverterData);

		builderFactory.MCR.assertNumberOfCallsToMethod("createObjectBuilder", 2);
		assertMainAndInternalLinkBuilder(lowerCaseAction, url, requestMethod, accept, contentType);
	}

	@Test
	public void testReadForRecordType() {
		Action readAction = Action.READ;
		actionsConverterData.actions.add(readAction);
		actionsConverterData.recordType = "recordType";

		String lowerCaseAction = readAction.name().toLowerCase();
		String url = baseUrl + actionsConverterData.recordType + "/"
				+ actionsConverterData.recordId;

		String requestMethod = GET;
		String accept = APPLICATION_VND_CORA_RECORD_JSON;
		String contentType = null;

		actionsConverter.toJsonObjectBuilder(actionsConverterData);

		builderFactory.MCR.assertNumberOfCallsToMethod("createObjectBuilder", 2);
		assertMainAndInternalLinkBuilder(lowerCaseAction, url, requestMethod, accept, contentType);
	}

	@Test
	public void testUpdateActionForRecordType() {
		Action action = Action.UPDATE;
		actionsConverterData.actions.add(action);
		actionsConverterData.recordType = "recordType";

		String lowerCaseAction = action.name().toLowerCase();
		String url = baseUrl + actionsConverterData.recordType + "/"
				+ actionsConverterData.recordId;
		String requestMethod = POST;
		String accept = APPLICATION_VND_CORA_RECORD_JSON;
		String contentType = APPLICATION_VND_CORA_RECORDGROUP_JSON;

		actionsConverter.toJsonObjectBuilder(actionsConverterData);

		builderFactory.MCR.assertNumberOfCallsToMethod("createObjectBuilder", 2);
		assertMainAndInternalLinkBuilder(lowerCaseAction, url, requestMethod, accept, contentType);
	}

	@Test
	public void testReadIncommingLinksActionForRecordType() {
		Action action = Action.READ_INCOMING_LINKS;
		actionsConverterData.actions.add(action);
		actionsConverterData.recordType = "recordType";

		String lowerCaseAction = action.name().toLowerCase();
		String url = baseUrl + actionsConverterData.recordType + "/" + actionsConverterData.recordId
				+ "/incomingLinks";
		String requestMethod = GET;
		String accept = APPLICATION_VND_CORA_RECORD_LIST_JSON;
		String contentType = null;

		actionsConverter.toJsonObjectBuilder(actionsConverterData);

		builderFactory.MCR.assertNumberOfCallsToMethod("createObjectBuilder", 2);
		assertMainAndInternalLinkBuilder(lowerCaseAction, url, requestMethod, accept, contentType);
	}

	@Test
	public void testDeleteActionForRecordType() {
		Action action = Action.DELETE;
		actionsConverterData.actions.add(action);
		actionsConverterData.recordType = "recordType";

		String lowerCaseAction = action.name().toLowerCase();
		String url = baseUrl + actionsConverterData.recordType + "/"
				+ actionsConverterData.recordId;
		String requestMethod = "DELETE";
		String accept = null;
		String contentType = null;

		actionsConverter.toJsonObjectBuilder(actionsConverterData);

		builderFactory.MCR.assertNumberOfCallsToMethod("createObjectBuilder", 2);
		assertMainAndInternalLinkBuilder(lowerCaseAction, url, requestMethod, accept, contentType);
	}

	@Test
	public void testIndexActionForRecordType() {
		Action action = Action.INDEX;
		actionsConverterData.actions.add(action);
		actionsConverterData.recordType = "recordType";

		String lowerCaseAction = action.name().toLowerCase();
		String url = baseUrl + "workOrder/";
		String requestMethod = POST;
		String accept = APPLICATION_VND_CORA_RECORD_JSON;
		String contentType = APPLICATION_VND_CORA_RECORD_JSON;

		actionsConverter.toJsonObjectBuilder(actionsConverterData);

		builderFactory.MCR.assertNumberOfCallsToMethod("createObjectBuilder", 2);
		assertMainAndInternalLinkBuilder(lowerCaseAction, url, requestMethod, accept, contentType);

		assertWorkOrderBuilderCreatedUsingConverterFactoryWithDataGroupAddedToInternalLinkBuilderAsBody();

		assertCreatedWorkOrderBodyDataStructureIsCorrect();
	}
}
