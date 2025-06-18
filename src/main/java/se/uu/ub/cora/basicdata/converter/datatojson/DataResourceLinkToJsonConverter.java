/*
 * Copyright 2021, 2023, 2025 Uppsala University Library
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

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import se.uu.ub.cora.data.DataAttribute;
import se.uu.ub.cora.data.DataResourceLink;
import se.uu.ub.cora.data.converter.DataToJsonConverter;
import se.uu.ub.cora.data.converter.DataToJsonConverterFactory;
import se.uu.ub.cora.json.builder.JsonArrayBuilder;
import se.uu.ub.cora.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.json.builder.JsonObjectBuilder;

public class DataResourceLinkToJsonConverter implements DataToJsonConverter {

	private static final String GET = "GET";
	private static final String READ = "read";
	private DataResourceLink resourceLink;
	private Optional<String> baseURL;
	private DataToJsonConverterFactory converterFactory;
	protected JsonBuilderFactory jsonBuilderFactory;
	private JsonObjectBuilder jsonObjectBuilder;

	private DataResourceLinkToJsonConverter(DataToJsonConverterFactory converterFactory,
			DataResourceLink dataResourceLink, Optional<String> baseURL,
			JsonBuilderFactory jsonBuilderFactory) {
		this.converterFactory = converterFactory;
		this.resourceLink = dataResourceLink;
		this.baseURL = baseURL;
		this.jsonBuilderFactory = jsonBuilderFactory;
	}

	public static DataResourceLinkToJsonConverter usingConverterFactoryJsonBuilderFactoryAndDataResourceLinkAndRecordUrl(
			DataToJsonConverterFactory converterFactory, JsonBuilderFactory factory,
			DataResourceLink convertible, Optional<String> recordUrl) {
		return new DataResourceLinkToJsonConverter(converterFactory, convertible, recordUrl,
				factory);
	}

	@Override
	public JsonObjectBuilder toJsonObjectBuilder() {
		jsonObjectBuilder = jsonBuilderFactory.createObjectBuilder();
		addNameInData();
		addChildren();
		possiblyAddAttributes();
		possiblyAddRepeatId();
		possiblyAddActionLink();
		return jsonObjectBuilder;
	}

	private void addNameInData() {
		jsonObjectBuilder.addKeyString("name", resourceLink.getNameInData());
	}

	void addChildren() {
		Map<String, String> childrenToBe = collectResourceLinkFields();
		JsonArrayBuilder childrenArray = createJsonChildren(childrenToBe);
		jsonObjectBuilder.addKeyJsonArrayBuilder("children", childrenArray);
	}

	private JsonArrayBuilder createJsonChildren(Map<String, String> childrenToBe) {
		JsonArrayBuilder childrenJsonArray = jsonBuilderFactory.createArrayBuilder();
		for (Entry<String, String> child : childrenToBe.entrySet()) {
			JsonObjectBuilder jsonChild = createChild(child.getKey(), child.getValue());
			childrenJsonArray.addJsonObjectBuilder(jsonChild);
		}
		return childrenJsonArray;
	}

	private JsonObjectBuilder createChild(String name, String value) {
		JsonObjectBuilder child = jsonBuilderFactory.createObjectBuilder();
		child.addKeyString("name", name);
		child.addKeyString("value", value);
		return child;
	}

	private Map<String, String> collectResourceLinkFields() {
		Map<String, String> childrenToBe = new HashMap<>();
		childrenToBe.put("linkedRecordType", resourceLink.getType());
		childrenToBe.put("linkedRecordId", resourceLink.getId());
		childrenToBe.put("mimeType", resourceLink.getMimeType());
		return childrenToBe;
	}

	private void possiblyAddAttributes() {
		if (resourceLink.hasAttributes()) {
			addAttributes();
		}
	}

	private void addAttributes() {
		JsonObjectBuilder attributes = jsonBuilderFactory.createObjectBuilder();
		for (DataAttribute attribute : resourceLink.getAttributes()) {
			attributes.addKeyString(attribute.getNameInData(), attribute.getValue());
		}
		jsonObjectBuilder.addKeyJsonObjectBuilder("attributes", attributes);
	}

	private void possiblyAddRepeatId() {
		if (resourceLink.hasRepeatId()) {
			jsonObjectBuilder.addKeyString("repeatId", resourceLink.getRepeatId());
		}
	}

	private void possiblyAddActionLink() {
		if (resourceLink.hasReadAction()) {
			createReadActionLink();
		}
	}

	private void createReadActionLink() {
		JsonObjectBuilder actionLinksObject = jsonBuilderFactory.createObjectBuilder();
		JsonObjectBuilder readAction = buildReadAction();
		actionLinksObject.addKeyJsonObjectBuilder(READ, readAction);
		jsonObjectBuilder.addKeyJsonObjectBuilder("actionLinks", actionLinksObject);
	}

	private JsonObjectBuilder buildReadAction() {
		String url = generateURL(resourceLink.getType(), resourceLink.getId(),
				resourceLink.getNameInData());
		String mimeType = resourceLink.getMimeType();
		JsonObjectBuilder readAction = jsonBuilderFactory.createObjectBuilder();
		readAction.addKeyString("rel", READ);
		readAction.addKeyString("url", url);
		readAction.addKeyString("requestMethod", GET);
		readAction.addKeyString("accept", mimeType);
		return readAction;
	}

	private String generateURL(String recordType, String recordId, String nameInData) {
		return MessageFormat.format("{0}/{1}/{2}/{3}", baseURL.get(), recordType, recordId,
				nameInData);
	}

	@Override
	public String toJsonCompactFormat() {
		return toJsonObjectBuilder().toJsonFormattedString();
	}

	@Override
	public String toJson() {
		return toJsonObjectBuilder().toJsonFormattedPrettyString();
	}

	Object onlyForTestGetConverterFactory() {
		return converterFactory;
	}

	JsonBuilderFactory onlyForTestGetJsonBuilderFactory() {
		return jsonBuilderFactory;
	}

	Optional<String> onlyForTestGetRecordUrl() {
		return baseURL;
	}

}
