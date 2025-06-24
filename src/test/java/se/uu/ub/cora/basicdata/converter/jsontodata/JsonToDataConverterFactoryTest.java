/*
 * Copyright 2015, 2025 Uppsala University Library
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

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.converter.JsonToDataConverter;
import se.uu.ub.cora.data.converter.JsonToDataConverterFactory;
import se.uu.ub.cora.json.parser.JsonObject;
import se.uu.ub.cora.json.parser.JsonParseException;
import se.uu.ub.cora.json.parser.JsonParser;
import se.uu.ub.cora.json.parser.JsonValue;
import se.uu.ub.cora.json.parser.org.OrgJsonParser;

public class JsonToDataConverterFactoryTest {
	private JsonToDataConverterFactory jsonToDataConverterFactory;
	private JsonParser jsonParser;

	@BeforeMethod
	public void beforeMethod() {
		jsonToDataConverterFactory = new JsonToDataConverterFactoryImp();
		jsonParser = new OrgJsonParser();
	}

	@Test
	public void testFactorOnJsonStringDataGroupEmptyChildren() {
		String json = "{\"name\":\"groupNameInData\", \"children\":[]}";
		JsonValue jsonValue = jsonParser.parseString(json);
		JsonToDataConverter jsonToDataConverter = jsonToDataConverterFactory
				.createForJsonObject(jsonValue);
		assertTrue(jsonToDataConverter instanceof JsonToDataGroupConverter);
	}

	@Test
	public void testFactorOnJsonStringDataGroupAtomicChild() {
		String json = "{\"name\":\"id\",\"children\":[{\"name\":\"someNameInData\",\"value\":\"id2\"}]}";
		JsonValue jsonValue = jsonParser.parseString(json);
		JsonToDataConverter jsonToDataConverter = jsonToDataConverterFactory
				.createForJsonObject(jsonValue);
		assertTrue(jsonToDataConverter instanceof JsonToDataGroupConverter);
	}

	@Test
	public void testFactorOnJsonStringOnlyRecordTypeFactorsGroupConverter() {
		String json = "{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"recordType\"},{\"name\":\"NOTlinkedRecordId\",\"value\":\"place\"}],\"name\":\"type\"}";
		JsonValue jsonValue = jsonParser.parseString(json);
		JsonToDataConverter jsonToDataConverter = jsonToDataConverterFactory
				.createForJsonObject(jsonValue);
		assertTrue(jsonToDataConverter instanceof JsonToDataGroupConverter);
	}

	@Test
	public void testFactorOnJsonStringOnlyRecordIdFactorsGroupConverter() {
		String json = "{\"children\":[{\"name\":\"NOTlinkedRecordType\",\"value\":\"recordType\"},{\"name\":\"linkedRecordId\",\"value\":\"place\"}],\"name\":\"type\"}";
		JsonValue jsonValue = jsonParser.parseString(json);
		JsonToDataConverter jsonToDataConverter = jsonToDataConverterFactory
				.createForJsonObject(jsonValue);
		assertTrue(jsonToDataConverter instanceof JsonToDataGroupConverter);
	}

	@Test
	public void testFactorOnJsonStringRemovesActionLinkFromGroup() {
		String json = "{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"cora\"}],\"actionLinks\":{\"read\":{\"requestMethod\":\"GET\",\"rel\":\"read\",\"url\":\"http://localhost:8080/systemone/rest/record/system/cora\",\"accept\":\"application/vnd.uub.record+json\"}},\"name\":\"dataDivider\"}";
		JsonValue jsonValue = jsonParser.parseString(json);
		JsonToDataGroupConverter jsonToDataConverter = (JsonToDataGroupConverter) jsonToDataConverterFactory
				.createForJsonObject(jsonValue);
		JsonObject converterJsonObject = jsonToDataConverter.onlyForTestGetJsonObject();
		assertFalse(converterJsonObject.toJsonFormattedString().contains("actionLinks"));
	}

	@Test
	public void testFactorOnJsonStringNoLinkedPathFactorsDataRecordLink() {
		String json = "{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"recordType\"},{\"name\":\"linkedRecordId\",\"value\":\"place\"}],\"name\":\"type\"}";

		JsonValue jsonValue = jsonParser.parseString(json);
		JsonToDataConverter jsonToDataConverter = jsonToDataConverterFactory
				.createForJsonObject(jsonValue);
		assertTrue(jsonToDataConverter instanceof JsonToDataRecordLinkConverter);
	}

	@Test
	public void testFactorOnJsonStringWithLinkedPathFactorsDataRecordLink() {
		String json = "{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"coraText\"},{\"name\":\"linkedRecordId\",\"value\":\"exampleGroupText\"},{\"children\":[{\"name\":\"nameInData\",\"value\":\"recordInfo\"},{\"children\":[{\"name\":\"nameInData\",\"value\":\"type\"}],\"name\":\"linkedPath\"}],\"name\":\"linkedPath\"}],\"name\":\"from\"}";
		JsonValue jsonValue = jsonParser.parseString(json);
		JsonToDataConverter jsonToDataConverter = jsonToDataConverterFactory
				.createForJsonObject(jsonValue);
		assertTrue(jsonToDataConverter instanceof JsonToDataRecordLinkConverter);
	}

	@Test
	public void testFactorOnJsonStringWithLinkedRepeatIdFactorsDataRecordLink() {
		String json = "{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"coraText\"},{\"name\":\"linkedRecordId\",\"value\":\"exampleGroupText\"},{\"name\":\"linkedRepeatId\",\"value\":\"one\"}],\"name\":\"from\"}";
		JsonValue jsonValue = jsonParser.parseString(json);
		JsonToDataConverter jsonToDataConverter = jsonToDataConverterFactory
				.createForJsonObject(jsonValue);
		assertTrue(jsonToDataConverter instanceof JsonToDataRecordLinkConverter);
	}

	@Test
	public void testFactorOnJsonStringWithLinkedRepeatIdAndLinkedPathFactorsDataRecordLink() {
		String json = "{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"coraText\"},{\"name\":\"linkedRecordId\",\"value\":\"exampleGroupText\"},{\"name\":\"linkedRepeatId\",\"value\":\"one\"},{\"children\":[{\"name\":\"nameInData\",\"value\":\"recordInfo\"},{\"children\":[{\"name\":\"nameInData\",\"value\":\"type\"}],\"name\":\"linkedPath\"}],\"name\":\"linkedPath\"}],\"name\":\"from\"}";
		JsonValue jsonValue = jsonParser.parseString(json);
		JsonToDataConverter jsonToDataConverter = jsonToDataConverterFactory
				.createForJsonObject(jsonValue);
		assertTrue(jsonToDataConverter instanceof JsonToDataRecordLinkConverter);
	}

	@Test
	public void testFactorOnJsonStringWithLinkedPathButNotLinkedRecordTypeFactorsDataGroup() {
		String json = "{\"children\":[{\"name\":\"NOTlinkedRecordType\",\"value\":\"coraText\"},{\"name\":\"linkedRecordId\",\"value\":\"exampleGroupText\"},{\"children\":[{\"name\":\"nameInData\",\"value\":\"recordInfo\"},{\"children\":[{\"name\":\"nameInData\",\"value\":\"type\"}],\"name\":\"linkedPath\"}],\"name\":\"linkedPath\"}],\"name\":\"from\"}";
		JsonValue jsonValue = jsonParser.parseString(json);
		JsonToDataConverter jsonToDataConverter = jsonToDataConverterFactory
				.createForJsonObject(jsonValue);
		assertTrue(jsonToDataConverter instanceof JsonToDataGroupConverter);
		assertFalse(jsonToDataConverter instanceof JsonToDataRecordLinkConverter);
	}

	@Test
	public void testFactorOnJsonStringWithLinkedPathButNotLinkedRecordIdFactorsDataGroup() {
		String json = "{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"coraText\"},{\"name\":\"NOTlinkedRecordId\",\"value\":\"exampleGroupText\"},{\"children\":[{\"name\":\"nameInData\",\"value\":\"recordInfo\"},{\"children\":[{\"name\":\"nameInData\",\"value\":\"type\"}],\"name\":\"linkedPath\"}],\"name\":\"linkedPath\"}],\"name\":\"from\"}";
		JsonValue jsonValue = jsonParser.parseString(json);
		JsonToDataConverter jsonToDataConverter = jsonToDataConverterFactory
				.createForJsonObject(jsonValue);
		assertTrue(jsonToDataConverter instanceof JsonToDataGroupConverter);
		assertFalse(jsonToDataConverter instanceof JsonToDataRecordLinkConverter);
	}

	@Test
	public void testFactorOnJsonStringWithThreeChildrenButNoLinkedRepatIdAndNoLinkedPathFactorsDataGroup() {
		String json = "{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"coraText\"},{\"name\":\"linkedRecordId\",\"value\":\"exampleGroupText\"},{\"children\":[{\"name\":\"nameInData\",\"value\":\"recordInfo\"},{\"children\":[{\"name\":\"nameInData\",\"value\":\"type\"}],\"name\":\"linkedPath\"}],\"name\":\"NOTlinkedPath\"}],\"name\":\"from\"}";
		JsonValue jsonValue = jsonParser.parseString(json);
		JsonToDataConverter jsonToDataConverter = jsonToDataConverterFactory
				.createForJsonObject(jsonValue);
		assertTrue(jsonToDataConverter instanceof JsonToDataGroupConverter);
		assertFalse(jsonToDataConverter instanceof JsonToDataRecordLinkConverter);
	}

	@Test
	public void testFactorOnJsonStringWithMaxNumOfChildrenBUtNOLinkedRepeatIdFactorsDataGroup() {
		String json = "{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"coraText\"},{\"name\":\"linkedRecordId\",\"value\":\"exampleGroupText\"},{\"name\":\"NOTlinkedRepeatId\",\"value\":\"one\"},{\"children\":[{\"name\":\"nameInData\",\"value\":\"recordInfo\"},{\"children\":[{\"name\":\"nameInData\",\"value\":\"type\"}],\"name\":\"linkedPath\"}],\"name\":\"linkedPath\"}],\"name\":\"from\"}";
		JsonValue jsonValue = jsonParser.parseString(json);
		JsonToDataConverter jsonToDataConverter = jsonToDataConverterFactory
				.createForJsonObject(jsonValue);
		assertTrue(jsonToDataConverter instanceof JsonToDataGroupConverter);
		assertFalse(jsonToDataConverter instanceof JsonToDataRecordLinkConverter);
	}

	@Test
	public void testFactorOnJsonStringWithMaxNumOfChildrenBUtNOLinkedPathIdFactorsDataGroup() {
		String json = "{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"coraText\"},{\"name\":\"linkedRecordId\",\"value\":\"exampleGroupText\"},{\"name\":\"linkedRepeatId\",\"value\":\"one\"},{\"children\":[{\"name\":\"nameInData\",\"value\":\"recordInfo\"},{\"children\":[{\"name\":\"nameInData\",\"value\":\"type\"}],\"name\":\"linkedPath\"}],\"name\":\"NOTlinkedPath\"}],\"name\":\"from\"}";
		JsonValue jsonValue = jsonParser.parseString(json);
		JsonToDataConverter jsonToDataConverter = jsonToDataConverterFactory
				.createForJsonObject(jsonValue);
		assertTrue(jsonToDataConverter instanceof JsonToDataGroupConverter);
		assertFalse(jsonToDataConverter instanceof JsonToDataRecordLinkConverter);
	}

	@Test
	public void testFactorOnJsonToDataResourceLink() {
		String json = "{\"children\":[{\"name\":\"streamId\",\"value\":\"soundBinary:18269669168741\"},{\"name\":\"NOTfilename\",\"value\":\"adele.png\"},{\"name\":\"NOTfilesize\",\"value\":\"8\"},{\"name\":\"NOTmimeType\",\"value\":\"application/octet-stream\"}],\"name\":\"master\"}";
		JsonValue jsonValue = jsonParser.parseString(json);
		JsonToDataConverter jsonToDataConverter = jsonToDataConverterFactory
				.createForJsonObject(jsonValue);
		assertTrue(jsonToDataConverter instanceof JsonToDataGroupConverter);
		assertFalse(jsonToDataConverter instanceof JsonToDataResourceLinkConverter);
	}

	@Test
	public void testFactorOnJsonStringOnlyStreamIdFactorsGroupConverter() {
		String json = "{\"children\":[{\"name\":\"streamId\",\"value\":\"soundBinary:18269669168741\"},{\"name\":\"NOTfilename\",\"value\":\"adele.png\"},{\"name\":\"NOTfilesize\",\"value\":\"8\"},{\"name\":\"NOTmimeType\",\"value\":\"application/octet-stream\"}],\"name\":\"master\"}";
		JsonValue jsonValue = jsonParser.parseString(json);
		JsonToDataConverter jsonToDataConverter = jsonToDataConverterFactory
				.createForJsonObject(jsonValue);
		assertTrue(jsonToDataConverter instanceof JsonToDataGroupConverter);
		assertFalse(jsonToDataConverter instanceof JsonToDataResourceLinkConverter);
	}

	@Test
	public void testFactorOnJsonStringNOTStreamIdFactorsGroupConverter() {
		String json = "{\"children\":[{\"name\":\"NOTstreamId\",\"value\":\"soundBinary:18269669168741\"},{\"name\":\"NOTfilename\",\"value\":\"adele.png\"},{\"name\":\"NOTfilesize\",\"value\":\"8\"},{\"name\":\"NOTmimeType\",\"value\":\"application/octet-stream\"}],\"name\":\"master\"}";
		JsonValue jsonValue = jsonParser.parseString(json);
		JsonToDataConverter jsonToDataConverter = jsonToDataConverterFactory
				.createForJsonObject(jsonValue);
		assertTrue(jsonToDataConverter instanceof JsonToDataGroupConverter);
		assertFalse(jsonToDataConverter instanceof JsonToDataResourceLinkConverter);
	}

	@Test
	public void testFactorOnlyStreamIdAndFileNameFactorsGroupConverter() {
		String json = "{\"children\":[{\"name\":\"streamId\",\"value\":\"soundBinary:18269669168741\"},{\"name\":\"filename\",\"value\":\"adele.png\"},{\"name\":\"NOTfilesize\",\"value\":\"8\"},{\"name\":\"NOTmimeType\",\"value\":\"application/octet-stream\"}],\"name\":\"master\"}";
		JsonValue jsonValue = jsonParser.parseString(json);
		JsonToDataConverter jsonToDataConverter = jsonToDataConverterFactory
				.createForJsonObject(jsonValue);
		assertTrue(jsonToDataConverter instanceof JsonToDataGroupConverter);
		assertFalse(jsonToDataConverter instanceof JsonToDataResourceLinkConverter);
	}

	@Test
	public void testFactorOnlyStreamIdAndFileNameAndFileSizeFactorsGroupConverter() {
		String json = "{\"children\":[{\"name\":\"streamId\",\"value\":\"soundBinary:18269669168741\"},{\"name\":\"filename\",\"value\":\"adele.png\"},{\"name\":\"filesize\",\"value\":\"8\"},{\"name\":\"NOTmimeType\",\"value\":\"application/octet-stream\"}],\"name\":\"master\"}";
		JsonValue jsonValue = jsonParser.parseString(json);
		JsonToDataConverter jsonToDataConverter = jsonToDataConverterFactory
				.createForJsonObject(jsonValue);
		assertTrue(jsonToDataConverter instanceof JsonToDataGroupConverter);
		assertFalse(jsonToDataConverter instanceof JsonToDataResourceLinkConverter);
	}

	@Test
	public void testFactorOnJsonStringCompleteSetupFactorsDataResourceLink() {
		String json = """
				{
				  "name": "master",
				  "children": [
				    {"name": "linkedRecordType", "value": "someType"                  },
				    {"name": "linkedRecordId",   "value": "someId"},
				    {"name": "mimeType",           "value": "image/png"               }
				  ],
				  "actionLinks": {
				    "read": {
				      "requestMethod": "GET",
				      "rel": "read",
				      "url": "https://cora.epc.ub.uu.se/systemone/rest/record/someType/someId/master",
				      "accept": "image/png"
				    }
				  }
				}""";
		JsonValue jsonValue = jsonParser.parseString(json);
		JsonToDataConverter jsonToDataConverter = jsonToDataConverterFactory
				.createForJsonObject(jsonValue);

		assertTrue(jsonToDataConverter instanceof JsonToDataResourceLinkConverter);
	}

	@Test
	public void testFactorOnJsonStringCompleteSetupFactorsDataResourceLinkAndRepeatId() {
		String json = """
				{
				  "name": "master",
				  "children": [
				    {"name": "linkedRecordType", "value": "someType"                  },
				    {"name": "linkedRecordId",   "value": "someId"},
				    {"name": "mimeType",           "value": "image/png"               }
				  ],
				  "repeatId":"0",
				  "actionLinks": {
				    "read": {
				      "requestMethod": "GET",
				      "rel": "read",
				      "url": "https://cora.epc.ub.uu.se/systemone/rest/record/someType/someId/master",
				      "accept": "image/png"
				    }
				  }
				}""";
		JsonValue jsonValue = jsonParser.parseString(json);
		JsonToDataConverter jsonToDataConverter = jsonToDataConverterFactory
				.createForJsonObject(jsonValue);

		assertTrue(jsonToDataConverter instanceof JsonToDataResourceLinkConverter);
	}

	@Test
	public void testFactorOnJsonStringRemovesActionLinksFromResourceLinks() {
		String json = """
				{
				  "name": "master",
				  "children": [
				    {"name": "linkedRecordType", "value": "someType"                  },
				    {"name": "linkedRecordId",   "value": "someId"},
				    {"name": "mimeType",           "value": "image/png"               }
				  ],
				  "actionLinks": {
				    "read": {
				      "requestMethod": "GET",
				      "rel": "read",
				      "url": "https://cora.epc.ub.uu.se/systemone/rest/record/someType/someId/master",
				      "accept": "image/png"
				    }
				  }
				}""";
		JsonValue jsonValue = jsonParser.parseString(json);
		JsonToDataResourceLinkConverter jsonToDataConverter = (JsonToDataResourceLinkConverter) jsonToDataConverterFactory
				.createForJsonObject(jsonValue);
		JsonObject converterJsonObject = jsonToDataConverter.onlyForTestGetJsonObject();
		assertFalse(converterJsonObject.toJsonFormattedString().contains("actionLinks"));
	}

	@Test
	public void testFactorOnJsonStringDataAtomic() {
		String json = "{\"name\":\"atomicNameInData\",\"value\":\"atomicValue\"}";
		JsonValue jsonValue = jsonParser.parseString(json);
		JsonToDataConverter jsonToDataConverter = jsonToDataConverterFactory
				.createForJsonObject(jsonValue);
		assertTrue(jsonToDataConverter instanceof JsonToDataAtomicConverter);
	}

	@Test
	public void testFactorOnJsonStringDataAttribute() {
		String json = "{\"attributeNameInData\":\"attributeValue\"}";
		JsonValue jsonValue = jsonParser.parseString(json);
		JsonToDataConverter jsonToDataConverter = jsonToDataConverterFactory
				.createForJsonObject(jsonValue);
		assertTrue(jsonToDataConverter instanceof JsonToDataAttributeConverter);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testFactorOnJsonObjectNullJson() {
		jsonToDataConverterFactory.createForJsonObject(null);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testClassCreatorGroupNotAGroup() {
		String json = "[{\"id\":{\"id2\":\"value\"}}]";
		JsonValue jsonValue = jsonParser.parseString(json);
		jsonToDataConverterFactory.createForJsonObject(jsonValue);
	}

}
