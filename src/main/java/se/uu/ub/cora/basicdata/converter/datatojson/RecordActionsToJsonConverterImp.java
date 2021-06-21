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

import java.util.List;

import se.uu.ub.cora.data.Action;
import se.uu.ub.cora.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.json.builder.JsonObjectBuilder;

public class RecordActionsToJsonConverterImp implements RecordActionsToJsonConverter {

	private JsonBuilderFactory builderFactory;
	private JsonObjectBuilder mainBuilder;
	private String baseUrl;

	public RecordActionsToJsonConverterImp(JsonBuilderFactory builderFactory, String baseUrl) {
		this.builderFactory = builderFactory;
		this.baseUrl = baseUrl;
		mainBuilder = builderFactory.createObjectBuilder();
	}

	@Override
	public JsonObjectBuilder toJsonObjectBuilder(List<Action> actions, String recordType,
			String recordId) {
		if (!actions.isEmpty()) {
			Action action = actions.get(0);
			JsonObjectBuilder linkBuilder = builderFactory.createObjectBuilder();
			String lowerCaseAction = action.name().toLowerCase();
			linkBuilder.addKeyString("rel", lowerCaseAction);
			linkBuilder.addKeyString("url", baseUrl + recordType + "/" + recordId);
			mainBuilder.addKeyJsonObjectBuilder(lowerCaseAction, linkBuilder);
		}
		return mainBuilder;
	}

}
