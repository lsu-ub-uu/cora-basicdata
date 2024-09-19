/*
 * Copyright 2015, 2019, 2021, 2022, 2024 Uppsala University Library
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

import java.util.Optional;
import java.util.Set;

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataRecord;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.data.converter.DataToJsonConverter;
import se.uu.ub.cora.data.converter.DataToJsonConverterFactory;
import se.uu.ub.cora.data.converter.ExternalUrls;
import se.uu.ub.cora.json.builder.JsonArrayBuilder;
import se.uu.ub.cora.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.json.builder.JsonObjectBuilder;

public class DataRecordToJsonConverter implements DataToJsonConverter {

	DataToJsonConverterFactory converterFactory;
	JsonBuilderFactory builderFactory;
	RecordActionsToJsonConverter actionsConverter;
	DataRecord dataRecord;
	private JsonObjectBuilder recordJsonObjectBuilder;
	private Optional<ExternalUrls> externalUrls;

	public static DataRecordToJsonConverter usingConverterFactoryAndActionsConverterAndBuilderFactoryAndExternalUrls(
			DataRecord dataRecord, DataToJsonConverterFactory converterFactory,
			RecordActionsToJsonConverter actionsConverter, JsonBuilderFactory builderFactory,
			Optional<ExternalUrls> externalUrls) {
		return new DataRecordToJsonConverter(converterFactory, actionsConverter, builderFactory,
				externalUrls, dataRecord);
	}

	DataRecordToJsonConverter(DataToJsonConverterFactory converterFactory,
			RecordActionsToJsonConverter actionsConverter, JsonBuilderFactory builderFactory,
			Optional<ExternalUrls> externalUrls, DataRecord dataRecord) {
		this.converterFactory = converterFactory;
		this.actionsConverter = actionsConverter;
		this.builderFactory = builderFactory;
		this.externalUrls = externalUrls;
		this.dataRecord = dataRecord;
		recordJsonObjectBuilder = builderFactory.createObjectBuilder();
	}

	@Override
	public String toJson() {
		return toJsonObjectBuilder().toJsonFormattedPrettyString();
	}

	@Override
	public JsonObjectBuilder toJsonObjectBuilder() {
		convertMainDataGroup();
		possiblyConvertPermissions();
		possiblyConvertActions();
		possiblyConvertProtocols();
		return createTopLevelJsonObjectWithRecordAsChild();
	}

	private void convertMainDataGroup() {
		DataToJsonConverter dataToJsonConverter;
		dataToJsonConverter = createConverterForMainDataGroup();

		JsonObjectBuilder jsonDataGroupObjectBuilder = dataToJsonConverter.toJsonObjectBuilder();
		recordJsonObjectBuilder.addKeyJsonObjectBuilder("data", jsonDataGroupObjectBuilder);
	}

	private DataToJsonConverter createConverterForMainDataGroup() {
		if (actionLinksShouldBeCreated()) {
			String recordUrl = externalUrls.get().getBaseUrl() + dataRecord.getType() + "/"
					+ dataRecord.getId();
			return converterFactory.factorUsingBaseUrlAndRecordUrlAndConvertible(
					externalUrls.get().getBaseUrl(), recordUrl, dataRecord.getDataRecordGroup());
		}
		return converterFactory.factorUsingConvertible(dataRecord.getDataRecordGroup());

	}

	private boolean actionLinksShouldBeCreated() {
		return externalUrls.isPresent();
	}

	private void possiblyConvertActions() {
		if (dataRecord.hasActions()) {
			ActionsConverterData actionsConverterData = collectDataForActions();
			possiblySetSearchIdFromRecordType(actionsConverterData);
			JsonObjectBuilder jsonObjectBuilder = actionsConverter
					.toJsonObjectBuilder(actionsConverterData);
			recordJsonObjectBuilder.addKeyJsonObjectBuilder("actionLinks", jsonObjectBuilder);
		}
	}

	private ActionsConverterData collectDataForActions() {
		ActionsConverterData actionsConverterData = new ActionsConverterData();
		actionsConverterData.recordType = dataRecord.getType();
		actionsConverterData.recordId = dataRecord.getId();
		actionsConverterData.actions.addAll(dataRecord.getActions());
		return actionsConverterData;
	}

	private void possiblySetSearchIdFromRecordType(ActionsConverterData actionsConverterData) {
		if (thisRecordIsRecordType()) {
			DataRecordGroup dataRecordGroup = dataRecord.getDataRecordGroup();
			possiblySetSearchRecordIdIfDefinedInDataGroup(actionsConverterData, dataRecordGroup);
		}
	}

	private boolean thisRecordIsRecordType() {
		return "recordType".equals(dataRecord.getType());
	}

	private void possiblySetSearchRecordIdIfDefinedInDataGroup(
			ActionsConverterData actionsConverterData, DataRecordGroup dataRecordGroup) {
		if (dataRecordGroup.containsChildWithNameInData("search")) {
			DataGroup searchGroup = dataRecordGroup.getFirstGroupWithNameInData("search");
			actionsConverterData.searchRecordId = searchGroup
					.getFirstAtomicValueWithNameInData("linkedRecordId");
		}
	}

	private void possiblyConvertPermissions() {
		if (recordHasPermissions()) {
			convertPermissions();
		}
	}

	private boolean recordHasPermissions() {
		return dataRecord.hasReadPermissions() || dataRecord.hasWritePermissions();
	}

	private void convertPermissions() {
		JsonObjectBuilder permissionsJsonObjectBuilder = builderFactory.createObjectBuilder();
		possiblyAddReadPermissions(permissionsJsonObjectBuilder);
		possiblyAddWritePermissions(permissionsJsonObjectBuilder);
		recordJsonObjectBuilder.addKeyJsonObjectBuilder("permissions",
				permissionsJsonObjectBuilder);
	}

	private void possiblyAddReadPermissions(JsonObjectBuilder permissionsJsonObjectBuilder) {
		if (dataRecord.hasReadPermissions()) {
			addReadPermissions(permissionsJsonObjectBuilder);
		}
	}

	private void addReadPermissions(JsonObjectBuilder permissionsJsonObjectBuilder) {
		JsonArrayBuilder readPermissionsArray = createJsonForPermissions(
				dataRecord.getReadPermissions());
		permissionsJsonObjectBuilder.addKeyJsonArrayBuilder("read", readPermissionsArray);
	}

	private JsonArrayBuilder createJsonForPermissions(Set<String> permissions) {
		JsonArrayBuilder permissionsBuilder = builderFactory.createArrayBuilder();
		for (String permission : permissions) {
			permissionsBuilder.addString(permission);
		}
		return permissionsBuilder;
	}

	private void possiblyAddWritePermissions(JsonObjectBuilder permissionsJsonObjectBuilder) {
		if (dataRecord.hasWritePermissions()) {
			addWritePermissions(permissionsJsonObjectBuilder);
		}
	}

	private void addWritePermissions(JsonObjectBuilder permissionsJsonObjectBuilder) {
		JsonArrayBuilder writePermissionsArray = createJsonForPermissions(
				dataRecord.getWritePermissions());
		permissionsJsonObjectBuilder.addKeyJsonArrayBuilder("write", writePermissionsArray);
	}

	private JsonObjectBuilder createTopLevelJsonObjectWithRecordAsChild() {
		JsonObjectBuilder rootWrappingJsonObjectBuilder = builderFactory.createObjectBuilder();
		rootWrappingJsonObjectBuilder.addKeyJsonObjectBuilder("record", recordJsonObjectBuilder);
		return rootWrappingJsonObjectBuilder;
	}

	private void possiblyConvertProtocols() {
		possiblyAddIiifProtocol();
	}

	private void possiblyAddIiifProtocol() {
		if (!dataRecord.getProtocols().isEmpty()) {
			for (String protocol : dataRecord.getProtocols()) {
				addIiifProtocol(protocol);
			}
		}
	}

	private void addIiifProtocol(String protocol) {
		if ("iiif".equals(protocol)) {
			JsonObjectBuilder iifProtocol = createIIIF();
			recordJsonObjectBuilder.addKeyJsonObjectBuilder("otherProtocols", iifProtocol);
		}
	}

	private JsonObjectBuilder createIIIF() {
		JsonObjectBuilder iiifBody = createIIIBody();
		JsonObjectBuilder iifProtocol = builderFactory.createObjectBuilder();
		iifProtocol.addKeyJsonObjectBuilder("iiif", iiifBody);
		return iifProtocol;
	}

	private JsonObjectBuilder createIIIBody() {
		JsonObjectBuilder iiidBody = builderFactory.createObjectBuilder();
		iiidBody.addKeyString("server", externalUrls.get().getIfffUrl());
		iiidBody.addKeyString("identifier", dataRecord.getId());
		return iiidBody;
	}

	@Override
	public String toJsonCompactFormat() {
		return toJsonObjectBuilder().toJsonFormattedString();
	}

	public Optional<ExternalUrls> onlyForTestGetOptionalExternalUrls() {
		return externalUrls;
	}

}
