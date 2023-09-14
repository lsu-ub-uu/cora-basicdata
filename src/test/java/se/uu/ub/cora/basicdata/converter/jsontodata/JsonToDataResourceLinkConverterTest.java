/*
 * Copyright 2019 Uppsala University Library
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

package se.uu.ub.cora.basicdata.converter.jsontodata;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import se.uu.ub.cora.basicdata.data.CoraDataResourceLink;
import se.uu.ub.cora.data.DataLink;
import se.uu.ub.cora.json.parser.JsonObject;
import se.uu.ub.cora.json.parser.JsonParseException;
import se.uu.ub.cora.json.parser.JsonValue;
import se.uu.ub.cora.json.parser.org.OrgJsonParser;

public class JsonToDataResourceLinkConverterTest {

	@Test
	public void testToInstance() {
		String json = "{\"children\":[],\"name\":\"someResourceLink\"}";
		CoraDataResourceLink resourceLink = (CoraDataResourceLink) getConverterdLink(json);
		assertEquals(resourceLink.getNameInData(), "someResourceLink");
	}

	@Test
	public void testName() throws Exception {
		String json = "{\n" + "  \"children\": [\n" + "    {\n"
				+ "      \"name\": \"resourceId\",\n"
				+ "      \"value\": \"binary:3216882709379211\"\n" + "    },\n" + "    {\n"
				+ "      \"name\": \"resourceLink\"\n" + "    },\n" + "    {\n"
				+ "      \"name\": \"fileSize\",\n" + "      \"value\": \"110104\"\n" + "    },\n"
				+ "    {\n" + "      \"name\": \"mimeType\",\n"
				+ "      \"value\": \"application/octet-stream\"\n" + "    }\n" + "  ],\n"
				+ "  \"name\": \"master\"\n" + "}";
		CoraDataResourceLink resourceLink = (CoraDataResourceLink) getConverterdLink(json);
	}

	private DataLink getConverterdLink(String json) {
		OrgJsonParser jsonParser = new OrgJsonParser();
		JsonValue jsonValue = jsonParser.parseString(json);
		JsonToDataResourceLinkConverter converter = JsonToDataResourceLinkConverter
				.forJsonObject((JsonObject) jsonValue);

		DataLink dataLink = (DataLink) converter.toInstance();
		return dataLink;
	}

	@Test
	public void testToClassWithRepeatId() {
		String json = "{\"children\":[],\"repeatId\":\"0\",\"name\":\"someResourceLink\"}";
		DataLink dataLink = getConverterdLink(json);
		assertEquals(dataLink.getNameInData(), "someResourceLink");
		assertEquals(dataLink.getRepeatId(), "0");
	}

	@Test
	public void testToClassWithAttribute() {
		String json = "{\"children\":[],\"attributes\":{\"type\":\"someType\"},\"name\":\"someResourceLink\"}";
		DataLink dataLink = getConverterdLink(json);

		assertEquals(dataLink.getNameInData(), "someResourceLink");
		String attributeValue = dataLink.getAttribute("type").getValue();
		assertEquals(attributeValue, "someType");
	}

	@Test
	public void testToClassWithRepeatIdAndAttribute() {
		String json = "{\"children\":[{\"name\":\"streamId\",\"value\":\"aStreamId\"},{\"name\":\"filename\",\"value\":\"aFilename\"},{\"name\":\"filesize\",\"value\":\"12345\"},{\"name\":\"mimeType\",\"value\":\"application/png\"}],\"repeatId\":\"0\",\"attributes\":{\"type\":\"someType\"},\"name\":\"someResourceLink\"}";
		DataLink dataLink = getConverterdLink(json);

		assertEquals(dataLink.getNameInData(), "someResourceLink");
		String attributeValue = dataLink.getAttribute("type").getValue();
		assertEquals(attributeValue, "someType");
		assertEquals(dataLink.getRepeatId(), "0");
	}

	@Test(expectedExceptions = JsonParseException.class, expectedExceptionsMessageRegExp = ""
			+ "Error parsing jsonObject: Group data can only contain keys: name, children and attributes")
	public void testToClassWithRepeatIdAndAttributeAndExtra() {
		String json = "{\"children\":[{\"name\":\"streamId\",\"value\":\"aStreamId\"},{\"name\":\"filename\",\"value\":\"aFilename\"},{\"name\":\"filesize\",\"value\":\"12345\"},{\"name\":\"mimeType\",\"value\":\"application/png\"}],\"repeatId\":\"0\",\"attributes\":{\"type\":\"someType\"}, \"extra\":\"extraValue\", \"name\":\"someResourceLink\"}";
		getConverterdLink(json);
	}

	@Test(expectedExceptions = JsonParseException.class, expectedExceptionsMessageRegExp = ""
			+ "Error parsing jsonObject: Group data must contain name and children, and may contain attributes or repeatId")
	public void testToClassWithIncorrectAttributeNameInData() {
		String json = "{\"children\":[{\"name\":\"streamId\",\"value\":\"aStreamId\"},{\"name\":\"filename\",\"value\":\"aFilename\"},{\"name\":\"filesize\",\"value\":\"12345\"},{\"name\":\"mimeType\",\"value\":\"application/png\"}],\"NOTattributes\":{\"type\":\"someType\"},\"name\":\"someResourceLink\"}";
		getConverterdLink(json);
	}

}
