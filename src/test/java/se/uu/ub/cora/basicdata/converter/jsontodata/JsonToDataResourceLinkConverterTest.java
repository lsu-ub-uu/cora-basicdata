/*
 * Copyright 2019, 2025 Uppsala University Library
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

import org.testng.annotations.DataProvider;
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
				  "children": [
				    {"name": "linkedRecordType", "value": "someType"                  },
				    {"name": "linkedRecordId",   "value": "someId"},
				    {"name": "mimeType",           "value": "image/png"               }
				  ]
				}
				""";
		CoraDataResourceLink resourceLink = getConverterdLink(json);

		assertEquals(resourceLink.getNameInData(), "master");
		assertEquals(resourceLink.getType(), "someType");
		assertEquals(resourceLink.getId(), "someId");
		assertEquals(resourceLink.getMimeType(), "image/png");
	}

	private CoraDataResourceLink getConverterdLink(String json) {
		OrgJsonParser jsonParser = new OrgJsonParser();
		JsonValue jsonValue = jsonParser.parseString(json);
		JsonToDataResourceLinkConverter converter = JsonToDataResourceLinkConverter
				.forJsonObject((JsonObject) jsonValue);

		return (CoraDataResourceLink) converter.toInstance();
	}

	@Test
	public void testToClassWithRepeatId() {
		String json = """
				{
				  "name": "master",
				  "children": [
				    {"name": "linkedRecordType", "value": "someType"                  },
				    {"name": "linkedRecordId",   "value": "someId"},
				    {"name": "mimeType",           "value": "image/png"               }
				  ],
				  "repeatId":"0"
				}
				""";

		DataLink dataLink = getConverterdLink(json);

		assertEquals(dataLink.getNameInData(), "master");
		assertEquals(dataLink.getRepeatId(), "0");
	}

	@Test(dataProvider = "testValidJson", expectedExceptions = JsonParseException.class, expectedExceptionsMessageRegExp = ""
			+ "Error parsing jsonObject: ResourceLink must contain name,children\\[linkedRecordType,linkedRecordId,mimeType\\] and repeatId\\.")
	public void testValidJson(String json) {
		getConverterdLink(json);
	}

	@DataProvider(name = "testValidJson")
	public Object[][] testExceptionMimeTypeNotExist2() {
		String json0 = jsonWithOutNameInData();
		String json1 = jsonWithOutChildren();
		String json2 = jsonTooManyFields();
		String json3 = jsonTooManyFields_repeatIdMissing();
		String json4 = jsonTooManyFields_NameMissingAndRepeatIdMissing();
		String json5 = jsonMimeTypeMissing();
		String json6 = jsonLinkedRecordId();
		String json7 = jsonExtraChild();

		return new Object[][] { { json0 }, { json1 }, { json2 }, { json3 }, { json4 }, { json5 },
				{ json6 }, { json7 } };
	}

	private String jsonWithOutNameInData() {
		return """
					{
					"children": [
					             {"name": "linkedRecordType", "value": "someType"                  },
					             {"name": "linkedRecordId",   "value": "someId"},
					             {"name": "mimeType",           "value": "image/png"               }
					             ],
					"repeatId":"0"
						}
				""";
	}

	private String jsonWithOutChildren() {
		return """
				{
				  "name": "master",
				  "repeatId":"0"
				}
				""";
	}

	private String jsonTooManyFields() {
		return """
				{
				  "name": "master",
					"children": [
					             {"name": "linkedRecordType", "value": "someType"                  },
					             {"name": "linkedRecordId",   "value": "someId"},
					             {"name": "mimeType",           "value": "image/png"               }
					             ],
					"repeatId":"0",
					"someOther": "someOther"
				}
				""";
	}

	private String jsonTooManyFields_repeatIdMissing() {
		return """
				{
				  "name": "master",
					"children": [
					             {"name": "linkedRecordType", "value": "someType"                  },
					             {"name": "linkedRecordId",   "value": "someId"},
					             {"name": "mimeType",           "value": "image/png"               }
					             ],
					 "someOther": "someOther"
				}
				""";
	}

	private String jsonTooManyFields_NameMissingAndRepeatIdMissing() {
		return """
				{
				  "nameSpecial": "master",
					"children": [
					             {"name": "linkedRecordType", "value": "someType"                  },
					             {"name": "linkedRecordId",   "value": "someId"},
					             {"name": "mimeType",           "value": "image/png"               }
					             ],
					 "someOther": "someOther"
				}
				""";
	}

	private String jsonMimeTypeMissing() {
		return """
					{
					"name": "master",
					"children": [
					             {"name": "linkedRecordType", "value": "someType"                  },
					             {"name": "linkedRecordId",   "value": "someId"}
					             ],
					"repeatId":"0"
						}
				""";
	}

	private String jsonLinkedRecordId() {
		return """
					{
					"name": "master",
					"children": [
					             {"name": "linkedRecordType", "value": "someType"                  },
					             {"name": "someOtherId",   "value": "someId"},
					              {"name": "mimeType",           "value": "image/png"               }
					             ],
					"repeatId":"0"
						}
				""";
	}

	private String jsonExtraChild() {
		return """
					{
					"name": "master",
					"children": [
					             {"name": "linkedRecordType", "value": "someType"                  },
					             {"name": "linkedRecordId",   "value": "someId"},
					             {"name": "mimeType",           "value": "image/png"               },
					             {"name": "another",           "value": "anotherValue"               }
					             ],
					"repeatId":"0"
						}
				""";
	}

}
