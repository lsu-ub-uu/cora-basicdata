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

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.Action;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.json.builder.JsonObjectBuilder;

public class RecordActionsToJsonConverterTest {

	private static final String GET = "GET";
	private static final String POST = "POST";
	private RecordActionsToJsonConverter actionsConverter;
	private List<Action> actions;
	private JsonBuilderFactorySpy builderFactory;
	private String baseUrl = "some/base/url/";
	private String recordType = "someRecordType";
	private String recordId = "someRecordId";
	private static final String APPLICATION_VND_UUB_RECORD_LIST_JSON = "application/vnd.uub.recordList+json";
	private static final String APPLICATION_VND_UUB_RECORD_JSON = "application/vnd.uub.record+json";
	private DataToJsonConverterFactorySpy converterFactory;

	@BeforeMethod
	private void beforeMethod() {
		builderFactory = new JsonBuilderFactorySpy();
		actions = new ArrayList<>();

		converterFactory = new DataToJsonConverterFactorySpy();
		actionsConverter = new RecordActionsToJsonConverterImp(converterFactory, builderFactory,
				baseUrl);

	}

	@Test
	public void testToJsonObjectBuilder() throws Exception {
		JsonObjectBuilder objectBuilder = actionsConverter.toJsonObjectBuilder(actions, recordType,
				recordId);

		builderFactory.MCR.assertMethodWasCalled("createObjectBuilder");
		builderFactory.MCR.assertReturn("createObjectBuilder", 0, objectBuilder);
		builderFactory.MCR.assertNumberOfCallsToMethod("createObjectBuilder", 1);
	}

	@Test
	public void testToJsonObjectBuilderForRecordType() throws Exception {
		String searchRecordId = "someSearchRecordId";
		JsonObjectBuilder objectBuilder = actionsConverter
				.toJsonObjectBuilderForRecordTypeWithSearchId(actions, recordId, searchRecordId);

		builderFactory.MCR.assertMethodWasCalled("createObjectBuilder");
		builderFactory.MCR.assertReturn("createObjectBuilder", 0, objectBuilder);
		builderFactory.MCR.assertNumberOfCallsToMethod("createObjectBuilder", 1);
	}

	@Test
	public void testReadAction() throws Exception {
		Action action = Action.READ;
		actions.add(action);
		String lowerCaseAction = action.name().toLowerCase();
		String url = baseUrl + recordType + "/" + recordId;

		String requestMethod = GET;
		String accept = APPLICATION_VND_UUB_RECORD_JSON;
		String contentType = null;

		actionsConverter.toJsonObjectBuilder(actions, recordType, recordId);

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
	public void testUpdateAction() throws Exception {
		Action action = Action.UPDATE;
		actions.add(action);
		String lowerCaseAction = action.name().toLowerCase();
		String url = baseUrl + recordType + "/" + recordId;

		String requestMethod = POST;
		String accept = APPLICATION_VND_UUB_RECORD_JSON;
		String contentType = APPLICATION_VND_UUB_RECORD_JSON;

		actionsConverter.toJsonObjectBuilder(actions, recordType, recordId);

		builderFactory.MCR.assertNumberOfCallsToMethod("createObjectBuilder", 2);
		assertMainAndInternalLinkBuilder(lowerCaseAction, url, requestMethod, accept, contentType);
	}

	@Test
	public void testReadIncommingLinksAction() throws Exception {
		Action action = Action.READ_INCOMING_LINKS;
		actions.add(action);
		String lowerCaseAction = action.name().toLowerCase();
		String url = baseUrl + recordType + "/" + recordId + "/incomingLinks";

		String requestMethod = GET;
		String accept = APPLICATION_VND_UUB_RECORD_LIST_JSON;
		String contentType = null;

		actionsConverter.toJsonObjectBuilder(actions, recordType, recordId);

		builderFactory.MCR.assertNumberOfCallsToMethod("createObjectBuilder", 2);
		assertMainAndInternalLinkBuilder(lowerCaseAction, url, requestMethod, accept, contentType);
	}

	@Test
	public void testDeleteAction() throws Exception {
		Action action = Action.DELETE;
		actions.add(action);
		String lowerCaseAction = action.name().toLowerCase();
		String url = baseUrl + recordType + "/" + recordId;

		String requestMethod = "DELETE";
		String accept = null;
		String contentType = null;

		actionsConverter.toJsonObjectBuilder(actions, recordType, recordId);

		builderFactory.MCR.assertNumberOfCallsToMethod("createObjectBuilder", 2);
		assertMainAndInternalLinkBuilder(lowerCaseAction, url, requestMethod, accept, contentType);
	}

	@Test
	public void testCreateAction() throws Exception {
		Action action = Action.CREATE;
		actions.add(action);

		String lowerCaseAction = action.name().toLowerCase();
		String url = baseUrl + recordId + "/";
		String requestMethod = POST;
		String accept = APPLICATION_VND_UUB_RECORD_JSON;
		String contentType = APPLICATION_VND_UUB_RECORD_JSON;

		actionsConverter.toJsonObjectBuilder(actions, recordType, recordId);

		builderFactory.MCR.assertNumberOfCallsToMethod("createObjectBuilder", 2);
		assertMainAndInternalLinkBuilder(lowerCaseAction, url, requestMethod, accept, contentType);
	}

	@Test
	public void testUploadAction() throws Exception {
		Action action = Action.UPLOAD;
		actions.add(action);

		String lowerCaseAction = action.name().toLowerCase();
		String url = baseUrl + recordType + "/" + recordId + "/master";
		String requestMethod = POST;
		String accept = null;
		String contentType = "multipart/form-data";

		actionsConverter.toJsonObjectBuilder(actions, recordType, recordId);

		builderFactory.MCR.assertNumberOfCallsToMethod("createObjectBuilder", 2);
		assertMainAndInternalLinkBuilder(lowerCaseAction, url, requestMethod, accept, contentType);
	}

	@Test
	public void testSearchActionForSearchRecordType() throws Exception {
		Action action = Action.SEARCH;
		actions.add(action);

		String lowerCaseAction = action.name().toLowerCase();
		String url = baseUrl + "searchResult/" + recordId;
		String requestMethod = GET;
		String accept = APPLICATION_VND_UUB_RECORD_LIST_JSON;
		String contentType = null;

		actionsConverter.toJsonObjectBuilder(actions, recordType, recordId);

		builderFactory.MCR.assertNumberOfCallsToMethod("createObjectBuilder", 2);
		assertMainAndInternalLinkBuilder(lowerCaseAction, url, requestMethod, accept, contentType);
	}

	@Test
	public void testSearchActionForRecordTypeTypeWithSearchId() throws Exception {
		Action action = Action.SEARCH;
		actions.add(action);

		String lowerCaseAction = action.name().toLowerCase();
		String searchRecordId = "someSearchRecordId";
		String url = baseUrl + "searchResult/" + searchRecordId;
		String requestMethod = GET;
		String accept = APPLICATION_VND_UUB_RECORD_LIST_JSON;
		String contentType = null;

		actionsConverter.toJsonObjectBuilderForRecordTypeWithSearchId(actions, recordId,
				searchRecordId);

		builderFactory.MCR.assertNumberOfCallsToMethod("createObjectBuilder", 2);
		assertMainAndInternalLinkBuilder(lowerCaseAction, url, requestMethod, accept, contentType);
	}

	@Test
	public void testIndexAction() throws Exception {
		Action action = Action.INDEX;
		actions.add(action);

		String lowerCaseAction = action.name().toLowerCase();
		String url = baseUrl + "workOrder/";
		String requestMethod = POST;
		String accept = APPLICATION_VND_UUB_RECORD_JSON;
		String contentType = APPLICATION_VND_UUB_RECORD_JSON;

		actionsConverter.toJsonObjectBuilder(actions, recordType, recordId);

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
				.getValueForMethodNameAndCallNumberAndParameterName("factorUsingConvertible", 0,
						"convertible");
		assertEquals(workOrder.getNameInData(), "workOrder");
		DataGroup recordTypeGroup = workOrder.getFirstGroupWithNameInData("recordType");
		assertEquals(recordTypeGroup.getFirstAtomicValueWithNameInData("linkedRecordType"),
				"recordType");
		assertEquals(recordTypeGroup.getFirstAtomicValueWithNameInData("linkedRecordId"),
				recordType);
		assertEquals(workOrder.getFirstAtomicValueWithNameInData("recordId"), recordId);
		assertEquals(workOrder.getFirstAtomicValueWithNameInData("type"), "index");
	}

	@Test
	public void testValidateAction() throws Exception {
		Action action = Action.VALIDATE;
		actions.add(action);

		String lowerCaseAction = action.name().toLowerCase();
		String url = baseUrl + "workOrder/";
		String requestMethod = POST;
		String accept = APPLICATION_VND_UUB_RECORD_JSON;
		String contentType = "application/vnd.uub.workorder+json";

		actionsConverter.toJsonObjectBuilder(actions, recordType, recordId);

		builderFactory.MCR.assertNumberOfCallsToMethod("createObjectBuilder", 2);
		assertMainAndInternalLinkBuilder(lowerCaseAction, url, requestMethod, accept, contentType);
	}

	@Test
	public void testBatchIndexAction() throws Exception {
		Action action = Action.BATCH_INDEX;
		actions.add(action);

		String lowerCaseAction = action.name().toLowerCase();
		String url = baseUrl + "index/" + recordId + "/";
		String requestMethod = POST;
		String accept = APPLICATION_VND_UUB_RECORD_JSON;
		String contentType = null;

		actionsConverter.toJsonObjectBuilder(actions, recordType, recordId);

		builderFactory.MCR.assertNumberOfCallsToMethod("createObjectBuilder", 2);
		assertMainAndInternalLinkBuilder(lowerCaseAction, url, requestMethod, accept, contentType);
	}

	@Test
	public void testListAction() throws Exception {
		Action action = Action.LIST;
		actions.add(action);

		String lowerCaseAction = action.name().toLowerCase();
		String url = baseUrl + recordId + "/";
		String requestMethod = GET;
		String accept = APPLICATION_VND_UUB_RECORD_LIST_JSON;
		String contentType = null;

		actionsConverter.toJsonObjectBuilder(actions, recordType, recordId);

		builderFactory.MCR.assertNumberOfCallsToMethod("createObjectBuilder", 2);
		assertMainAndInternalLinkBuilder(lowerCaseAction, url, requestMethod, accept, contentType);
	}

}
