/*
 * Copyright 2015, 2019 Uppsala University Library
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

package se.uu.ub.cora.basicdata.data;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.testng.annotations.Test;

import se.uu.ub.cora.basicdata.DataMissingException;
import se.uu.ub.cora.data.Data;
import se.uu.ub.cora.data.DataElement;
import se.uu.ub.cora.data.DataGroup;

public class CoraDataGroupTest {
	@Test
	public void testInit() {
		DataGroup dataGroup = CoraDataGroup.withNameInData("nameInData");
		assertEquals(dataGroup.getNameInData(), "nameInData");
		assertNotNull(dataGroup.getAttributes());
		assertNotNull(dataGroup.getChildren());
	}

	@Test
	public void testGroupIsData() {
		DataGroup dataGroup = CoraDataGroup.withNameInData("nameInData");
		assertTrue(dataGroup instanceof Data);
	}

	@Test
	public void testGroupAsLink() {
		DataGroup dataGroup = CoraDataGroup.asLinkWithNameInDataAndTypeAndId("nameInData",
				"someType", "someId");
		assertEquals(dataGroup.getNameInData(), "nameInData");
		assertEquals(dataGroup.getFirstAtomicValueWithNameInData("linkedRecordType"), "someType");
		assertEquals(dataGroup.getFirstAtomicValueWithNameInData("linkedRecordId"), "someId");
	}

	@Test
	public void testInitWithRepeatId() {
		DataGroup dataGroup = CoraDataGroup.withNameInData("nameInData");
		dataGroup.setRepeatId("hrumph");
		assertEquals(dataGroup.getNameInData(), "nameInData");
		assertNotNull(dataGroup.getAttributes());
		assertNotNull(dataGroup.getChildren());
		assertEquals(dataGroup.getRepeatId(), "hrumph");
	}

	@Test
	public void testAddAttribute() {
		DataGroup dataGroup = CoraDataGroup.withNameInData("nameInData");
		dataGroup.addAttributeByIdWithValue("nameInData", "value");
		Map<String, String> attributes = dataGroup.getAttributes();
		Entry<String, String> entry = attributes.entrySet().iterator().next();
		assertEquals(entry.getKey(), "nameInData");
		assertEquals(entry.getValue(), "value");
	}

	@Test
	public void testGetAttribute() {
		DataGroup dataGroup = CoraDataGroup.withNameInData("nameInData");
		dataGroup.addAttributeByIdWithValue("attributeId", "attributeValue");
		assertEquals(dataGroup.getAttribute("attributeId"), "attributeValue");

	}

	@Test
	public void testAddChild() {
		DataGroup dataGroup = CoraDataGroup.withNameInData("nameInData");
		DataElement dataElement = CoraDataAtomic.withNameInDataAndValue("childNameInData",
				"childValue");
		dataGroup.addChild(dataElement);
		List<DataElement> children = dataGroup.getChildren();
		DataElement childElementOut = children.get(0);
		assertEquals(childElementOut.getNameInData(), "childNameInData");
	}

	@Test
	public void testContainsChildWithId() {
		DataGroup dataGroup = CoraDataGroup.withNameInData("nameInData");
		dataGroup
				.addChild(CoraDataAtomic.withNameInDataAndValue("otherChildId", "otherChildValue"));
		DataElement child = CoraDataAtomic.withNameInDataAndValue("childId", "child value");
		dataGroup.addChild(child);
		assertTrue(dataGroup.containsChildWithNameInData("childId"));
	}

	@Test
	public void testContainsChildWithIdNotFound() {
		DataGroup dataGroup = CoraDataGroup.withNameInData("nameInData");
		DataElement child = CoraDataAtomic.withNameInDataAndValue("childId", "child value");
		dataGroup.addChild(child);
		assertFalse(dataGroup.containsChildWithNameInData("childId_NOT_FOUND"));
	}

	@Test
	public void testRemoveChildWithId() {
		CoraDataGroup dataGroup = CoraDataGroup.withNameInData("nameInData");
		DataElement child = CoraDataAtomic.withNameInDataAndValue("childId", "child value");
		dataGroup.addChild(child);
		dataGroup.removeChild("childId");
		assertFalse(dataGroup.containsChildWithNameInData("childId"));
	}

	@Test(expectedExceptions = DataMissingException.class)
	public void testRemoveChildWithIdNotFound() {
		CoraDataGroup dataGroup = CoraDataGroup.withNameInData("nameInData");
		dataGroup.removeChild("childId");
		assertFalse(dataGroup.containsChildWithNameInData("childId"));
	}

	@Test
	public void testGetAtomicValue() {
		DataGroup dataGroup = CoraDataGroup.withNameInData("nameInData");
		dataGroup
				.addChild(CoraDataAtomic.withNameInDataAndValue("atomicNameInData", "atomicValue"));
		assertEquals(dataGroup.getFirstAtomicValueWithNameInData("atomicNameInData"),
				"atomicValue");
	}

	@Test(expectedExceptions = DataMissingException.class, expectedExceptionsMessageRegExp = ""
			+ "Atomic value not found for childNameInData:" + "atomicNameInData_NOT_FOUND")
	public void testExtractAtomicValueNotFound() {
		DataGroup dataGroup = CoraDataGroup.withNameInData("nameInData");
		dataGroup
				.addChild(CoraDataAtomic.withNameInDataAndValue("atomicNameInData", "atomicValue"));
		dataGroup.getFirstAtomicValueWithNameInData("atomicNameInData_NOT_FOUND");
	}

	@Test
	public void testGetAllDataAtomicsWithNameInData() {
		DataGroup book = createDataGroupWithTwoAtomicChildrenAndOneGroupChild();

		assertEquals(book.getAllDataAtomicsWithNameInData("someChild").size(), 2);

	}

	private DataGroup createDataGroupWithTwoAtomicChildrenAndOneGroupChild() {
		DataGroup book = CoraDataGroup.withNameInData("book");
		CoraDataAtomic child1 = CoraDataAtomic.withNameInDataAndValue("someChild", "child1");
		child1.setRepeatId("0");
		book.addChild(child1);

		CoraDataAtomic child2 = CoraDataAtomic.withNameInDataAndValue("someChild", "child2");
		child2.setRepeatId("1");
		book.addChild(child2);

		DataGroup child3 = CoraDataGroup.withNameInData("someChild");
		book.addChild(child3);
		return book;
	}

	@Test
	public void testGetFirstDataAtomicWithNameInData() {
		DataGroup book = createDataGroupWithTwoAtomicChildrenAndOneGroupChild();
		assertEquals(book.getFirstDataAtomicWithNameInData("someChild"), book.getChildren().get(0));
	}

	@Test(expectedExceptions = DataMissingException.class, expectedExceptionsMessageRegExp = ""
			+ "DataAtomic not found for childNameInData:childNameInData_NOT_FOUND")
	public void testGetFirstDataAtomicWithNameInDataNotFound() {
		DataGroup dataGroup = CoraDataGroup.withNameInData("nameInData");
		dataGroup.addChild(
				CoraDataAtomic.withNameInDataAndValue("someChildNameInData", "atomicValue"));
		dataGroup.getFirstDataAtomicWithNameInData("childNameInData_NOT_FOUND");
	}

	@Test
	public void testGetGroup() {
		DataGroup dataGroup = CoraDataGroup.withNameInData("nameInData");
		dataGroup
				.addChild(CoraDataAtomic.withNameInDataAndValue("atomicNameInData", "atomicValue"));
		DataGroup dataGroup2 = CoraDataGroup.withNameInData("childNameInData");
		dataGroup2.addChild(CoraDataGroup.withNameInData("grandChildNameInData"));
		dataGroup.addChild(dataGroup2);
		assertEquals(dataGroup.getFirstGroupWithNameInData("childNameInData"), dataGroup2);
	}

	@Test(expectedExceptions = DataMissingException.class, expectedExceptionsMessageRegExp = ""
			+ "Group not found for childNameInData:childNameInData_NOT_FOUND")
	public void testGetFirstGroupWithNameInDataNotFound() {
		DataGroup dataGroup = CoraDataGroup.withNameInData("nameInData");
		dataGroup
				.addChild(CoraDataAtomic.withNameInDataAndValue("atomicNameInData", "atomicValue"));
		DataGroup dataGroup2 = CoraDataGroup.withNameInData("childNameInData");
		dataGroup2.addChild(CoraDataGroup.withNameInData("grandChildNameInData"));
		dataGroup.addChild(dataGroup2);
		dataGroup.getFirstGroupWithNameInData("childNameInData_NOT_FOUND");
	}

	@Test
	public void testGetFirstChildWithNameInData() {
		DataGroup dataGroup = CoraDataGroup.withNameInData("nameInData");
		dataGroup
				.addChild(CoraDataAtomic.withNameInDataAndValue("atomicNameInData", "atomicValue"));
		DataGroup dataGroup2 = CoraDataGroup.withNameInData("childNameInData");
		dataGroup2.addChild(CoraDataGroup.withNameInData("grandChildNameInData"));
		dataGroup.addChild(dataGroup2);
		assertEquals(dataGroup.getFirstChildWithNameInData("childNameInData"), dataGroup2);
	}

	@Test(expectedExceptions = DataMissingException.class, expectedExceptionsMessageRegExp = ""
			+ "Element not found for childNameInData:childNameInData_NOT_FOUND")
	public void testGetFirstChildWithNameInDataNotFound() {
		DataGroup dataGroup = CoraDataGroup.withNameInData("nameInData");
		dataGroup
				.addChild(CoraDataAtomic.withNameInDataAndValue("atomicNameInData", "atomicValue"));
		DataGroup dataGroup2 = CoraDataGroup.withNameInData("childNameInData");
		dataGroup2.addChild(CoraDataGroup.withNameInData("grandChildNameInData"));
		dataGroup.addChild(dataGroup2);
		dataGroup.getFirstChildWithNameInData("childNameInData_NOT_FOUND");
	}

	@Test
	public void testGetAllGroupsWithNameInData() {
		DataGroup dataGroup = CoraDataGroup.withNameInData("nameInData");
		dataGroup
				.addChild(CoraDataAtomic.withNameInDataAndValue("atomicNameInData", "atomicValue"));
		addTwoGroupChildrenWithSameNameInData(dataGroup);

		List<DataGroup> groupsFound = dataGroup.getAllGroupsWithNameInData("childNameInData");
		assertEquals(groupsFound.size(), 2);
	}

	private void addTwoGroupChildrenWithSameNameInData(DataGroup parentDataGroup) {
		DataGroup dataGroup = CoraDataGroup.withNameInData("childNameInData");
		dataGroup.addChild(CoraDataAtomic.withNameInDataAndValue("firstName", "someName"));
		dataGroup.setRepeatId("0");
		parentDataGroup.addChild(dataGroup);
		DataGroup dataGroup2 = CoraDataGroup.withNameInData("childNameInData");
		dataGroup2.addChild(CoraDataAtomic.withNameInDataAndValue("firstName", "someOtherName"));
		dataGroup2.setRepeatId("1");
		parentDataGroup.addChild(dataGroup2);
	}

	@Test
	public void testGetAllGroupsWithNameInDataNoMatches() {
		DataGroup dataGroup = CoraDataGroup.withNameInData("nameInData");
		dataGroup
				.addChild(CoraDataAtomic.withNameInDataAndValue("atomicNameInData", "atomicValue"));

		List<DataGroup> groupsFound = dataGroup.getAllGroupsWithNameInData("childNameInData");
		assertEquals(groupsFound.size(), 0);
	}

	@Test
	public void testGetAllGroupsWithNameInDataAndAttributesOneMatch() {
		CoraDataGroup dataGroup = CoraDataGroup.withNameInData("someDataGroup");
		DataGroup child3 = createTestGroupForAttributesReturnChildGroupWithAttribute(dataGroup);

		Collection<DataGroup> groupsFound = dataGroup.getAllGroupsWithNameInDataAndAttributes(
				"groupId2", CoraDataAttribute.withNameInDataAndValue("nameInData", "value1"));

		assertEquals(groupsFound.size(), 1);
		assertGroupsFoundAre(groupsFound, child3);
	}

	private void assertGroupsFoundAre(Collection<DataGroup> groupsFound,
			DataGroup... assertedGroups) {
		int i = 0;
		for (DataGroup groupFound : groupsFound) {
			assertEquals(groupFound, assertedGroups[i]);
			i++;
		}
	}

	private DataGroup createTestGroupForAttributesReturnChildGroupWithAttribute(
			DataGroup dataGroup) {
		addAndReturnDataGroupChildWithNameInData(dataGroup, "groupId2");
		addAndReturnDataGroupChildWithNameInData(dataGroup, "groupId3");
		addAndReturnDataGroupChildWithNameInData(dataGroup, "groupId2");
		DataGroup child3 = addAndReturnDataGroupChildWithNameInDataAndAttributes(dataGroup,
				"groupId2", CoraDataAttribute.withNameInDataAndValue("nameInData", "value1"));
		return child3;
	}

	private DataGroup addAndReturnDataGroupChildWithNameInData(DataGroup dataGroup,
			String nameInData) {
		DataGroup child = CoraDataGroup.withNameInData(nameInData);
		dataGroup.addChild(child);
		return child;
	}

	private DataGroup addAndReturnDataGroupChildWithNameInDataAndAttributes(DataGroup dataGroup,
			String nameInData, CoraDataAttribute... attributes) {
		DataGroup child = CoraDataGroup.withNameInData(nameInData);
		dataGroup.addChild(child);
		for (CoraDataAttribute attribute : attributes) {
			child.addAttributeByIdWithValue(attribute.getNameInData(), attribute.getValue());
		}
		return child;
	}

	@Test
	public void testGetAllGroupsWithNameInDataAndAttributesTwoMatches() {
		CoraDataGroup dataGroup = CoraDataGroup.withNameInData("someDataGroup");
		DataGroup child3 = createTestGroupForAttributesReturnChildGroupWithAttribute(dataGroup);
		DataGroup child4 = addAndReturnDataGroupChildWithNameInDataAndAttributes(dataGroup,
				"groupId2", CoraDataAttribute.withNameInDataAndValue("nameInData", "value1"));

		Collection<DataGroup> groupsFound = dataGroup.getAllGroupsWithNameInDataAndAttributes(
				"groupId2", CoraDataAttribute.withNameInDataAndValue("nameInData", "value1"));

		assertEquals(groupsFound.size(), 2);
		assertGroupsFoundAre(groupsFound, child3, child4);
	}

	@Test
	public void testGetAllGroupsWithNameInDataAndAttributesOneWrongAttributeValueTwoMatches() {
		CoraDataGroup dataGroup = CoraDataGroup.withNameInData("someDataGroup");
		DataGroup child3 = createTestGroupForAttributesReturnChildGroupWithAttribute(dataGroup);
		DataGroup child4 = addAndReturnDataGroupChildWithNameInDataAndAttributes(dataGroup,
				"groupId2", CoraDataAttribute.withNameInDataAndValue("nameInData", "value1"));
		addAndReturnDataGroupChildWithNameInDataAndAttributes(dataGroup, "groupId2",
				CoraDataAttribute.withNameInDataAndValue("nameInData", "value2"));

		Collection<DataGroup> groupsFound = dataGroup.getAllGroupsWithNameInDataAndAttributes(
				"groupId2", CoraDataAttribute.withNameInDataAndValue("nameInData", "value1"));

		assertEquals(groupsFound.size(), 2);
		assertGroupsFoundAre(groupsFound, child3, child4);
	}

	@Test
	public void testGetAllGroupsWithNameInDataAndAttributesOneWrongAttributeNameTwoMatches() {
		CoraDataGroup dataGroup = CoraDataGroup.withNameInData("someDataGroup");
		DataGroup child3 = createTestGroupForAttributesReturnChildGroupWithAttribute(dataGroup);
		DataGroup child4 = addAndReturnDataGroupChildWithNameInDataAndAttributes(dataGroup,
				"groupId2", CoraDataAttribute.withNameInDataAndValue("nameInData", "value1"));
		addAndReturnDataGroupChildWithNameInDataAndAttributes(dataGroup, "groupId2",
				CoraDataAttribute.withNameInDataAndValue("nameInData2", "value1"));

		Collection<DataGroup> groupsFound = dataGroup.getAllGroupsWithNameInDataAndAttributes(
				"groupId2", CoraDataAttribute.withNameInDataAndValue("nameInData", "value1"));

		assertEquals(groupsFound.size(), 2);
		assertGroupsFoundAre(groupsFound, child3, child4);
	}

	@Test
	public void testGetAllGroupsWithNameInDataAndTwoAttributesNoMatches() {
		CoraDataGroup dataGroup = CoraDataGroup.withNameInData("someDataGroup");
		createTestGroupForAttributesReturnChildGroupWithAttribute(dataGroup);
		addAndReturnDataGroupChildWithNameInDataAndAttributes(dataGroup, "groupId2",
				CoraDataAttribute.withNameInDataAndValue("nameInData", "value1"),
				CoraDataAttribute.withNameInDataAndValue("nameInData2", "value2"));

		Collection<DataGroup> groupsFound = dataGroup.getAllGroupsWithNameInDataAndAttributes(
				"groupId2", CoraDataAttribute.withNameInDataAndValue("nameInData", "value1"),
				CoraDataAttribute.withNameInDataAndValue("nameInData2", "value1"));

		assertEquals(groupsFound.size(), 0);
	}

	@Test
	public void testGetAllGroupsWithNameInDataAndTwoAttributesOneMatches() {
		CoraDataGroup dataGroup = CoraDataGroup.withNameInData("someDataGroup");
		createTestGroupForAttributesReturnChildGroupWithAttribute(dataGroup);
		DataGroup child4 = addAndReturnDataGroupChildWithNameInDataAndAttributes(dataGroup,
				"groupId2", CoraDataAttribute.withNameInDataAndValue("nameInData", "value1"),
				CoraDataAttribute.withNameInDataAndValue("nameInData2", "value2"));
		addAndReturnDataGroupChildWithNameInDataAndAttributes(dataGroup, "groupId2",
				CoraDataAttribute.withNameInDataAndValue("nameInData", "value1"),
				CoraDataAttribute.withNameInDataAndValue("nameInData3", "value2"));

		Collection<DataGroup> groupsFound = dataGroup.getAllGroupsWithNameInDataAndAttributes(
				"groupId2", CoraDataAttribute.withNameInDataAndValue("nameInData", "value1"),
				CoraDataAttribute.withNameInDataAndValue("nameInData2", "value2"));

		assertEquals(groupsFound.size(), 1);
		assertGroupsFoundAre(groupsFound, child4);
	}

	@Test
	public void testRemoveChild() {
		DataGroup dataGroup = CoraDataGroup.withNameInData("someDataGroup");
		createAndAddAnAtomicChildToDataGroup(dataGroup);
		dataGroup.removeFirstChildWithNameInData("childId");
		assertFalse(dataGroup.containsChildWithNameInData("childId"));
	}

	@Test(expectedExceptions = DataMissingException.class)
	public void testRemoveChildNotFound() {
		DataGroup dataGroup = CoraDataGroup.withNameInData("someDataGroup");
		createAndAddAnAtomicChildToDataGroup(dataGroup);
		dataGroup.removeFirstChildWithNameInData("childId_NOTFOUND");
	}

	private DataElement createAndAddAnAtomicChildToDataGroup(DataGroup dataGroup) {
		return createAndAddAnAtomicChildToDataGroupUsingNameInData(dataGroup, "childId");
	}

	private DataElement createAndAddAnAtomicChildToDataGroupUsingNameInData(DataGroup dataGroup,
			String nameInData) {
		DataElement child = CoraDataAtomic.withNameInDataAndValue(nameInData, "child value");
		dataGroup.addChild(child);
		return child;
	}

	@Test
	public void testRemoveAllChildrenWithNameInData() {
		DataGroup dataGroup = CoraDataGroup.withNameInData("someDataGroup");
		createAndAddAnAtomicChildWithRepeatIdToDataGroup(dataGroup, "0");
		createAndAddAnAtomicChildWithRepeatIdToDataGroup(dataGroup, "1");
		dataGroup.removeAllChildrenWithNameInData("childId");
		assertFalse(dataGroup.containsChildWithNameInData("childId"));
	}

	private DataElement createAndAddAnAtomicChildWithRepeatIdToDataGroup(DataGroup dataGroup,
			String repeatId) {
		DataElement child = CoraDataAtomic.withNameInDataAndValueAndRepeatId("childId",
				"child value", repeatId);
		dataGroup.addChild(child);
		return child;
	}

	@Test
	public void testRemoveAllChildrenWithNameInDataWhenOtherChildrenExist() {
		DataGroup dataGroup = CoraDataGroup.withNameInData("someDataGroup");
		createAndAddAnAtomicChildWithRepeatIdToDataGroup(dataGroup, "0");
		createAndAddAnAtomicChildWithRepeatIdToDataGroup(dataGroup, "1");
		createAndAddAnAtomicChildToDataGroupUsingNameInData(dataGroup, "someOtherChildId");

		dataGroup.removeAllChildrenWithNameInData("childId");
		assertFalse(dataGroup.containsChildWithNameInData("childId"));
		assertTrue(dataGroup.containsChildWithNameInData("someOtherChildId"));
	}

	@Test(expectedExceptions = DataMissingException.class, expectedExceptionsMessageRegExp = ""
			+ "Element not found for childNameInData: childId_NOTFOUND")
	public void testRemoveAllChildNotFound() {
		DataGroup dataGroup = CoraDataGroup.withNameInData("someDataGroup");
		createAndAddAnAtomicChildToDataGroup(dataGroup);
		dataGroup.removeAllChildrenWithNameInData("childId_NOTFOUND");
	}

}