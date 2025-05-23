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

package se.uu.ub.cora.basicdata.converter.jsontodata;

import java.util.Map.Entry;

import se.uu.ub.cora.basicdata.data.CoraDataGroup;
import se.uu.ub.cora.data.Convertible;
import se.uu.ub.cora.data.DataChild;
import se.uu.ub.cora.data.converter.JsonToDataConverter;
import se.uu.ub.cora.json.parser.JsonArray;
import se.uu.ub.cora.json.parser.JsonObject;
import se.uu.ub.cora.json.parser.JsonParseException;
import se.uu.ub.cora.json.parser.JsonString;
import se.uu.ub.cora.json.parser.JsonValue;

public class JsonToDataGroupConverter implements JsonToDataConverter {

	private static final int ONE_OPTIONAL_KEY_IS_PRESENT = 3;
	private static final String CHILDREN = "children";
	private static final String ATTRIBUTES = "attributes";
	private static final int NUM_OF_ALLOWED_KEYS_AT_TOP_LEVEL = 4;
	private JsonObject jsonObject;
	protected CoraDataGroup dataGroup;

	static JsonToDataGroupConverter forJsonObject(JsonObject jsonObject) {
		return new JsonToDataGroupConverter(jsonObject);
	}

	protected JsonToDataGroupConverter(JsonObject jsonObject) {
		this.jsonObject = jsonObject;
	}

	@Override
	public Convertible toInstance() {
		try {
			return tryToInstanciate();
		} catch (Exception e) {
			throw new JsonParseException("Error parsing jsonObject: " + e.getMessage(), e);
		}
	}

	private Convertible tryToInstanciate() {
		validateOnlyCorrectKeysAtTopLevel();
		return createDataGroupInstance();
	}

	private String getNameInDataFromJsonObject() {
		return jsonObject.getValueAsJsonString("name").getStringValue();
	}

	protected void validateOnlyCorrectKeysAtTopLevel() {

		if (!jsonObject.containsKey("name")) {
			throw new JsonParseException("Group data must contain key: name");
		}

		if (!hasChildren()) {
			throw new JsonParseException("Group data must contain key: children");
		}

		validateNoOfKeysAtTopLevel();
	}

	private void validateNoOfKeysAtTopLevel() {
		if (threeKeysAtTopLevelButAttributeAndRepeatIdIsMissing()) {
			throw new JsonParseException(
					"Group data must contain name and children, and may contain "
							+ "attributes or repeatId");
		}
		if (maxKeysAtTopLevelButAttributeOrRepeatIdIsMissing()) {
			throw new JsonParseException("Group data must contain key: attributes");
		}

		if (moreKeysAtTopLevelThanAllowed()) {
			throw new JsonParseException(
					"Group data can only contain keys: name, children and attributes");
		}
	}

	private boolean threeKeysAtTopLevelButAttributeAndRepeatIdIsMissing() {
		int oneOptionalKeyPresent = ONE_OPTIONAL_KEY_IS_PRESENT;
		return jsonObject.keySet().size() == oneOptionalKeyPresent && !hasAttributes()
				&& !hasRepeatId();
	}

	private boolean maxKeysAtTopLevelButAttributeOrRepeatIdIsMissing() {
		return jsonObject.keySet().size() == NUM_OF_ALLOWED_KEYS_AT_TOP_LEVEL
				&& (!hasAttributes() || !hasRepeatId());
	}

	private boolean moreKeysAtTopLevelThanAllowed() {
		return jsonObject.keySet().size() > NUM_OF_ALLOWED_KEYS_AT_TOP_LEVEL;
	}

	private Convertible createDataGroupInstance() {
		String nameInData = getNameInDataFromJsonObject();
		createInstanceOfDataElement(nameInData);
		addRepeatIdToGroup();
		if (hasAttributes()) {
			addAttributesToGroup();
		}
		addChildrenToGroup();
		return dataGroup;
	}

	protected void createInstanceOfDataElement(String nameInData) {
		dataGroup = CoraDataGroup.withNameInData(nameInData);
	}

	private void addRepeatIdToGroup() {
		if (hasRepeatId()) {
			dataGroup.setRepeatId(jsonObject.getValueAsJsonString("repeatId").getStringValue());
		}

	}

	private boolean hasAttributes() {
		return jsonObject.containsKey(ATTRIBUTES);
	}

	private boolean hasRepeatId() {
		return jsonObject.containsKey("repeatId");
	}

	private void addAttributesToGroup() {
		JsonObject attributes = jsonObject.getValueAsJsonObject(ATTRIBUTES);
		for (Entry<String, JsonValue> attributeEntry : attributes.entrySet()) {
			addAttributeToGroup(attributeEntry);
		}
	}

	private void addAttributeToGroup(Entry<String, JsonValue> attributeEntry) {
		String value = ((JsonString) attributeEntry.getValue()).getStringValue();
		dataGroup.addAttributeByIdWithValue(attributeEntry.getKey(), value);
	}

	private boolean hasChildren() {
		return jsonObject.containsKey(CHILDREN);
	}

	private void addChildrenToGroup() {
		JsonArray children = jsonObject.getValueAsJsonArray(CHILDREN);
		for (JsonValue child : children) {
			addChildToGroup((JsonObject) child);
		}
	}

	private void addChildToGroup(JsonObject child) {
		JsonToDataConverterFactoryImp jsonToDataConverterFactoryImp = new JsonToDataConverterFactoryImp();
		JsonToDataConverter childJsonToDataConverter = jsonToDataConverterFactoryImp
				.createForJsonObject(child);
		dataGroup.addChild((DataChild) childJsonToDataConverter.toInstance());
	}

	public JsonObject onlyForTestGetJsonObject() {
		return jsonObject;
	}
}
