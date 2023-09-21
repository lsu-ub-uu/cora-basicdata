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
		String json = """
				{
				  "name": "master",
				  "mimeType": "application/vnd.uub.record+json"
				}
				""";
		CoraDataResourceLink resourceLink = getConverterdLink(json);

		assertEquals(resourceLink.getNameInData(), "master");
		assertEquals(resourceLink.getMimeType(), "application/vnd.uub.record+json");
	}

	private CoraDataResourceLink getConverterdLink(String json) {
		OrgJsonParser jsonParser = new OrgJsonParser();
		JsonValue jsonValue = jsonParser.parseString(json);
		JsonToDataResourceLinkConverter converter = JsonToDataResourceLinkConverter
				.forJsonObject((JsonObject) jsonValue);

		CoraDataResourceLink dataLink = (CoraDataResourceLink) converter.toInstance();
		return dataLink;
	}

	@Test
	public void testToClassWithRepeatId() {
		String json = """
				{
				  "name": "master",
				  "mimeType": "application/vnd.uub.record+json",
				  "repeatId":"0"
				}
				""";

		DataLink dataLink = getConverterdLink(json);

		assertEquals(dataLink.getNameInData(), "master");
		assertEquals(dataLink.getRepeatId(), "0");
	}

	@Test(expectedExceptions = JsonParseException.class, expectedExceptionsMessageRegExp = ""
			+ "Error parsing jsonObject: ResourceLink must contain name, mimeType and repeatId.")
	public void testExceptionMimeTypeNotExist() {
		String json = """
				{
				  "name": "master",
				  "repeatId":"0"
				}
				""";
		getConverterdLink(json);
	}

	@Test(expectedExceptions = JsonParseException.class, expectedExceptionsMessageRegExp = ""
			+ "Error parsing jsonObject: ResourceLink must contain name, mimeType and repeatId.")
	public void testExceptionNameNotExist() {
		String json = """
				{
				  "mimeType": "application/vnd.uub.record+json",
				  "repeatId":"0"
				}
				""";
		getConverterdLink(json);
	}

	@Test(expectedExceptions = JsonParseException.class, expectedExceptionsMessageRegExp = ""
			+ "Error parsing jsonObject: ResourceLink must contain name, mimeType and repeatId.")
	public void testExceptionIfTooManyFields() {
		String json = """
				{
				  "name": "master",
				  "mimeType": "application/vnd.uub.record+json",
				  "repeatId":"0",
				  "someOther": "someOther"
				}
				""";
		getConverterdLink(json);
	}

	@Test(expectedExceptions = JsonParseException.class, expectedExceptionsMessageRegExp = ""
			+ "Error parsing jsonObject: ResourceLink must contain name, mimeType and repeatId.")
	public void testExceptionIfTooManyFields2() {
		String json = """
				{
				  "name": "master",
				  "mimeType": "application/vnd.uub.record+json",
				  "someOther": "someOther"
				}
				""";
		getConverterdLink(json);
	}

	@Test(expectedExceptions = JsonParseException.class, expectedExceptionsMessageRegExp = ""
			+ "Error parsing jsonObject: ResourceLink must contain name, mimeType and repeatId.")
	public void testExceptionIfTooManyFields3() {
		String json = """
				{
				  "nameSpecial": "master",
				  "mimeType": "application/vnd.uub.record+json",
				  "someOther": "someOther"
				}
				""";
		getConverterdLink(json);
	}
}
