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

import java.util.List;

import se.uu.ub.cora.basicdata.data.CoraDataAtomic;
import se.uu.ub.cora.basicdata.data.CoraDataGroup;
import se.uu.ub.cora.data.Action;
import se.uu.ub.cora.data.converter.DataToJsonConverter;
import se.uu.ub.cora.data.converter.DataToJsonConverterFactory;
import se.uu.ub.cora.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.json.builder.JsonObjectBuilder;

public class RecordActionsToJsonConverterImp implements RecordActionsToJsonConverter {

	private static final String APPLICATION_VND_UUB_RECORD_LIST_JSON = "application/vnd.uub.recordList+json";
	private static final String APPLICATION_VND_UUB_RECORD_JSON = "application/vnd.uub.record+json";
	private DataToJsonConverterFactory converterFactory;
	private JsonBuilderFactory builderFactory;
	private JsonObjectBuilder mainBuilder;
	private String baseUrl;
	private List<Action> actions;
	private String recordType;
	private String recordId;
	private String currentLowerCaseAction;
	private JsonObjectBuilder currentLinkBuilder;
	private String currentRequestMethod;
	private String currentUrl;
	private String currentAccept;
	private String searchIdOrRecordId;

	public RecordActionsToJsonConverterImp(DataToJsonConverterFactory converterFactory,
			JsonBuilderFactory builderFactory, String baseUrl) {
		this.converterFactory = converterFactory;
		this.builderFactory = builderFactory;
		this.baseUrl = baseUrl;
		mainBuilder = builderFactory.createObjectBuilder();
	}

	@Override
	public JsonObjectBuilder toJsonObjectBuilder(List<Action> actions, String recordType,
			String recordId) {
		this.actions = actions;
		this.recordType = recordType;
		this.recordId = recordId;
		// if (!actions.isEmpty()) {
		searchIdOrRecordId = recordId;
		createJsonForActions();
		// }
		return mainBuilder;
	}

	private void createJsonForActions() {
		for (Action action : actions) {
			setStandardForAction(action);

			if (Action.READ.equals(action)) {
				addStandardParametersToCurrentLinkBuilder();
				currentLinkBuilder.addKeyString("accept", currentAccept);
			}
			if (Action.UPDATE.equals(action)) {
				currentRequestMethod = "POST";
				addStandardParametersToCurrentLinkBuilder();
				currentLinkBuilder.addKeyString("accept", currentAccept);
				currentLinkBuilder.addKeyString("contentType", APPLICATION_VND_UUB_RECORD_JSON);
			}
			if (Action.READ_INCOMING_LINKS.equals(action)) {
				currentUrl = currentUrl + "/incomingLinks";
				addStandardParametersToCurrentLinkBuilder();
				currentLinkBuilder.addKeyString("accept", APPLICATION_VND_UUB_RECORD_LIST_JSON);
			}
			if (Action.DELETE.equals(action)) {
				currentRequestMethod = "DELETE";
				addStandardParametersToCurrentLinkBuilder();
			}
			if (Action.INDEX.equals(action)) {
				currentRequestMethod = "POST";
				currentUrl = baseUrl + "workOrder/";
				addStandardParametersToCurrentLinkBuilder();
				currentLinkBuilder.addKeyString("accept", currentAccept);
				currentLinkBuilder.addKeyString("contentType", APPLICATION_VND_UUB_RECORD_JSON);
				createBody();
			}
			// recordType=binary and children to binary
			if (Action.UPLOAD.equals(action)) {
				currentRequestMethod = "POST";
				currentUrl = baseUrl + recordType + "/" + recordId + "/master";
				addStandardParametersToCurrentLinkBuilder();
				currentLinkBuilder.addKeyString("contentType", "multipart/form-data");
			}
			// recordtype=search / recordType (with searchIdOrRecordId)
			if (Action.SEARCH.equals(action)) {
				currentUrl = baseUrl + "searchResult/" + searchIdOrRecordId;
				addStandardParametersToCurrentLinkBuilder();
				currentLinkBuilder.addKeyString("accept", APPLICATION_VND_UUB_RECORD_LIST_JSON);
			}
			// createActionWhenRecordIsARecordType
			// for recordtype = recordType
			if (Action.CREATE.equals(action)) {
				currentRequestMethod = "POST";
				String urlForRecordTypeActions = baseUrl + recordId + "/";
				currentUrl = urlForRecordTypeActions;
				addStandardParametersToCurrentLinkBuilder();
				currentLinkBuilder.addKeyString("accept", currentAccept);
				currentLinkBuilder.addKeyString("contentType", APPLICATION_VND_UUB_RECORD_JSON);
			}
			if (Action.LIST.equals(action)) {
				currentRequestMethod = "GET";
				String urlForRecordTypeActions = baseUrl + recordId + "/";
				currentUrl = urlForRecordTypeActions;
				currentAccept = APPLICATION_VND_UUB_RECORD_LIST_JSON;
				addStandardParametersToCurrentLinkBuilder();
				currentLinkBuilder.addKeyString("accept", currentAccept);
			}
			if (Action.BATCH_INDEX.equals(action)) {
				currentRequestMethod = "POST";
				currentUrl = baseUrl + "index/" + recordId + "/";
				addStandardParametersToCurrentLinkBuilder();
				currentLinkBuilder.addKeyString("accept", currentAccept);
			}
			if (Action.VALIDATE.equals(action)) {
				currentRequestMethod = "POST";
				currentUrl = baseUrl + "workOrder/";
				addStandardParametersToCurrentLinkBuilder();
				currentLinkBuilder.addKeyString("accept", currentAccept);
				currentLinkBuilder.addKeyString("contentType",
						"application/vnd.uub.workorder+json");
			}
		}
	}

	private void setStandardForAction(Action action) {
		currentLowerCaseAction = action.name().toLowerCase();
		currentLinkBuilder = builderFactory.createObjectBuilder();
		currentRequestMethod = "GET";
		String urlForActionsOnThisRecord = baseUrl + recordType + "/" + recordId;
		currentUrl = urlForActionsOnThisRecord;
		currentAccept = APPLICATION_VND_UUB_RECORD_JSON;
		mainBuilder.addKeyJsonObjectBuilder(currentLowerCaseAction, currentLinkBuilder);
	}

	private void createBody() {
		JsonObjectBuilder workOrderBuilder = convertBody();
		currentLinkBuilder.addKeyJsonObjectBuilder("body", workOrderBuilder);
	}

	private JsonObjectBuilder convertBody() {
		CoraDataGroup workOrder = createWorkOrderDataGroup();
		DataToJsonConverter workOrderConverter = converterFactory.factorUsingConvertible(workOrder);
		return workOrderConverter.toJsonObjectBuilder();
	}

	private CoraDataGroup createWorkOrderDataGroup() {
		CoraDataGroup workOrder = CoraDataGroup.withNameInData("workOrder");
		CoraDataGroup recordTypeGroup = CoraDataGroup.withNameInData("recordType");
		recordTypeGroup
				.addChild(CoraDataAtomic.withNameInDataAndValue("linkedRecordType", "recordType"));
		recordTypeGroup
				.addChild(CoraDataAtomic.withNameInDataAndValue("linkedRecordId", recordType));
		workOrder.addChild(recordTypeGroup);
		workOrder.addChild(CoraDataAtomic.withNameInDataAndValue("recordId", recordId));
		workOrder.addChild(CoraDataAtomic.withNameInDataAndValue("type", "index"));
		return workOrder;
	}

	private void addStandardParametersToCurrentLinkBuilder() {
		currentLinkBuilder.addKeyString("rel", currentLowerCaseAction);
		currentLinkBuilder.addKeyString("url", currentUrl);
		currentLinkBuilder.addKeyString("requestMethod", currentRequestMethod);
	}

	@Override
	public JsonObjectBuilder toJsonObjectBuilderForRecordTypeWithSearchId(List<Action> actions,
			String recordId, String searchRecordId) {
		this.actions = actions;
		this.recordType = "recordType";
		this.recordId = recordId;
		searchIdOrRecordId = searchRecordId;
		createJsonForActions();
		return mainBuilder;
	}

}
