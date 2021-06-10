/*
 * Copyright 2015 Uppsala University Library
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

import se.uu.ub.cora.data.DataAttribute;
import se.uu.ub.cora.data.DataElement;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataPart;
import se.uu.ub.cora.data.converter.DataToJsonConverter;
import se.uu.ub.cora.data.converter.DataToJsonConverterFactory;
import se.uu.ub.cora.json.builder.JsonArrayBuilder;
import se.uu.ub.cora.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.json.builder.JsonObjectBuilder;

public class DataGroupToJsonConverter implements DataToJsonConverter {

	private DataGroup dataGroup;
	JsonObjectBuilder dataGroupJsonObjectBuilder;
	private JsonBuilderFactory jsonBuilderFactory;

	public static DataToJsonConverter usingJsonFactoryForDataGroup(JsonBuilderFactory factory,
			DataGroup dataGroup) {
		return new DataGroupToJsonConverter(factory, dataGroup);
	}

	DataGroupToJsonConverter(JsonBuilderFactory factory, DataGroup dataGroup) {
		this.jsonBuilderFactory = factory;
		this.dataGroup = dataGroup;
		dataGroupJsonObjectBuilder = factory.createObjectBuilder();
	}

	@Override
	public JsonObjectBuilder toJsonObjectBuilder() {
		possiblyAddRepeatId();
		if (dataGroup.hasAttributes()) {
			addAttributesToGroup();
		}
		if (dataGroup.hasChildren()) {
			addChildrenToGroup();
		}
		hookForSubclassesToImplementExtraConversion();
		dataGroupJsonObjectBuilder.addKeyString("name", dataGroup.getNameInData());
		return dataGroupJsonObjectBuilder;
	}

	void hookForSubclassesToImplementExtraConversion() {
		// TODO Auto-generated method stub
	}

	private void possiblyAddRepeatId() {
		if (hasNonEmptyRepeatId()) {
			dataGroupJsonObjectBuilder.addKeyString("repeatId", dataGroup.getRepeatId());
		}
	}

	private boolean hasNonEmptyRepeatId() {
		return dataGroup.getRepeatId() != null && !"".equals(dataGroup.getRepeatId());
	}

	private void addAttributesToGroup() {
		JsonObjectBuilder attributes = jsonBuilderFactory.createObjectBuilder();
		for (DataAttribute attribute : dataGroup.getAttributes()) {
			attributes.addKeyString(attribute.getNameInData(), attribute.getValue());
		}
		dataGroupJsonObjectBuilder.addKeyJsonObjectBuilder("attributes", attributes);
	}

	void addChildrenToGroup() {
		DataToJsonConverterFactory dataToJsonConverterFactory = new DataToJsonConverterFactoryImp();
		JsonArrayBuilder childrenArray = jsonBuilderFactory.createArrayBuilder();
		for (DataElement dataElement : dataGroup.getChildren()) {
			DataPart dataPart = dataElement;
			childrenArray.addJsonObjectBuilder(dataToJsonConverterFactory
					.createForDataElement(jsonBuilderFactory, dataPart).toJsonObjectBuilder());
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
