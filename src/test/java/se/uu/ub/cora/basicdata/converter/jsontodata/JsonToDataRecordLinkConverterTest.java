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
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import se.uu.ub.cora.basicdata.converter.jsontodata.JsonToDataRecordLinkConverter;
import se.uu.ub.cora.basicdata.data.CoraDataRecordLink;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataLink;
import se.uu.ub.cora.json.parser.JsonObject;
import se.uu.ub.cora.json.parser.JsonParseException;
import se.uu.ub.cora.json.parser.JsonValue;
import se.uu.ub.cora.json.parser.org.OrgJsonParser;

public class JsonToDataRecordLinkConverterTest {

	@Test
	public void testToInstance() {
		String json = "{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"recordType\"},{\"name\":\"linkedRecordId\",\"value\":\"place\"}],\"name\":\"someLink\"}";
		CoraDataRecordLink dataLink = (CoraDataRecordLink) getConverterdLink(json);
		assertEquals(dataLink.getNameInData(), "someLink");
		assertEquals(dataLink.getFirstAtomicValueWithNameInData("linkedRecordType"), "recordType");
		assertEquals(dataLink.getFirstAtomicValueWithNameInData("linkedRecordId"), "place");

	}

	@Test
	public void testToInstanceWithLinkedRepeatId() {
		String json = "{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"recordType\"},{\"name\":\"linkedRecordId\",\"value\":\"place\"},{\"name\":\"linkedRepeatId\",\"value\":\"one\"}],\"name\":\"someLink\"}";
		CoraDataRecordLink dataLink = (CoraDataRecordLink) getConverterdLink(json);
		assertEquals(dataLink.getNameInData(), "someLink");
		assertEquals(dataLink.getFirstAtomicValueWithNameInData("linkedRecordType"), "recordType");
		assertEquals(dataLink.getFirstAtomicValueWithNameInData("linkedRecordId"), "place");
		assertEquals(dataLink.getFirstAtomicValueWithNameInData("linkedRepeatId"), "one");

	}

	@Test
	public void testToInstanceWithLinkedPath() {
		String json = "{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"coraText\"},{\"name\":\"linkedRecordId\",\"value\":\"exampleGroupText\"},{\"children\":[{\"name\":\"nameInData\",\"value\":\"recordInfo\"},{\"children\":[{\"name\":\"nameInData\",\"value\":\"type\"}],\"name\":\"linkedPath\"}],\"name\":\"linkedPath\"}],\"name\":\"from\"}";
		CoraDataRecordLink dataLink = (CoraDataRecordLink) getConverterdLink(json);
		assertEquals(dataLink.getNameInData(), "from");
		assertEquals(dataLink.getFirstAtomicValueWithNameInData("linkedRecordType"), "coraText");
		assertEquals(dataLink.getFirstAtomicValueWithNameInData("linkedRecordId"),
				"exampleGroupText");
		assertTrue(dataLink.containsChildWithNameInData("linkedPath"));
		assertCorrectDataInLinkedPath(dataLink);

	}

	@Test
	public void testToInstanceWithLinkedRepeatIdAndLinkedPath() {
		String json = "{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"coraText\"},{\"name\":\"linkedRecordId\",\"value\":\"exampleGroupText\"},{\"name\":\"linkedRepeatId\",\"value\":\"one\"},{\"children\":[{\"name\":\"nameInData\",\"value\":\"recordInfo\"},{\"children\":[{\"name\":\"nameInData\",\"value\":\"type\"}],\"name\":\"linkedPath\"}],\"name\":\"linkedPath\"}],\"name\":\"from\"}";
		CoraDataRecordLink dataLink = (CoraDataRecordLink) getConverterdLink(json);
		assertEquals(dataLink.getNameInData(), "from");
		assertEquals(dataLink.getFirstAtomicValueWithNameInData("linkedRecordType"), "coraText");
		assertEquals(dataLink.getFirstAtomicValueWithNameInData("linkedRecordId"),
				"exampleGroupText");
		assertEquals(dataLink.getFirstAtomicValueWithNameInData("linkedRepeatId"), "one");
		assertTrue(dataLink.containsChildWithNameInData("linkedPath"));
		assertCorrectDataInLinkedPath(dataLink);

	}

	private void assertCorrectDataInLinkedPath(CoraDataRecordLink dataLink) {
		DataGroup outerLinkedPath = dataLink.getFirstGroupWithNameInData("linkedPath");
		assertEquals(outerLinkedPath.getFirstAtomicValueWithNameInData("nameInData"), "recordInfo");
		DataGroup innerLinkedPath = outerLinkedPath.getFirstGroupWithNameInData("linkedPath");
		assertEquals(innerLinkedPath.getFirstAtomicValueWithNameInData("nameInData"), "type");
	}

	private DataLink getConverterdLink(String json) {
		OrgJsonParser jsonParser = new OrgJsonParser();
		JsonValue jsonValue = jsonParser.parseString(json);
		JsonToDataRecordLinkConverter converter = JsonToDataRecordLinkConverter
				.forJsonObject((JsonObject) jsonValue);

		DataLink dataLink = (DataLink) converter.toInstance();
		return dataLink;
	}

	@Test
	public void testToClassWithRepeatId() {
		String json = "{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"recordType\"},{\"name\":\"linkedRecordId\",\"value\":\"place\"}],\"repeatId\":\"0\",\"name\":\"someLink\"}";
		DataLink dataLink = getConverterdLink(json);
		assertEquals(dataLink.getNameInData(), "someLink");
		assertEquals(dataLink.getRepeatId(), "0");
	}

	@Test
	public void testToClassWithAttribute() {
		String json = "{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"recordType\"},{\"name\":\"linkedRecordId\",\"value\":\"place\"}],\"attributes\":{\"type\":\"someType\"},\"name\":\"someLink\"}";
		DataLink dataLink = getConverterdLink(json);

		assertEquals(dataLink.getNameInData(), "someLink");
		String attributeValue = dataLink.getAttribute("type").getValue();
		assertEquals(attributeValue, "someType");
	}

	@Test
	public void testToClassWithRepeatIdAndAttribute() {
		String json = "{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"recordType\"},{\"name\":\"linkedRecordId\",\"value\":\"place\"}],\"repeatId\":\"0\",\"attributes\":{\"type\":\"someType\"},\"name\":\"someLink\"}";
		DataLink dataLink = getConverterdLink(json);

		assertEquals(dataLink.getNameInData(), "someLink");
		String attributeValue = dataLink.getAttribute("type").getValue();
		assertEquals(attributeValue, "someType");
		assertEquals(dataLink.getRepeatId(), "0");
	}

	@Test(expectedExceptions = JsonParseException.class, expectedExceptionsMessageRegExp = ""
			+ "Error parsing jsonObject: Group data can only contain keys: name, children and attributes")
	public void testToClassWithRepeatIdAndAttributeAndExtra() {
		String json = "{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"recordType\"},{\"name\":\"linkedRecordId\",\"value\":\"place\"}],\"repeatId\":\"0\",\"attributes\":{\"type\":\"someType\"},\"name\":\"someLink\", \"extra\":\"extraValue\"}";
		getConverterdLink(json);
	}

	@Test(expectedExceptions = JsonParseException.class, expectedExceptionsMessageRegExp = ""
			+ "Error parsing jsonObject: Group data must contain name and children, and may contain attributes or repeatId")
	public void testToClassWithIncorrectAttributeNameInData() {
		String json = "{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"recordType\"},{\"name\":\"linkedRecordId\",\"value\":\"place\"}],\"NOTattributes\":{\"type\":\"someType\"},\"name\":\"someLink\"}";
		getConverterdLink(json);
	}

	@Test(expectedExceptions = JsonParseException.class, expectedExceptionsMessageRegExp = ""
			+ "RecordLinkData must contain children with name linkedRecordType and linkedRecordId "
			+ "and might contain child with name linkedRepeatId and linkedPath")
	public void testToClassWithNoLinkedRecordType() {
		String json = "{\"children\":[{\"name\":\"linkedRecordId\",\"value\":\"place\"}],\"name\":\"someLink\"}";
		getConverterdLink(json);
	}

	@Test(expectedExceptions = JsonParseException.class, expectedExceptionsMessageRegExp = ""
			+ "RecordLinkData must contain children with name linkedRecordType and linkedRecordId "
			+ "and might contain child with name linkedRepeatId and linkedPath")
	public void testToClassWithNoLinkedRecordTypeButOtherChild() {
		String json = "{\"children\":[{\"name\":\"NOTlinkedRecordType\",\"value\":\"recordType\"},{\"name\":\"linkedRecordId\",\"value\":\"place\"}],\"name\":\"someLink\"}";
		getConverterdLink(json);
	}

	@Test(expectedExceptions = JsonParseException.class, expectedExceptionsMessageRegExp = ""
			+ "RecordLinkData must contain children with name linkedRecordType and linkedRecordId "
			+ "and might contain child with name linkedRepeatId and linkedPath")
	public void testToClassWithNoLinkedRecordId() {
		String json = "{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"recordType\"}],\"name\":\"someLink\"}";
		getConverterdLink(json);
	}

	@Test(expectedExceptions = JsonParseException.class, expectedExceptionsMessageRegExp = ""
			+ "RecordLinkData must contain children with name linkedRecordType and linkedRecordId "
			+ "and might contain child with name linkedRepeatId and linkedPath")
	public void testToClassWithNoLinkedRecordIdButOtherChild() {
		String json = "{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"recordType\"},{\"name\":\"NOTlinkedRecordId\",\"value\":\"place\"}],\"name\":\"someLink\"}";
		getConverterdLink(json);
	}

	@Test(expectedExceptions = JsonParseException.class, expectedExceptionsMessageRegExp = ""
			+ "RecordLinkData must contain children with name linkedRecordType and linkedRecordId "
			+ "and might contain child with name linkedRepeatId and linkedPath")
	public void testToClassWithTooManyChildren() {
		String json = "{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"recordType\"},{\"name\":\"linkedRecordId\",\"value\":\"place\"},{\"name\":\"linkedRepeatId\",\"value\":\"one\"},{\"name\":\"someExtra\",\"value\":\"one\"},{\"children\":[{\"name\":\"nameInData\",\"value\":\"recordInfo\"},{\"children\":[{\"name\":\"nameInData\",\"value\":\"type\"}],\"name\":\"linkedPath\"}],\"name\":\"linkedPath\"}],\"name\":\"someLink\"}";
		getConverterdLink(json);
	}

	@Test(expectedExceptions = JsonParseException.class, expectedExceptionsMessageRegExp = ""
			+ "RecordLinkData must contain children with name linkedRecordType and linkedRecordId "
			+ "and might contain child with name linkedRepeatId and linkedPath")
	public void testToClassWithOkNumberOfChildrenButNoLinkedRepeatIdAndNoLinkedPath() {
		String json = "{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"coraText\"},{\"name\":\"linkedRecordId\",\"value\":\"exampleGroupText\"},{\"name\":\"someOtherChild\",\"value\":\"one\"}],\"name\":\"from\"}";
		getConverterdLink(json);
	}

	@Test(expectedExceptions = JsonParseException.class, expectedExceptionsMessageRegExp = ""
			+ "RecordLinkData must contain children with name linkedRecordType and linkedRecordId "
			+ "and might contain child with name linkedRepeatId and linkedPath")
	public void testToClassWithMaxNumberOfChildrenButNoLinkedRepeatId() {
		String json = "{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"recordType\"},{\"name\":\"linkedRecordId\",\"value\":\"place\"},{\"name\":\"someExtra\",\"value\":\"one\"},{\"children\":[{\"name\":\"nameInData\",\"value\":\"recordInfo\"},{\"children\":[{\"name\":\"nameInData\",\"value\":\"type\"}],\"name\":\"linkedPath\"}],\"name\":\"linkedPath\"}],\"name\":\"someLink\"}";
		getConverterdLink(json);
	}

	@Test(expectedExceptions = JsonParseException.class, expectedExceptionsMessageRegExp = ""
			+ "RecordLinkData must contain children with name linkedRecordType and linkedRecordId "
			+ "and might contain child with name linkedRepeatId and linkedPath")
	public void testToClassWithMaxNumberOfChildrenButNoLinkedPath() {
		String json = "{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"recordType\"},{\"name\":\"linkedRecordId\",\"value\":\"place\"},{\"name\":\"linkedRepeatId\",\"value\":\"one\"},{\"name\":\"someExtra\",\"value\":\"one\"}],\"name\":\"someLink\"}";
		getConverterdLink(json);
	}
}
