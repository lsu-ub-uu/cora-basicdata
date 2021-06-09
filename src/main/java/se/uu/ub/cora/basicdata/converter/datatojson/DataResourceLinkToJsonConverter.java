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
import se.uu.ub.cora.data.converter.DataToJsonConverter;
import se.uu.ub.cora.json.builder.JsonBuilderFactory;

public class DataResourceLinkToJsonConverter extends DataGroupToJsonConverter
		implements DataToJsonConverter {

	public DataResourceLinkToJsonConverter(DataResourceLink dataResourceLink, String recordURL,
			JsonBuilderFactory jsonBuilderFactorySpy) {
		super(jsonBuilderFactorySpy, dataResourceLink);
	}

	@Override
	void addExtraStuff() {
		// TODO Auto-generated method stub
		// super.addExtraStuff();
	}

	@Override
	void addChildrenToGroup() {
		super.addChildrenToGroup();
		// TODO: Add actionsLinks

	}
}
