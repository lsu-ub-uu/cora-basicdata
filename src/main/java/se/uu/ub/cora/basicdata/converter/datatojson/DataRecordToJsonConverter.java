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

import java.util.Set;

import se.uu.ub.cora.data.DataRecord;
import se.uu.ub.cora.data.converter.DataToJsonConverter;
import se.uu.ub.cora.data.converter.DataToJsonConverterFactory;
import se.uu.ub.cora.json.builder.JsonArrayBuilder;
import se.uu.ub.cora.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.json.builder.JsonObjectBuilder;

public class DataRecordToJsonConverter implements DataToJsonConverter {

	private JsonBuilderFactory jsonBuilderFactory;
	private DataRecord dataRecord;
	private JsonObjectBuilder recordJsonObjectBuilder;
	private DataToJsonConverterFactory converterFactory;
	private String baseUrl;

	public static DataRecordToJsonConverter usingConverterFactoryAndBuilderFactoryAndDataRecord(
			DataToJsonConverterFactory converterFactory, JsonBuilderFactory jsonFactory,
			String baseUrl, DataRecord dataRecord) {
		return new DataRecordToJsonConverter(converterFactory, jsonFactory, baseUrl, dataRecord);
	}

	DataRecordToJsonConverter(DataToJsonConverterFactory converterFactory,
			JsonBuilderFactory builderFactory, String baseUrl, DataRecord dataRecord) {
		this.converterFactory = converterFactory;
		this.jsonBuilderFactory = builderFactory;
		this.baseUrl = baseUrl;
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
		// TODO: convertActions
		return createTopLevelJsonObjectWithRecordAsChild();
	}

	// private void convertActionLinks() {
	// if (recordHasActionLinks()) {
	// addActionLinksToRecord();
	// }
	// }

	// private boolean recordHasActionLinks() {
	// return !dataRecord.getActions().isEmpty();
	// }
	//
	// private void addActionLinksToRecord() {
	// List<Action> actionLinks = dataRecord.getActions();
	// ActionLinksToJsonConverter actionLinkConverter = new ActionLinksToJsonConverter(
	// jsonBuilderFactory, actionLinks);
	// JsonObjectBuilder actionLinksObject = actionLinkConverter.toJsonObjectBuilder();
	// recordJsonObjectBuilder.addKeyJsonObjectBuilder("actionLinks", actionLinksObject);
	// }

	private void convertMainDataGroup() {
		DataToJsonConverter dataToJsonConverter;
		dataToJsonConverter = createConverterForMainDataGroup();

		JsonObjectBuilder jsonDataGroupObjectBuilder = dataToJsonConverter.toJsonObjectBuilder();
		recordJsonObjectBuilder.addKeyJsonObjectBuilder("data", jsonDataGroupObjectBuilder);
	}

	private DataToJsonConverter createConverterForMainDataGroup() {
		if (actionLinksShouldBeCreated()) {
			String recordUrl = baseUrl + dataRecord.getType() + "/" + dataRecord.getId();
			return converterFactory.factorUsingBaseUrlAndRecordUrlAndConvertible(baseUrl, recordUrl,
					dataRecord.getDataGroup());
		}
		return converterFactory.factorUsingConvertible(dataRecord.getDataGroup());

	}

	private boolean actionLinksShouldBeCreated() {
		return baseUrl != null;
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
		JsonObjectBuilder permissionsJsonObjectBuilder = jsonBuilderFactory.createObjectBuilder();
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
		JsonArrayBuilder permissionsBuilder = jsonBuilderFactory.createArrayBuilder();
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
		JsonObjectBuilder rootWrappingJsonObjectBuilder = jsonBuilderFactory.createObjectBuilder();
		rootWrappingJsonObjectBuilder.addKeyJsonObjectBuilder("record", recordJsonObjectBuilder);
		return rootWrappingJsonObjectBuilder;
	}

	@Override
	public String toJsonCompactFormat() {
		return toJsonObjectBuilder().toJsonFormattedString();
	}

}
