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

import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.basicdata.data.spy.DataResourceLinkSpy;
import se.uu.ub.cora.data.converter.DataToJsonConverter;
import se.uu.ub.cora.data.converter.DataToJsonConverterFactory;

public class DataResourceLinkToJsonConverterTest {

	DataResourceLinkToJsonConverter resourceLinkToJsonConverter;
	DataToJsonConverterFactory converterFactory;
	JsonBuilderFactorySpy jsonBuilderFactorySpy;
	String recordURL;
	private DataResourceLinkSpy dataResourceLink;

	@BeforeMethod
	public void beforeMethod() {
		recordURL = "https://somesystem.org/rest/records/someRecordType/someRecordId";
		dataResourceLink = new DataResourceLinkSpy("someNameInData");

		jsonBuilderFactorySpy = new JsonBuilderFactorySpy();

		converterFactory = new DataToJsonConverterFactorySpy();
		resourceLinkToJsonConverter = DataResourceLinkToJsonConverter
				.usingConverterFactoryJsonBuilderFactoryAndDataResourceLinkAndRecordUrl(converterFactory,
						jsonBuilderFactorySpy, dataResourceLink, recordURL);

	}

	@Test
	public void testConverterFactorySetInParent() throws Exception {
		assertSame(resourceLinkToJsonConverter.converterFactory, converterFactory);
	}

	@Test
	public void testResourceLinkConverterExtendsGroupConverter() throws Exception {
		assertTrue(resourceLinkToJsonConverter instanceof DataGroupToJsonConverter);
		assertTrue(resourceLinkToJsonConverter instanceof DataToJsonConverter);
	}

	@Test
	public void testNoActions() throws Exception {
		resourceLinkToJsonConverter.hookForSubclassesToImplementExtraConversion();

		assertJsonBuilderNotUsed();
	}

	private void assertJsonBuilderNotUsed() {
		dataResourceLink.MCR.assertParameters("hasReadAction", 0);

		JsonObjectBuilderSpy jsonObjectBuilderSpy = (JsonObjectBuilderSpy) jsonBuilderFactorySpy.MCR
				.getReturnValue("createObjectBuilder", 0);

		jsonObjectBuilderSpy.MCR.assertMethodNotCalled("addKeyJsonObjectBuilder");
	}

	@Test
	public void testActionLinksBuilderAddedToMainBuilder() throws Exception {
		dataResourceLink.hasReadAction = true;

		resourceLinkToJsonConverter.hookForSubclassesToImplementExtraConversion();

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
	public void testActionAddedToActionBuilder() throws Exception {
		dataResourceLink.hasReadAction = true;

		resourceLinkToJsonConverter.hookForSubclassesToImplementExtraConversion();

		JsonObjectBuilderSpy actionLinksBuilderSpy = getActionsBuilder();
		JsonObjectBuilderSpy internalLinkBuilderSpy = (JsonObjectBuilderSpy) jsonBuilderFactorySpy.MCR
				.getReturnValue("createObjectBuilder", 2);
		actionLinksBuilderSpy.MCR.assertParameters("addKeyJsonObjectBuilder", 0, "read",
				internalLinkBuilderSpy);

		internalLinkBuilderSpy.MCR.assertParameters("addKeyString", 0, "rel", "read");
		internalLinkBuilderSpy.MCR.assertParameters("addKeyString", 1, "url",
				recordURL + "/" + dataResourceLink.getNameInData());
		internalLinkBuilderSpy.MCR.assertParameters("addKeyString", 2, "requestMethod", "GET");
		String mimeType = (String) dataResourceLink.MCR.getReturnValue("getMimeType", 0);
		internalLinkBuilderSpy.MCR.assertParameters("addKeyString", 3, "accept", mimeType);
		internalLinkBuilderSpy.MCR.assertNumberOfCallsToMethod("addKeyString", 4);

	}

	private JsonObjectBuilderSpy getActionsBuilder() {
		return (JsonObjectBuilderSpy) jsonBuilderFactorySpy.MCR
				.getReturnValue("createObjectBuilder", 1);
	}
}
