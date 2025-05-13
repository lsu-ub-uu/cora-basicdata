/*
 * Copyright 2019, 2023 Uppsala University Library
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

import se.uu.ub.cora.basicdata.data.CoraDataResourceLink;
import se.uu.ub.cora.data.Convertible;
import se.uu.ub.cora.data.converter.JsonToDataConverter;
import se.uu.ub.cora.json.parser.JsonObject;
import se.uu.ub.cora.json.parser.JsonParseException;

public class JsonToDataResourceLinkConverter implements JsonToDataConverter {

	private static final int MAX_JSON_KEYS_WITHOUT_REPEAT_ID = 2;
	private static final int MAX_JSON_KEYS_WITH_REPEAT_ID = 3;
	private static final String PARSING_ERROR_MSG = "Error parsing jsonObject: ResourceLink must "
			+ "contain name, mimeType and repeatId.";
	private JsonObject resourceLinkAsJson;

	private JsonToDataResourceLinkConverter(JsonObject resourceLinkAsJson) {
		this.resourceLinkAsJson = resourceLinkAsJson;
	}

	@Override
	public Convertible toInstance() {
		validateJson();
		return createResourceLinkFromJson();
	}

	private void validateJson() {
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

	private CoraDataResourceLink createResourceLinkFromJson() {
		CoraDataResourceLink resourceLink = createResourceLinkWithNameAndMimeType();
		possiblySetRepeatId(resourceLink);
		return resourceLink;
	}

	private CoraDataResourceLink createResourceLinkWithNameAndMimeType() {
		String nameInData = getValueAsStringFromJsonObject("name");
		String mimeType = getValueAsStringFromJsonObject("mimeType");
		return CoraDataResourceLink.withNameInDataAndMimeType(nameInData, mimeType);
	}

	private void possiblySetRepeatId(CoraDataResourceLink resourceLink) {
		if (repeatIdExists()) {
			String repeatId = getValueAsStringFromJsonObject("repeatId");
			resourceLink.setRepeatId(repeatId);
		}
	}

	private boolean nameAndMimeTypeNotExists() {
		return !resourceLinkAsJson.containsKey("name")
				|| !resourceLinkAsJson.containsKey("mimeType");
	}

	private boolean repeatIdExists() {
		return resourceLinkAsJson.containsKey("repeatId");
	}

	private String getValueAsStringFromJsonObject(String key) {
		return resourceLinkAsJson.getValueAsJsonString(key).getStringValue();
	}

	public static JsonToDataResourceLinkConverter forJsonObject(JsonObject jsonObject) {
		return new JsonToDataResourceLinkConverter(jsonObject);
	}

	public JsonObject getJsonObjectonlyForTest() {
		return resourceLinkAsJson;
	}
}
