/*
 * Copyright 2024 Uppsala University Library
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

import se.uu.ub.cora.json.builder.JsonArrayBuilder;
import se.uu.ub.cora.json.builder.JsonObjectBuilder;
import se.uu.ub.cora.json.parser.JsonObject;
import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;

public class JsonObjectBuilderSpy implements JsonObjectBuilder {

	MethodCallRecorder MCR = new MethodCallRecorder();

	@Override
	public void addKeyJsonArrayBuilder(String key, JsonArrayBuilder jsonArrayBuilder) {
		MCR.addCall("key", key, "jsonArrayBuilder", jsonArrayBuilder);
	}

	@Override
	public void addKeyJsonObjectBuilder(String key, JsonObjectBuilder jsonObjectBuilder) {
		MCR.addCall("key", key, "jsonObjectBuilder", jsonObjectBuilder);
	}

	@Override
	public void addKeyString(String key, String value) {
		MCR.addCall("key", key, "value", value);
	}

	@Override
	public String toJsonFormattedPrettyString() {
		String out = "fake json from JsonObjectBuilderSpy toJsonFormattedPrettyString";
		MCR.addCall();
		MCR.addReturned(out);
		return out;
	}

	@Override
	public String toJsonFormattedString() {
		String out = "fake json from JsonObjectBuilderSpy toJsonFormattedString";
		MCR.addCall();
		MCR.addReturned(out);
		return out;
	}

	@Override
	public JsonObject toJsonObject() {
		JsonObject out = null;
		MCR.addCall();
		MCR.addReturned(out);
		return out;
	}

}
