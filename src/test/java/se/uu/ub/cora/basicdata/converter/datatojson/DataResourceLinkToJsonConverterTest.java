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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

import java.text.MessageFormat;
import java.util.Optional;
import java.util.Set;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.converter.DataToJsonConverterFactory;
import se.uu.ub.cora.data.spies.DataAttributeSpy;
import se.uu.ub.cora.data.spies.DataResourceLinkSpy;
import se.uu.ub.cora.json.builder.org.OrgJsonBuilderFactoryAdapter;

public class DataResourceLinkToJsonConverterTest {

	DataResourceLinkToJsonConverter converter;
	DataToJsonConverterFactory converterFactory;
	JsonBuilderFactorySpy jsonBuilderFactorySpy;
	Optional<String> baseURL;
	DataResourceLinkSpy dataResourceLink;

	@BeforeMethod
	public void beforeMethod() {
		dataResourceLink = new DataResourceLinkSpy();
		jsonBuilderFactorySpy = new JsonBuilderFactorySpy();
		converterFactory = new DataToJsonConverterFactorySpy();

		constructWithRecordUrl();
	}

	private void constructWithRecordUrl() {
		dataResourceLink.MRV.setDefaultReturnValuesSupplier("getNameInData", () -> "master");
		dataResourceLink.MRV.setDefaultReturnValuesSupplier("getType", () -> "someType");
		dataResourceLink.MRV.setDefaultReturnValuesSupplier("getId", () -> "someId");
		dataResourceLink.MRV.setDefaultReturnValuesSupplier("getMimeType", () -> "image/png");

		baseURL = Optional.of("https://somesystem.org/rest/records");
		converter = DataResourceLinkToJsonConverter
				.usingConverterFactoryJsonBuilderFactoryAndDataResourceLinkAndRecordUrl(
						converterFactory, new OrgJsonBuilderFactoryAdapter(), dataResourceLink,
						baseURL);
	}

	@Test
	public void testToJson() {
		String json = converter.toJson();
		String jsonCompacted = converter.toJsonCompactFormat();

		String expectedJson = """
				{
				    "children": [
				        {
				            "name": "linkedRecordType",
				            "value": "someType"
				        },
				        {
				            "name": "linkedRecordId",
				            "value": "someId"
				        },
				        {
				            "name": "mimeType",
				            "value": "image/png"
				        }
				    ],
				    "name": "master"
				}""";
		assertEquals(json, expectedJson);
		assertEquals(jsonCompacted, toCompactFormat(expectedJson));
	}

	private String toCompactFormat(String json) {
		return json.replaceAll("\\s+", "");

	}

	@Test
	public void testToJson2() {
		dataResourceLink.MRV.setDefaultReturnValuesSupplier("getNameInData", () -> "thumbnail");
		dataResourceLink.MRV.setDefaultReturnValuesSupplier("getMimeType", () -> "image/jpeg");

		String json = converter.toJson();
		String jsonCompacted = converter.toJsonCompactFormat();

		String expectedJson = """
				{
				    "children": [
				        {
				            "name": "linkedRecordType",
				            "value": "someType"
				        },
				        {
				            "name": "linkedRecordId",
				            "value": "someId"
				        },
				        {
				            "name": "mimeType",
				            "value": "image/jpeg"
				        }
				    ],
				    "name": "thumbnail"
				}""";
		assertEquals(json, expectedJson);
		assertEquals(jsonCompacted, toCompactFormat(expectedJson));
	}

	@Test
	public void testToJson_withAttributes() {
		DataAttributeSpy attr01 = createAttributeUsingNameInDataAndValue("atribute01", "value01");
		DataAttributeSpy attr02 = createAttributeUsingNameInDataAndValue("atribute02", "value02");
		dataResourceLink.MRV.setDefaultReturnValuesSupplier("hasAttributes", () -> true);
		dataResourceLink.MRV.setDefaultReturnValuesSupplier("getAttributes",
				() -> Set.of(attr01, attr02));

		String json = converter.toJson();
		String jsonCompacted = converter.toJsonCompactFormat();

		String expectedJson = """
				{
				    "children": [
				        {
				            "name": "linkedRecordType",
				            "value": "someType"
				        },
				        {
				            "name": "linkedRecordId",
				            "value": "someId"
				        },
				        {
				            "name": "mimeType",
				            "value": "image/png"
				        }
				    ],
				    "name": "master",
				    "attributes": {
				        "atribute01": "value01",
				        "atribute02": "value02"
				    }
				}""";
		assertEquals(json, expectedJson);
		assertEquals(jsonCompacted, toCompactFormat(expectedJson));
	}

	private DataAttributeSpy createAttributeUsingNameInDataAndValue(String nameInData,
			String value) {
		DataAttributeSpy attribute = new DataAttributeSpy();
		attribute.MRV.setDefaultReturnValuesSupplier("getNameInData", () -> nameInData);
		attribute.MRV.setDefaultReturnValuesSupplier("getValue", () -> value);
		return attribute;
	}

	@Test
	public void testJsonWithRepeatId() {
		dataResourceLink.MRV.setDefaultReturnValuesSupplier("hasRepeatId", () -> true);
		dataResourceLink.MRV.setDefaultReturnValuesSupplier("getRepeatId", () -> "1");

		String json = converter.toJson();
		String jsonCompacted = converter.toJsonCompactFormat();

		String expectedJson = """
				{
				    "repeatId": "1",
				    "children": [
				        {
				            "name": "linkedRecordType",
				            "value": "someType"
				        },
				        {
				            "name": "linkedRecordId",
				            "value": "someId"
				        },
				        {
				            "name": "mimeType",
				            "value": "image/png"
				        }
				    ],
				    "name": "master"
				}""";
		assertEquals(json, expectedJson);
		assertEquals(jsonCompacted, toCompactFormat(expectedJson));
	}

	@Test
	public void testConverterFactorySetInParent() {
		constructWithRecordUrl();

		assertSame(converter.onlyForTestGetConverterFactory(), converterFactory);
	}

	@Test
	public void testNoActions() {
		converter = DataResourceLinkToJsonConverter
				.usingConverterFactoryJsonBuilderFactoryAndDataResourceLinkAndRecordUrl(
						converterFactory, jsonBuilderFactorySpy, dataResourceLink, baseURL);

		converter.toJsonObjectBuilder();

		assertJsonBuilderNotUsed();
	}

	private void assertJsonBuilderNotUsed() {
		dataResourceLink.MCR.assertParameters("hasReadAction", 0);

		JsonObjectBuilderSpy jsonObjectBuilderSpy = (JsonObjectBuilderSpy) jsonBuilderFactorySpy.MCR
				.getReturnValue("createObjectBuilder", 0);

		jsonObjectBuilderSpy.MCR.assertMethodNotCalled("addKeyJsonObjectBuilder");
	}

	@Test
	public void testActionLinksBuilderAddedToMainBuilder() {
		dataResourceLink.MRV.setDefaultReturnValuesSupplier("hasReadAction", () -> true);
		converter = DataResourceLinkToJsonConverter
				.usingConverterFactoryJsonBuilderFactoryAndDataResourceLinkAndRecordUrl(
						converterFactory, jsonBuilderFactorySpy, dataResourceLink, baseURL);

		converter.toJsonObjectBuilder();

		dataResourceLink.MCR.assertParameters("hasReadAction", 0);
		assertActionLinksBuilderAddedToMainBuilder();
	}

	private void assertActionLinksBuilderAddedToMainBuilder() {
		JsonObjectBuilderSpy mainBuilderSpy = (JsonObjectBuilderSpy) jsonBuilderFactorySpy.MCR
				.getReturnValue("createObjectBuilder", 0);
		JsonObjectBuilderSpy actionLinksBuilderSpy = getJsonBuilderForActionLinks();

		mainBuilderSpy.MCR.assertParameters("addKeyJsonObjectBuilder", 0, "actionLinks",
				actionLinksBuilderSpy);
	}

	@Test
	public void testActionAddedToActionBuilder() {
		dataResourceLink.MRV.setDefaultReturnValuesSupplier("hasReadAction", () -> true);
		converter = DataResourceLinkToJsonConverter
				.usingConverterFactoryJsonBuilderFactoryAndDataResourceLinkAndRecordUrl(
						converterFactory, jsonBuilderFactorySpy, dataResourceLink, baseURL);

		converter.toJsonObjectBuilder();

		JsonObjectBuilderSpy jsonBuilderActionLinks = getJsonBuilderForActionLinks();
		JsonObjectBuilderSpy jsonBuilderActionLinksFields = getJsonBuilderForActionLinksFields();
		jsonBuilderActionLinks.MCR.assertParameters("addKeyJsonObjectBuilder", 0, "read",
				jsonBuilderActionLinksFields);
		assertActionLinkFields(jsonBuilderActionLinksFields);

	}

	private void assertActionLinkFields(JsonObjectBuilderSpy jsonBuilderActionLinksFields) {
		jsonBuilderActionLinksFields.MCR.assertParameters("addKeyString", 0, "rel", "read");
		jsonBuilderActionLinksFields.MCR.assertParameters("addKeyString", 1, "url",
				generateURL("someType", "someId", dataResourceLink.getNameInData()));
		jsonBuilderActionLinksFields.MCR.assertParameters("addKeyString", 2, "requestMethod",
				"GET");
		String mimeType = (String) dataResourceLink.MCR.getReturnValue("getMimeType", 0);
		jsonBuilderActionLinksFields.MCR.assertParameters("addKeyString", 3, "accept", mimeType);
		jsonBuilderActionLinksFields.MCR.assertNumberOfCallsToMethod("addKeyString", 4);
	}

	private String generateURL(String recordType, String recordId, String nameInData) {
		return MessageFormat.format("{0}/{1}/{2}/{3}", baseURL.get(), recordType, recordId,
				nameInData);
	}

	private JsonObjectBuilderSpy getJsonBuilderForActionLinksFields() {
		return (JsonObjectBuilderSpy) jsonBuilderFactorySpy.MCR
				.getReturnValue("createObjectBuilder", 5);
	}

	private JsonObjectBuilderSpy getJsonBuilderForActionLinks() {
		return (JsonObjectBuilderSpy) jsonBuilderFactorySpy.MCR
				.getReturnValue("createObjectBuilder", 4);
	}

	@Test
	public void testJsonWithActions() {
		dataResourceLink.MRV.setDefaultReturnValuesSupplier("hasReadAction", () -> true);
		converter = DataResourceLinkToJsonConverter
				.usingConverterFactoryJsonBuilderFactoryAndDataResourceLinkAndRecordUrl(
						converterFactory, new OrgJsonBuilderFactoryAdapter(), dataResourceLink,
						baseURL);

		String json = converter.toJson();
		String jsonCompacted = converter.toJsonCompactFormat();

		String expectedJson = """
				{
				    "children": [
				        {
				            "name": "linkedRecordType",
				            "value": "someType"
				        },
				        {
				            "name": "linkedRecordId",
				            "value": "someId"
				        },
				        {
				            "name": "mimeType",
				            "value": "image/png"
				        }
				    ],
				    "actionLinks": {"read": {
				        "requestMethod": "GET",
				        "rel": "read",
				        "url": "https://somesystem.org/rest/records/someType/someId/master",
				        "accept": "image/png"
				    }},
				    "name": "master"
				}""";
		assertEquals(json, expectedJson);
		assertEquals(jsonCompacted, toCompactFormat(expectedJson));
	}
}
