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

import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.basicdata.data.DataResourceLinkSpy;
import se.uu.ub.cora.data.converter.DataToJsonConverter;

public class DataResourceLinkToJsonConverterTest {

	DataResourceLinkToJsonConverter converter;
	JsonBuilderFactorySpy jsonBuilderFactorySpy;
	String recordURL;
	private DataResourceLinkToJsonConverterForTest forTest;
	// private String childrenJsonString = "\"children\":[" +
	// "{\"name\":\"streamId\",\"value\":\"aStreamId\"}"
	// + ",{\"name\":\"filename\",\"value\":\"aFilename\"}"
	// + ",{\"name\":\"filesize\",\"value\":\"12345\"}"
	// + ",{\"name\":\"mimeType\",\"value\":\"application/png\"}]";

	@BeforeMethod
	public void beforeMethod() {

		DataResourceLinkSpy dataResourceLink = new DataResourceLinkSpy("someNameInData");

		jsonBuilderFactorySpy = new JsonBuilderFactorySpy();
		// jsonBuilderFactorySpy = new OrgJsonBuilderFactoryAdapter();

		// dataResourceLink = new DataResourceLinkSpy("master");
		// dataResourceLink.addChild(new DataAtomicSpy("streamId", "aStreamId"));
		// dataResourceLink.addChild(new DataAtomicSpy("mimeType", "application/png"));

		converter = new DataResourceLinkToJsonConverter(dataResourceLink, recordURL,
				jsonBuilderFactorySpy);

		forTest = new DataResourceLinkToJsonConverterForTest(dataResourceLink, recordURL,
				jsonBuilderFactorySpy);

	}

	@Test
	public void testResourceLinkConverterExtendsGroupConverter() throws Exception {
		assertTrue(converter instanceof DataGroupToJsonConverter);
		assertTrue(converter instanceof DataToJsonConverter);
	}

	@Test
	public void testToJsonCallsMethodsInSuperClass() throws Exception {
		// forTest.toJson();
		// assertTrue(forTest.dataGroupToJsonHasBeenCalled);
		//
		// assertTrue(forTest.addChildrenToGroupHasBeenCalled);
	}

	@Test
	public void testInit() throws Exception {

	}

	// @Test
	// public void testCallToJson() throws Exception {
	// String json = converter.toJson();
	// assertEquals(json, "{\"name\":\"nameInData\"}");
	// // assertEquals(json, "{" + childrenJsonString + ",\"name\":\"nameInData\"}");
	// }
}
