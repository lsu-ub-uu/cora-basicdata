/*
 * Copyright 2021, 2023 Uppsala University Library
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

import se.uu.ub.cora.data.DataResourceLink;
import se.uu.ub.cora.data.converter.DataToJsonConverter;
import se.uu.ub.cora.data.converter.DataToJsonConverterFactory;
import se.uu.ub.cora.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.json.builder.JsonObjectBuilder;

public class DataResourceLinkToJsonConverter implements DataToJsonConverter {

	private static final String GET = "GET";
	private static final String READ = "read";
	private DataResourceLink dataResourceLink;
	private Optional<String> recordURL;
	private DataToJsonConverterFactory converterFactory;
	protected JsonBuilderFactory jsonBuilderFactory;
	private JsonObjectBuilder jsonObjectBuilder;

	private DataResourceLinkToJsonConverter(DataToJsonConverterFactory converterFactory,
			DataResourceLink dataResourceLink, Optional<String> recordURL,
			JsonBuilderFactory jsonBuilderFactory) {
		this.converterFactory = converterFactory;
		this.dataResourceLink = dataResourceLink;
		this.recordURL = recordURL;
		this.jsonBuilderFactory = jsonBuilderFactory;
	}

	public static DataResourceLinkToJsonConverter usingConverterFactoryJsonBuilderFactoryAndDataResourceLinkAndRecordUrl(
			DataToJsonConverterFactory converterFactory, JsonBuilderFactory factory,
			DataResourceLink convertible, Optional<String> recordUrl) {

		return new DataResourceLinkToJsonConverter(converterFactory, convertible, recordUrl,
				factory);
	}

	private void possiblyAddActionLink() {
		if (dataResourceLink.hasReadAction()) {
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
		String url = recordURL.get() + "/" + dataResourceLink.getNameInData();
		String mimeType = dataResourceLink.getMimeType();
		JsonObjectBuilder readAction = jsonBuilderFactory.createObjectBuilder();
		readAction.addKeyString("rel", READ);
		readAction.addKeyString("url", url);
		readAction.addKeyString("requestMethod", GET);
		readAction.addKeyString("accept", mimeType);
		return readAction;
	}

	@Override
	public JsonObjectBuilder toJsonObjectBuilder() {
		jsonObjectBuilder = jsonBuilderFactory.createObjectBuilder();
		addNameInDataAndMimeType();
		possiblyAddRepeatId();
		possiblyAddActionLink();
		return jsonObjectBuilder;
	}

	private void addNameInDataAndMimeType() {
		jsonObjectBuilder.addKeyString("name", dataResourceLink.getNameInData());
		jsonObjectBuilder.addKeyString("mimeType", dataResourceLink.getMimeType());
	}

	private void possiblyAddRepeatId() {
		if (dataResourceLink.hasRepeatId()) {
			jsonObjectBuilder.addKeyString("repeatId", dataResourceLink.getRepeatId());
		}
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
		return recordURL;
	}

}
