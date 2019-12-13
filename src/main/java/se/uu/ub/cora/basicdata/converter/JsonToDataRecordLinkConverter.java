/*
 * Copyright 2019 Uppsala University Library
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
package se.uu.ub.cora.basicdata.converter;

import se.uu.ub.cora.basicdata.data.CoraDataRecordLink;
import se.uu.ub.cora.data.DataPart;
import se.uu.ub.cora.data.converter.JsonToDataConverter;
import se.uu.ub.cora.json.parser.JsonObject;
import se.uu.ub.cora.json.parser.JsonParseException;

public class JsonToDataRecordLinkConverter extends JsonToDataGroupConverter
		implements JsonToDataConverter {

	public static JsonToDataRecordLinkConverter forJsonObject(JsonObject jsonObject) {
		return new JsonToDataRecordLinkConverter(jsonObject);
	}

	private JsonToDataRecordLinkConverter(JsonObject jsonObject) {
		super(jsonObject);
	}

	@Override
	public DataPart toInstance() {
		CoraDataRecordLink recordLink = (CoraDataRecordLink) super.toInstance();
		throwErrorIfLinkChildrenAreIncorrect(recordLink);
		return recordLink;
	}

	private void throwErrorIfLinkChildrenAreIncorrect(CoraDataRecordLink recordLink) {
		if (incorrectNumberOfChildren(recordLink) || incorrectChildren(recordLink)) {
			throw new JsonParseException(
					"RecordLinkData must and can only contain children with name linkedRecordType and linkedRecordId");
		}
	}

	private boolean incorrectNumberOfChildren(CoraDataRecordLink recordLink) {
		return recordLink.getChildren().size() != 2;
	}

	private boolean incorrectChildren(CoraDataRecordLink recordLink) {
		return !recordLink.containsChildWithNameInData("linkedRecordType")
				|| !recordLink.containsChildWithNameInData("linkedRecordId");
	}

	@Override
	protected void createInstanceOfDataElement(String nameInData) {
		dataGroup = CoraDataRecordLink.withNameInData(nameInData);
	}

}
