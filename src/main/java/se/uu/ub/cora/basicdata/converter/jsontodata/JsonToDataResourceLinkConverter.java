/*
 * Copyright 2019, 2023, 2025 Uppsala University Library
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
package se.uu.ub.cora.basicdata.converter.jsontodata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.uu.ub.cora.basicdata.data.CoraDataResourceLink;
import se.uu.ub.cora.data.Convertible;
import se.uu.ub.cora.data.converter.JsonToDataConverter;
import se.uu.ub.cora.json.parser.JsonArray;
import se.uu.ub.cora.json.parser.JsonObject;
import se.uu.ub.cora.json.parser.JsonParseException;
import se.uu.ub.cora.json.parser.JsonValue;

public class JsonToDataResourceLinkConverter implements JsonToDataConverter {

	private static final String CHILDREN = "children";
	private static final String LINKED_RECORD_TYPE = "linkedRecordType";
	private static final String LINKED_RECORD_ID = "linkedRecordId";
	private static final String VALUE = "value";
	private static final String NAME = "name";
	private static final String REPEAT_ID = "repeatId";
	private static final String MIME_TYPE = "mimeType";
	private static final int MAX_JSON_KEYS_WITHOUT_REPEAT_ID = 2;
	private static final int MAX_JSON_KEYS_WITH_REPEAT_ID = 3;
	private static final String PARSING_ERROR_MSG = "Error parsing jsonObject: ResourceLink must "
			+ "contain name,children[linkedRecordType,linkedRecordId,mimeType] and repeatId.";
	private JsonObject resourceLinkAsJson;

	private JsonToDataResourceLinkConverter(JsonObject resourceLinkAsJson) {
		this.resourceLinkAsJson = resourceLinkAsJson;
	}

	@Override
	public Convertible toInstance() {
		Map<String, String> fields = validateJsonAndCollectFields();
		CoraDataResourceLink resourceLink = createResourceLink(fields);
		// possiblySetRepeatId(resourceLink);
		return resourceLink;
	}

	private void validateJsonStructure() {
		if (validateJsonKeys()) {
			throw new JsonParseException(PARSING_ERROR_MSG);
		}
	}

	private boolean validateJsonKeys() {
		return exceedeFieldsRepeatIdExists() || exceedeFieldsRepeatIdNotExists()
				|| nameAndMimeTypeNotExists();
	}

	private boolean exceedeFieldsRepeatIdNotExists() {
		return !repeatIdExists()
				&& resourceLinkAsJson.keySet().size() > MAX_JSON_KEYS_WITHOUT_REPEAT_ID;
	}

	private boolean exceedeFieldsRepeatIdExists() {
		return repeatIdExists()
				&& resourceLinkAsJson.keySet().size() > MAX_JSON_KEYS_WITH_REPEAT_ID;
	}

	private Map<String, String> validateJsonAndCollectFields() {
		validateJsonStructure();
		Map<String, String> fields = collectResourceLinksFields();
		validateChildren(fields);
		return fields;
	}

	private Map<String, String> collectResourceLinksFields() {
		Map<String, String> fields = new HashMap<>();
		fields.put(NAME, getValueAsStringFromJsonObject(resourceLinkAsJson, NAME));
		if (repeatIdExists()) {
			fields.put(REPEAT_ID, getValueAsStringFromJsonObject(resourceLinkAsJson, REPEAT_ID));
		}
		fields.putAll(readChildren());
		return fields;
	}

	private Map<String, String> readChildren() {
		Map<String, String> childrenFields = new HashMap<>();
		JsonArray children = resourceLinkAsJson.getValueAsJsonArray(CHILDREN);
		for (JsonValue jsonValue : children) {
			JsonObject childrenObject = (JsonObject) jsonValue;
			String name = getValueAsStringFromJsonObject(childrenObject, NAME);
			String value = getValueAsStringFromJsonObject(childrenObject, VALUE);
			childrenFields.put(name, value);
		}
		return childrenFields;
	}

	private CoraDataResourceLink createResourceLink(Map<String, String> fields) {
		var resourceLink = CoraDataResourceLink.withNameInDataAndTypeAndIdAndMimeType(
				fields.get(NAME), fields.get(LINKED_RECORD_TYPE), fields.get(LINKED_RECORD_ID),
				fields.get(MIME_TYPE));
		if (fields.containsKey(REPEAT_ID)) {
			resourceLink.setRepeatId(fields.get(REPEAT_ID));
		}
		return resourceLink;
	}

	// TODO. Fix that shitty code below
	private void validateChildren(Map<String, String> fields) {
		if (fields.size() < 4 || fields.size() > 5) {
			throw new JsonParseException(PARSING_ERROR_MSG);
		}
		if (fields.size() == 4) {
			for (String key : List.of(NAME, LINKED_RECORD_TYPE, LINKED_RECORD_ID, MIME_TYPE)) {
				if (!fields.containsKey(key)) {
					throw new JsonParseException(PARSING_ERROR_MSG);
				}
			}
		}
		if (fields.size() == 5) {
			for (String key : List.of(NAME, LINKED_RECORD_TYPE, LINKED_RECORD_ID, MIME_TYPE,
					REPEAT_ID)) {
				if (!fields.containsKey(key)) {
					throw new JsonParseException(PARSING_ERROR_MSG);
				}
			}
		}
	}

	private boolean nameAndMimeTypeNotExists() {
		return !resourceLinkAsJson.containsKey(NAME) || !resourceLinkAsJson.containsKey(CHILDREN);
	}

	private boolean repeatIdExists() {
		return resourceLinkAsJson.containsKey(REPEAT_ID);
	}

	private String getValueAsStringFromJsonObject(JsonObject jsonObject, String key) {
		return jsonObject.getValueAsJsonString(key).getStringValue();
	}

	public static JsonToDataResourceLinkConverter forJsonObject(JsonObject jsonObject) {
		return new JsonToDataResourceLinkConverter(jsonObject);
	}

	public JsonObject onlyForTestGetJsonObject() {
		return resourceLinkAsJson;
	}
}
