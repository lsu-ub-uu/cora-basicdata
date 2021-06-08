/*
 * Copyright 2015, 2019 Uppsala University Library
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

public final class DataRecordToJsonConverter {

	private JsonBuilderFactory jsonBuilderFactory;
	private DataRecord dataRecord;
	private JsonObjectBuilder recordJsonObjectBuilder;

	public static DataRecordToJsonConverter usingJsonFactoryForDataRecord(
			JsonBuilderFactory jsonFactory, DataRecord dataRecord) {
		return new DataRecordToJsonConverter(jsonFactory, dataRecord);
	}

	private DataRecordToJsonConverter(JsonBuilderFactory jsonFactory, DataRecord dataRecord) {
		this.jsonBuilderFactory = jsonFactory;
		this.dataRecord = dataRecord;
		recordJsonObjectBuilder = jsonFactory.createObjectBuilder();
	}

	public String toJson() {
		return toJsonObjectBuilder().toJsonFormattedString();
	}

	JsonObjectBuilder toJsonObjectBuilder() {
		convertMainDataGroup();
		possiblyConvertPermissions();
		return createTopLevelJsonObjectWithRecordAsChild();
	}

	private void convertMainDataGroup() {
		DataToJsonConverterFactory dataToJsonConverterFactory = new DataToJsonConverterFactoryImp();
		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForDataElement(jsonBuilderFactory, dataRecord.getDataGroup());
		JsonObjectBuilder jsonDataGroupObjectBuilder = dataToJsonConverter.toJsonObjectBuilder();
		recordJsonObjectBuilder.addKeyJsonObjectBuilder("data", jsonDataGroupObjectBuilder);
	}

	private void possiblyConvertPermissions() {
		if (recordHasReadPermissions() || recordHasWritePermissions()) {
			convertPermissions();
		}
	}

	private boolean recordHasReadPermissions() {
		return !dataRecord.getReadPermissions().isEmpty();
	}

	private boolean recordHasWritePermissions() {
		return !dataRecord.getWritePermissions().isEmpty();
	}

	private void convertPermissions() {
		JsonObjectBuilder permissionsJsonObjectBuilder = jsonBuilderFactory.createObjectBuilder();
		possiblyAddReadPermissions(permissionsJsonObjectBuilder);
		possiblyAddWritePermissions(permissionsJsonObjectBuilder);
		recordJsonObjectBuilder.addKeyJsonObjectBuilder("permissions",
				permissionsJsonObjectBuilder);
	}

	private void possiblyAddReadPermissions(JsonObjectBuilder permissionsJsonObjectBuilder) {
		if (recordHasReadPermissions()) {
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
		if (recordHasWritePermissions()) {
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

}
