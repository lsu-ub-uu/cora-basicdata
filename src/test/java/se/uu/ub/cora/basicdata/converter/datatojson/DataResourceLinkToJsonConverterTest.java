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

import java.util.Optional;
import java.util.Set;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.basicdata.data.spy.DataResourceLinkSpy;
import se.uu.ub.cora.data.converter.DataToJsonConverterFactory;
import se.uu.ub.cora.data.spies.DataAttributeSpy;
import se.uu.ub.cora.json.builder.org.OrgJsonBuilderFactoryAdapter;

public class DataResourceLinkToJsonConverterTest {

	DataResourceLinkToJsonConverter converter;
	DataToJsonConverterFactory converterFactory;
	JsonBuilderFactorySpy jsonBuilderFactorySpy;
	Optional<String> recordURL;
	private DataResourceLinkSpy dataResourceLink;

	@BeforeMethod
	public void beforeMethod() {
		dataResourceLink = new DataResourceLinkSpy();
		jsonBuilderFactorySpy = new JsonBuilderFactorySpy();
		converterFactory = new DataToJsonConverterFactorySpy();

		constructWithRecordUrl();
	}

	private void constructWithRecordUrl() {
		dataResourceLink.MRV.setDefaultReturnValuesSupplier("getNameInData", () -> "master");
		dataResourceLink.MRV.setDefaultReturnValuesSupplier("getMimeType",
				() -> "application/octet-stream");

		recordURL = Optional.of("https://somesystem.org/rest/records/someRecordType/someRecordId");
		converter = DataResourceLinkToJsonConverter
				.usingConverterFactoryJsonBuilderFactoryAndDataResourceLinkAndRecordUrl(
						converterFactory, new OrgJsonBuilderFactoryAdapter(), dataResourceLink,
						recordURL);
	}

	@Test
	public void testToJson() {
		String json = converter.toJson();

		String expectedJson = """
				{
				    "name": "master",
				    "mimeType": "application/octet-stream"
				}""";
		assertEquals(json, expectedJson);
	}

	@Test
	public void testToJson2() {
		dataResourceLink.MRV.setDefaultReturnValuesSupplier("getNameInData", () -> "thumbnail");
		dataResourceLink.MRV.setDefaultReturnValuesSupplier("getMimeType", () -> "image/jpeg");

		String json = converter.toJson();

		String expectedJson = """
				{
				    "name": "thumbnail",
				    "mimeType": "image/jpeg"
				}""";
		assertEquals(json, expectedJson);
	}

	@Test
	public void testToJson_withAttributes() {
		DataAttributeSpy attr01 = createAttributeUsingNameInDataAndValue("atribute01", "value01");
		DataAttributeSpy attr02 = createAttributeUsingNameInDataAndValue("atribute02", "value02");
		dataResourceLink.MRV.setDefaultReturnValuesSupplier("hasAttributes", () -> true);
		dataResourceLink.MRV.setDefaultReturnValuesSupplier("getAttributes",
				() -> Set.of(attr01, attr02));

		String json = converter.toJson();

		String expectedJson = """
				{
				    "name": "master",
				    "attributes": {
				        "atribute01": "value01",
				        "atribute02": "value02"
				    },
				    "mimeType": "application/octet-stream"
				}""";
		assertEquals(json, expectedJson);
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

		String expectedJson = """
				{
				    "repeatId": "1",
				    "name": "master",
				    "mimeType": "application/octet-stream"
				}""";
		assertEquals(json, expectedJson);
	}

	@Test
	public void testToJsonCompactFormatWithoutRepeatId() {
		String json = converter.toJsonCompactFormat();

		String expectedJson = """
				{"name":"master","mimeType":"application/octet-stream"}""";

		assertEquals(json, expectedJson);
	}

	@Test
	public void testToJsonCompactFormatWithRepeatId() {
		dataResourceLink.MRV.setDefaultReturnValuesSupplier("hasRepeatId", () -> true);
		dataResourceLink.MRV.setDefaultReturnValuesSupplier("getRepeatId", () -> "1");

		String json = converter.toJsonCompactFormat();

		String expectedJson = """
				{"repeatId":"1","name":"master","mimeType":"application/octet-stream"}""";
		assertEquals(json, expectedJson);
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
						converterFactory, jsonBuilderFactorySpy, dataResourceLink, recordURL);

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
						converterFactory, jsonBuilderFactorySpy, dataResourceLink, recordURL);

		converter.toJsonObjectBuilder();

		dataResourceLink.MCR.assertParameters("hasReadAction", 0);
		assertActionLinksBuilderAddedToMainBuilder();
	}

	private void assertActionLinksBuilderAddedToMainBuilder() {
		JsonObjectBuilderSpy mainBuilderSpy = (JsonObjectBuilderSpy) jsonBuilderFactorySpy.MCR
				.getReturnValue("createObjectBuilder", 0);
		JsonObjectBuilderSpy actionLinksBuilderSpy = getActionsBuilder();

		mainBuilderSpy.MCR.assertParameters("addKeyJsonObjectBuilder", 0, "actionLinks",
				actionLinksBuilderSpy);
	}

	@Test
	public void testActionAddedToActionBuilder() {
		dataResourceLink.MRV.setDefaultReturnValuesSupplier("hasReadAction", () -> true);
		converter = DataResourceLinkToJsonConverter
				.usingConverterFactoryJsonBuilderFactoryAndDataResourceLinkAndRecordUrl(
						converterFactory, jsonBuilderFactorySpy, dataResourceLink, recordURL);

		converter.toJsonObjectBuilder();

		JsonObjectBuilderSpy actionLinksBuilderSpy = getActionsBuilder();
		JsonObjectBuilderSpy internalLinkBuilderSpy = (JsonObjectBuilderSpy) jsonBuilderFactorySpy.MCR
				.getReturnValue("createObjectBuilder", 2);
		actionLinksBuilderSpy.MCR.assertParameters("addKeyJsonObjectBuilder", 0, "read",
				internalLinkBuilderSpy);

		internalLinkBuilderSpy.MCR.assertParameters("addKeyString", 0, "rel", "read");
		internalLinkBuilderSpy.MCR.assertParameters("addKeyString", 1, "url",
				recordURL.get() + "/" + dataResourceLink.getNameInData());
		internalLinkBuilderSpy.MCR.assertParameters("addKeyString", 2, "requestMethod", "GET");
		String mimeType = (String) dataResourceLink.MCR.getReturnValue("getMimeType", 0);
		internalLinkBuilderSpy.MCR.assertParameters("addKeyString", 3, "accept", mimeType);
		internalLinkBuilderSpy.MCR.assertNumberOfCallsToMethod("addKeyString", 4);

	}

	private JsonObjectBuilderSpy getActionsBuilder() {
		return (JsonObjectBuilderSpy) jsonBuilderFactorySpy.MCR
				.getReturnValue("createObjectBuilder", 1);
	}

	@Test
	public void testJsonWithActions() {
		dataResourceLink.MRV.setDefaultReturnValuesSupplier("hasReadAction", () -> true);
		converter = DataResourceLinkToJsonConverter
				.usingConverterFactoryJsonBuilderFactoryAndDataResourceLinkAndRecordUrl(
						converterFactory, new OrgJsonBuilderFactoryAdapter(), dataResourceLink,
						recordURL);

		String json = converter.toJson();

		String excpectedJson = """
				{
				    "actionLinks": {"read": {
				        "requestMethod": "GET",
				        "rel": "read",
				        "url": "https://somesystem.org/rest/records/someRecordType/someRecordId/master",
				        "accept": "application/octet-stream"
				    }},
				    "name": "master",
				    "mimeType": "application/octet-stream"
				}""";
		assertEquals(json, excpectedJson);
	}
}
