/*
 * Copyright 2015, 2022 Uppsala University Library
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

import java.util.ArrayList;
import java.util.List;

import se.uu.ub.cora.data.converter.JsonToDataConverter;
import se.uu.ub.cora.data.converter.JsonToDataConverterFactory;
import se.uu.ub.cora.json.parser.JsonArray;
import se.uu.ub.cora.json.parser.JsonObject;
import se.uu.ub.cora.json.parser.JsonParseException;
import se.uu.ub.cora.json.parser.JsonValue;

public class JsonToDataConverterFactoryImp implements JsonToDataConverterFactory {

	private static final int NUM_OF_RECORDLINK_CHILDREN = 2;
	private static final int NUM_OF_RECORDLINK_CHILDREN_ONE_OPTIONAL = 3;
	private static final int MAX_NUM_OF_RECORDLINK_CHILDREN = 4;
	private static final int NUM_OF_RESOURCELINK_CHILDREN = 0;

	@Override
	public JsonToDataConverter createForJsonObject(JsonValue jsonValue) {
		if (!(jsonValue instanceof JsonObject)) {
			throw new JsonParseException("Json value is not an object, can not convert");
		}
		JsonObject jsonObject = (JsonObject) jsonValue;

		if (isGroup(jsonObject)) {
			return createConverterForGroupOrLink(jsonObject);
		}
		if (isAtomicData(jsonObject)) {
			return JsonToDataAtomicConverter.forJsonObject(jsonObject);
		}
		return JsonToDataAttributeConverter.forJsonObject(jsonObject);
	}

	private JsonToDataConverter createConverterForGroupOrLink(JsonObject jsonObject) {
		List<String> foundNames = extractChildNames(jsonObject);
		if (isRecordLink(foundNames)) {
			return JsonToDataRecordLinkConverter.forJsonObject(jsonObject);
		}
		if (isResourceLink(foundNames)) {
			return JsonToDataResourceLinkConverter.forJsonObject(jsonObject);
		}

		return JsonToDataGroupConverter.forJsonObject(jsonObject);
	}

	private boolean isResourceLink(List<String> foundNames) {
		return foundNames.size() == NUM_OF_RESOURCELINK_CHILDREN && foundNames.contains("streamId")
				&& foundNames.contains("filename") && foundNames.contains("filesize")
				&& foundNames.contains("mimeType");
	}

	private boolean isRecordLink(List<String> foundNames) {
		return correctChildrenForLink(foundNames)
				|| correctChildrenForLinkWithPathAndRepeatId(foundNames)
				|| correctChildrenForLinkWithPath(foundNames);
	}

	private boolean correctChildrenForLink(List<String> foundNames) {
		return foundNames.size() == NUM_OF_RECORDLINK_CHILDREN
				&& foundNames.contains("linkedRecordType") && foundNames.contains("linkedRecordId");
	}

	private boolean correctChildrenForLinkWithPathAndRepeatId(List<String> foundNames) {
		return foundNames.size() == MAX_NUM_OF_RECORDLINK_CHILDREN
				&& (foundNames.contains("linkedPath") && foundNames.contains("linkedRepeatId"));
	}

	private boolean correctChildrenForLinkWithPath(List<String> foundNames) {
		return foundNames.size() == NUM_OF_RECORDLINK_CHILDREN_ONE_OPTIONAL
				&& foundNames.contains("linkedRecordType") && foundNames.contains("linkedRecordId")
				&& (foundNames.contains("linkedPath") || foundNames.contains("linkedRepeatId"));
	}

	private List<String> extractChildNames(JsonObject jsonObject) {
		JsonArray childrenArray = jsonObject.getValueAsJsonArray("children");
		List<String> foundNames = new ArrayList<>();
		for (JsonValue child : childrenArray) {
			String name = getNameInDataFromChild((JsonObject) child);
			foundNames.add(name);
		}
		return foundNames;
	}

	private String getNameInDataFromChild(JsonObject child) {
		return child.getValueAsJsonString("name").getStringValue();
	}

	private boolean isAtomicData(JsonObject jsonObject) {
		return jsonObject.containsKey("value");
	}

	private boolean isGroup(JsonObject jsonObject) {
		return jsonObject.containsKey("children");
	}

}
