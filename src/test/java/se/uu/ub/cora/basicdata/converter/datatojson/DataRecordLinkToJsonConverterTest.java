/*
 * Copyright 2021, 2025 Uppsala University Library
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
import static org.testng.Assert.fail;

import java.util.Arrays;
import java.util.Optional;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.basicdata.data.spy.DataRecordLinkOldSpy;
import se.uu.ub.cora.data.DataChild;
import se.uu.ub.cora.data.converter.DataToJsonConverter;
import se.uu.ub.cora.data.converter.DataToJsonConverterFactory;
import se.uu.ub.cora.data.spies.DataAttributeSpy;
import se.uu.ub.cora.data.spies.DataGroupSpy;
import se.uu.ub.cora.data.spies.DataRecordLinkSpy;

public class DataRecordLinkToJsonConverterTest {
	DataRecordLinkToJsonConverter recordLinkToJsonConverter;
	DataToJsonConverterFactory converterFactory;
	JsonBuilderFactorySpy jsonBuilderFactorySpy;
	String baseURL;
	DataRecordLinkOldSpy dataRecordLink;

	@BeforeMethod
	public void beforeMethod() {
		baseURL = "https://somesystem.org/rest/records/";
		dataRecordLink = new DataRecordLinkOldSpy("someNameInData");

		jsonBuilderFactorySpy = new JsonBuilderFactorySpy();
		converterFactory = new DataToJsonConverterFactorySpy();
		recordLinkToJsonConverter = DataRecordLinkToJsonConverter
				.usingConverterFactoryAndJsonBuilderFactoryAndDataRecordLinkAndBaseUrl(
						converterFactory, jsonBuilderFactorySpy, dataRecordLink, baseURL);
	}

	@Test
	public void testConverterFactorySetInParent() {
		assertSame(recordLinkToJsonConverter.converterFactory, converterFactory);
	}

	@Test
	public void testRecordLinkConverterExtendsGroupConverter() {
		assertTrue(recordLinkToJsonConverter instanceof DataToJsonConverter);
		assertTrue(recordLinkToJsonConverter instanceof DataGroupToJsonConverter);
	}

	@Test
	public void testNoActions() {
		recordLinkToJsonConverter.hookForSubclassesToImplementExtraConversion();

		JsonObjectBuilderSpy jsonObjectBuilderSpy = (JsonObjectBuilderSpy) jsonBuilderFactorySpy.MCR
				.getReturnValue("createObjectBuilder", 0);

		jsonObjectBuilderSpy.MCR.assertMethodNotCalled("addKeyJsonObjectBuilder");
	}

	@Test
	public void testActionLinksBuilderAddedToMainBuilder() {
		dataRecordLink.MRV.setDefaultReturnValuesSupplier("hasReadAction", () -> true);

		recordLinkToJsonConverter.hookForSubclassesToImplementExtraConversion();

		JsonObjectBuilderSpy mainBuilderSpy = (JsonObjectBuilderSpy) jsonBuilderFactorySpy.MCR
				.getReturnValue("createObjectBuilder", 0);
		JsonObjectBuilderSpy actionLinksBuilderSpy = getActionsBuilder();

		mainBuilderSpy.MCR.assertParameters("addKeyJsonObjectBuilder", 0, "actionLinks",
				actionLinksBuilderSpy);

	}

	private JsonObjectBuilderSpy getActionsBuilder() {
		return (JsonObjectBuilderSpy) jsonBuilderFactorySpy.MCR
				.getReturnValue("createObjectBuilder", 1);
	}

	@Test
	public void testActionAddedToActionBuilder() {
		dataRecordLink.MRV.setDefaultReturnValuesSupplier("hasReadAction", () -> true);

		recordLinkToJsonConverter.hookForSubclassesToImplementExtraConversion();

		JsonObjectBuilderSpy actionLinksBuilderSpy = getActionsBuilder();
		JsonObjectBuilderSpy internalLinkBuilderSpy = (JsonObjectBuilderSpy) jsonBuilderFactorySpy.MCR
				.getReturnValue("createObjectBuilder", 2);

		actionLinksBuilderSpy.MCR.assertParameters("addKeyJsonObjectBuilder", 0, "read",
				internalLinkBuilderSpy);
		internalLinkBuilderSpy.MCR.assertParameters("addKeyString", 0, "rel", "read");
		internalLinkBuilderSpy.MCR.assertParameters("addKeyString", 1, "url", baseURL
				+ dataRecordLink.getLinkedRecordType() + "/" + dataRecordLink.getLinkedRecordId());
		internalLinkBuilderSpy.MCR.assertParameters("addKeyString", 2, "requestMethod", "GET");
		internalLinkBuilderSpy.MCR.assertParameters("addKeyString", 3, "accept",
				"application/vnd.cora.record+json");
		internalLinkBuilderSpy.MCR.assertNumberOfCallsToMethod("addKeyString", 4);
	}

	@Test
	public void testLinkedRecord() {
		DataGroupSpy linkedDataGroup = createDataRecordWithOneLinkWithReadActionAndLinkedRecord();
		dataRecordLink.MRV.setDefaultReturnValuesSupplier("getLinkedRecord",
				() -> Optional.of(linkedDataGroup));

		recordLinkToJsonConverter.hookForSubclassesToImplementExtraConversion();

		fail();
		// TODO: define how it must look like the linkedrecord
		// {
		// "children": [
		// {
		// "name": "linkedRecordType",
		// "value": "recordType"
		// },
		// {
		// "name": "linkedRecordId",
		// "value": "example"
		// }
		// ],
		// "actionLinks": {
		// "read": {
		// "requestMethod": "GET",
		// "rel": "read",
		// "url": "https://cora.epc.ub.uu.se/systemone/rest/record/recordType/example",
		// "accept": "application/vnd.cora.record+json"
		// }
		// },
		// "name": "type",
		// "attributes": {
		// "_no": "",
		// "_sv": "Posttyp",
		// "_en": "RecordType"
		// }
		// }
	}

	private DataGroupSpy createDataRecordWithOneLinkWithReadActionAndLinkedRecord() {
		DataGroupSpy linkedDataGroup = createGroupWithNameInDataAndChildren("linkedSomeRecord");

		DataRecordLinkSpy dataRecordLink = createRecordLink("someLinkNameInData", "someType",
				"someId");
		dataRecordLink.MRV.setDefaultReturnValuesSupplier("hasReadAction", () -> true);
		dataRecordLink.MRV.setDefaultReturnValuesSupplier("getLinkedRecord",
				() -> Optional.of(linkedDataGroup));

		return createGroupWithNameInDataAndChildren("recordToRecordLink", dataRecordLink);
	}

	private DataGroupSpy createDataRecordWithOneLinkWithReadActionAndLinkedRecordWithAttributes() {
		DataGroupSpy linkedDataGroup = createGroupWithNameInDataAndChildren("linkedSomeRecord");

		DataAttributeSpy attribute01 = createAttribute("attribute01", "value01");
		DataAttributeSpy attribute02 = createAttribute("attribute02", "value02");
		addAttributesToDataGroup(linkedDataGroup, attribute01, attribute02);

		DataRecordLinkSpy dataRecordLink = createRecordLink("someLinkNameInData", "someType",
				"someId");
		dataRecordLink.MRV.setDefaultReturnValuesSupplier("hasReadAction", () -> true);
		dataRecordLink.MRV.setDefaultReturnValuesSupplier("getLinkedRecord",
				() -> Optional.of(linkedDataGroup));
		return createGroupWithNameInDataAndChildren("recordToRecordLink", dataRecordLink);
	}

	private DataGroupSpy createDataRecordWithOneLinkWithReadActionAndLinkedRecordWithChildren() {
		DataGroupSpy groupChild01 = createGroupWithNameInData("groupChild01");
		DataGroupSpy groupChild02 = createGroupWithNameInData("groupChild02");
		DataGroupSpy linkedDataGroup = createGroupWithNameInDataAndChildren("linkedSomeRecord",
				groupChild01, groupChild02);

		DataRecordLinkSpy dataRecordLink = createRecordLink("someLinkNameInData", "someType",
				"someId");
		dataRecordLink.MRV.setDefaultReturnValuesSupplier("hasReadAction", () -> true);
		dataRecordLink.MRV.setDefaultReturnValuesSupplier("getLinkedRecord",
				() -> Optional.of(linkedDataGroup));

		return createGroupWithNameInDataAndChildren("recordToRecordLink", dataRecordLink);
	}

	private void addAttributesToDataGroup(DataGroupSpy dataGroup, DataAttributeSpy... attributes) {
		dataGroup.MRV.setDefaultReturnValuesSupplier("getAttributes",
				() -> Arrays.asList(attributes));
		dataGroup.MRV.setDefaultReturnValuesSupplier("hasAttributes", () -> true);
	}

	private DataAttributeSpy createAttribute(String nameInData, String value) {
		DataAttributeSpy attribute = new DataAttributeSpy();
		attribute.MRV.setDefaultReturnValuesSupplier("getNameInData", () -> nameInData);
		attribute.MRV.setDefaultReturnValuesSupplier("getValue", () -> value);
		return attribute;
	}

	private DataGroupSpy createGroupWithNameInData(String nameInData) {
		DataGroupSpy linkedDataGroup = new DataGroupSpy();
		linkedDataGroup.MRV.setDefaultReturnValuesSupplier("getNameInData", () -> nameInData);
		return linkedDataGroup;
	}

	private DataGroupSpy createGroupWithNameInDataAndChildren(String nameInData,
			DataChild... dataRecordLink) {
		DataGroupSpy dataGroup = new DataGroupSpy();
		dataGroup.MRV.setDefaultReturnValuesSupplier("getNameInData", () -> nameInData);
		dataGroup.MRV.setDefaultReturnValuesSupplier("getChildren",
				() -> Arrays.asList(dataRecordLink));
		return dataGroup;
	}

	private DataRecordLinkSpy createRecordLink(String linkedName, String linkedType,
			String linkToId) {
		DataRecordLinkSpy dataRecordLink = new DataRecordLinkSpy();
		dataRecordLink.MRV.setDefaultReturnValuesSupplier("getNameInData", () -> linkedName);
		dataRecordLink.MRV.setDefaultReturnValuesSupplier("getLinkedRecordType", () -> linkedType);
		dataRecordLink.MRV.setDefaultReturnValuesSupplier("getLinkedRecordId", () -> linkToId);
		return dataRecordLink;
	}

}
