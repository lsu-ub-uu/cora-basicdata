/*
 * Copyright 2021 Uppsala University Library
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

import se.uu.ub.cora.data.DataResourceLink;
import se.uu.ub.cora.json.builder.JsonBuilderFactory;

public class DataResourceLinkToJsonConverterForTest extends DataResourceLinkToJsonConverter {

	public boolean addChildrenToGroupHasBeenCalled = false;
	public boolean dataGroupToJsonHasBeenCalled = false;

	public DataResourceLinkToJsonConverterForTest(DataResourceLink dataResourceLink,
			String recordURL, JsonBuilderFactory jsonBuilderFactorySpy) {
		super(dataResourceLink, recordURL, jsonBuilderFactorySpy);
	}

	// @Override
	// public String toJson() {
	// dataGroupToJsonHasBeenCalled = true;
	// // super.addChildrenToGroup();
	// addExtraStuff();
	// return "fakeJsonFromForTest";
	// }

	@Override
	void hookForSubclassesToImplementExtraConversion() {
		// TODO Auto-generated method stub
	}

	// @Override
	// void addChildrenToGroup() {
	// // dataGroupJsonObjectBuilder = new JsonObjectBuilderSpy();
	//
	// // addChildrenToGroupHasBeenCalled = true;
	// super.addChildrenToGroup();
	// }
}
