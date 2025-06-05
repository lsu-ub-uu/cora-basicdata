/*
 * Copyright 2015, 2025 Uppsala University Library
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

import se.uu.ub.cora.data.Convertible;
import se.uu.ub.cora.data.DataAttribute;
import se.uu.ub.cora.data.DataChild;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.converter.DataToJsonConverter;
import se.uu.ub.cora.data.converter.DataToJsonConverterFactory;
import se.uu.ub.cora.json.builder.JsonArrayBuilder;
import se.uu.ub.cora.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.json.builder.JsonObjectBuilder;

public class DataGroupToJsonConverter implements DataToJsonConverter {

	private DataGroup dataGroup;
	JsonObjectBuilder dataGroupJsonObjectBuilder;
	JsonBuilderFactory jsonBuilderFactory;
	DataToJsonConverterFactory converterFactory;

	public static DataToJsonConverter usingConverterFactoryAndBuilderFactoryAndDataGroup(
			DataToJsonConverterFactory converterFactory, JsonBuilderFactory builderFactory,
			DataGroup dataGroup) {
		return new DataGroupToJsonConverter(converterFactory, builderFactory, dataGroup);
	}

	DataGroupToJsonConverter(DataToJsonConverterFactory converterFactory,
			JsonBuilderFactory builderFactory, DataGroup dataGroup) {
		this.converterFactory = converterFactory;
		this.jsonBuilderFactory = builderFactory;
		this.dataGroup = dataGroup;
		dataGroupJsonObjectBuilder = builderFactory.createObjectBuilder();
	}

	@Override
	public JsonObjectBuilder toJsonObjectBuilder() {
		possiblyAddRepeatId();
		possiblyAddAttributes();
		possiblyAddChildren();
		hookForSubclassesToImplementExtraConversion();
		dataGroupJsonObjectBuilder.addKeyString("name", dataGroup.getNameInData());
		return dataGroupJsonObjectBuilder;
	}

	/**
	 * hookForSubclassesToImplementExtraConversion enables subclasses (converters for DataRecordLink
	 * and DataResourceLink) to add extra conversion needed to completely convert their classes to
	 * json
	 */
	void hookForSubclassesToImplementExtraConversion() {
		// No default implementation in this class
	}

	private void possiblyAddRepeatId() {
		if (dataGroup.hasRepeatId()) {
			dataGroupJsonObjectBuilder.addKeyString("repeatId", dataGroup.getRepeatId());
		}
	}

	private void possiblyAddAttributes() {
		if (dataGroup.hasAttributes()) {
			addAttributesToGroup();
		}
	}

	private void addAttributesToGroup() {
		JsonObjectBuilder attributes = jsonBuilderFactory.createObjectBuilder();
		for (DataAttribute attribute : dataGroup.getAttributes()) {
			attributes.addKeyString(attribute.getNameInData(), attribute.getValue());
		}
		dataGroupJsonObjectBuilder.addKeyJsonObjectBuilder("attributes", attributes);
	}

	private void possiblyAddChildren() {
		if (dataGroup.hasChildren()) {
			addChildrenToGroup();
		}
	}

	void addChildrenToGroup() {
		JsonArrayBuilder childrenArray = jsonBuilderFactory.createArrayBuilder();
		for (DataChild dataElement : dataGroup.getChildren()) {
			Convertible convertible = (Convertible) dataElement;
			childrenArray.addJsonObjectBuilder(
					converterFactory.factorUsingConvertible(convertible).toJsonObjectBuilder());
		}
		dataGroupJsonObjectBuilder.addKeyJsonArrayBuilder("children", childrenArray);
	}

	@Override
	public String toJson() {
		JsonObjectBuilder jsonObjectBuilder = toJsonObjectBuilder();
		return jsonObjectBuilder.toJsonFormattedPrettyString();
	}

	@Override
	public String toJsonCompactFormat() {
		JsonObjectBuilder jsonObjectBuilder = toJsonObjectBuilder();
		return jsonObjectBuilder.toJsonFormattedString();
	}
}
