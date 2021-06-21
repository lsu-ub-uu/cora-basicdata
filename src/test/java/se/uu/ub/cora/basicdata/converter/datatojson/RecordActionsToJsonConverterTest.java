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

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.Action;
import se.uu.ub.cora.json.builder.JsonObjectBuilder;

public class RecordActionsToJsonConverterTest {

	private RecordActionsToJsonConverter actionsConverter;
	private List<Action> actions;
	private JsonBuilderFactorySpy builderFactory;
	private String baseUrl = "some/base/url/";
	private String recordType = "someRecordType";
	private String recordId = "someRecordId";

	@BeforeMethod
	private void beforeMethod() {
		builderFactory = new JsonBuilderFactorySpy();
		actions = new ArrayList<>();

		actionsConverter = new RecordActionsToJsonConverterImp(builderFactory, baseUrl);

	}

	@Test
	public void testInit() throws Exception {
		JsonObjectBuilder objectBuilder = actionsConverter.toJsonObjectBuilder(actions, recordType,
				recordId);

		builderFactory.MCR.assertMethodWasCalled("createObjectBuilder");
		builderFactory.MCR.assertReturn("createObjectBuilder", 0, objectBuilder);
		builderFactory.MCR.assertNumberOfCallsToMethod("createObjectBuilder", 1);
	}

	@Test
	public void testReadAction() throws Exception {
		actions.add(Action.READ);

		actionsConverter.toJsonObjectBuilder(actions, recordType, recordId);

		builderFactory.MCR.assertNumberOfCallsToMethod("createObjectBuilder", 2);

		JsonObjectBuilderSpy mainLinkBuilder = (JsonObjectBuilderSpy) builderFactory.MCR
				.getReturnValue("createObjectBuilder", 0);
		JsonObjectBuilderSpy internalLinkBuilder = (JsonObjectBuilderSpy) builderFactory.MCR
				.getReturnValue("createObjectBuilder", 1);

		internalLinkBuilder.MCR.assertParameters("addKeyString", 0, "rel", "read");
		internalLinkBuilder.MCR.assertParameters("addKeyString", 1, "url",
				baseUrl + recordType + "/" + recordId);
		// internalLinkBuilder.MCR.assertParameters("addKeyString", 0, "requestMethod", "read");

		mainLinkBuilder.MCR.assertParameters("addKeyJsonObjectBuilder", 0, "read",
				internalLinkBuilder);

	}

}
