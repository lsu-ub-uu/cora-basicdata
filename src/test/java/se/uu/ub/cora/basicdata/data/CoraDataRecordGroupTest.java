/*
 * Copyright 2015, 2019 Uppsala University Library
 * Copyright 2022 Olov McKie
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
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.Data;
import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataAttribute;
import se.uu.ub.cora.data.DataChild;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataMissingException;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.data.DataRecordLink;

public class CoraDataRecordGroupTest {

	private static final String RECORD_INFO = "recordInfo";
	private static final String VALIDATION_TYPE = "validationType";
	private DataRecordGroup defaultRecordGroup;

	@BeforeMethod
	public void setUp() {
		defaultRecordGroup = CoraDataRecordGroup.withNameInData("someDataGroup");
	}

	@Test
	public void testInit() {
		assertEquals(defaultRecordGroup.getNameInData(), "someDataGroup");
		assertNotNull(defaultRecordGroup.getAttributes());
		assertNotNull(defaultRecordGroup.getChildren());
	}

	@Test
	public void testGroupIsData() {
		assertTrue(defaultRecordGroup instanceof Data);
	}

	@Test
	public void testInitWithRepeatId() {
		assertEquals(defaultRecordGroup.getNameInData(), "someDataGroup");
		assertNotNull(defaultRecordGroup.getAttributes());
		assertNotNull(defaultRecordGroup.getChildren());
	}

	@Test
	public void testAddAttribute() {
		defaultRecordGroup.addAttributeByIdWithValue("someAttributeName", "value");
		Collection<DataAttribute> attributes = defaultRecordGroup.getAttributes();
		DataAttribute next = attributes.iterator().next();
		assertEquals(next.getNameInData(), "someAttributeName");
		assertEquals(next.getValue(), "value");
	}

	@Test
	public void testAddAttributeWithSameNameInDataOverwrites() {
		defaultRecordGroup.addAttributeByIdWithValue("someAttributeName", "value");
		defaultRecordGroup.addAttributeByIdWithValue("someAttributeName", "someOtherValue");

		Collection<DataAttribute> attributes = defaultRecordGroup.getAttributes();
		assertEquals(attributes.size(), 1);
		DataAttribute next = attributes.iterator().next();
		assertEquals(next.getValue(), "someOtherValue");
	}

	@Test
	public void testHasAttributes() {
		assertFalse(defaultRecordGroup.hasAttributes());
		defaultRecordGroup.addAttributeByIdWithValue("attributeId", "attributeValue");
		assertTrue(defaultRecordGroup.hasAttributes());
	}

	@Test
	public void testGetAttribute() {
		defaultRecordGroup.addAttributeByIdWithValue("attributeId", "attributeValue");
		assertEquals(defaultRecordGroup.getAttribute("attributeId").getValue(), "attributeValue");
	}

	@Test(expectedExceptions = DataMissingException.class, expectedExceptionsMessageRegExp = ""
			+ "Attribute with id someAttributeId not found.")
	public void testGetAttributeDoesNotExist() {
		defaultRecordGroup.getAttribute("someAttributeId");
	}

	@Test
	public void testAddChild() {
		DataChild dataElement = CoraDataAtomic.withNameInDataAndValue("childNameInData",
				"childValue");
		defaultRecordGroup.addChild(dataElement);
		List<DataChild> children = defaultRecordGroup.getChildren();
		DataChild childElementOut = children.get(0);
		assertEquals(childElementOut.getNameInData(), "childNameInData");
	}

	@Test
	public void testHasChildren() throws Exception {
		assertFalse(defaultRecordGroup.hasChildren());
		defaultRecordGroup.addChild(CoraDataGroup.withNameInData("child"));
		assertTrue(defaultRecordGroup.hasChildren());
	}

	@Test
	public void addChildrenEmptyList() {
		defaultRecordGroup.addChildren(Collections.emptyList());
		assertTrue(defaultRecordGroup.getChildren().isEmpty());
	}

	@Test
	public void testAddChildrenAddOneChildNoChildrenBefore() {
		List<DataChild> dataElements = createListWithOneChild();

		defaultRecordGroup.addChildren(dataElements);

		List<DataChild> children = defaultRecordGroup.getChildren();
		assertEquals(children.size(), 1);
		assertSame(children.get(0), dataElements.get(0));
	}

	@Test
	public void testAddChildrenAddOneChildOneChildBefore() {
		defaultRecordGroup
				.addChild(CoraDataAtomic.withNameInDataAndValue("someChild", "someValue"));
		List<DataChild> dataElements = createListWithOneChild();

		defaultRecordGroup.addChildren(dataElements);

		List<DataChild> children = defaultRecordGroup.getChildren();
		assertEquals(children.size(), 2);
		assertSame(children.get(1), dataElements.get(0));
	}

	@Test
	public void testAddChildrenAddMultipleChildOneChildBefore() {
		defaultRecordGroup
				.addChild(CoraDataAtomic.withNameInDataAndValue("someChild", "someValue"));
		List<DataChild> dataElements = createListWithOneChild();
		dataElements.add(CoraDataRecordGroup.withNameInData("someGroupChild"));
		dataElements.add(CoraDataAtomic.withNameInDataAndValue("someOtherAtomicChild", "42"));

		defaultRecordGroup.addChildren(dataElements);

		List<DataChild> children = defaultRecordGroup.getChildren();
		assertEquals(children.size(), 4);
		assertSame(children.get(1), dataElements.get(0));
		assertSame(children.get(2), dataElements.get(1));
		assertSame(children.get(3), dataElements.get(2));
	}

	private List<DataChild> createListWithOneChild() {
		DataChild dataElement = CoraDataAtomic.withNameInDataAndValue("childNameInData",
				"childValue");
		List<DataChild> dataElements = new ArrayList<>();
		dataElements.add(dataElement);
		return dataElements;
	}

	@Test
	public void testContainsChildWithId() {
		defaultRecordGroup
				.addChild(CoraDataAtomic.withNameInDataAndValue("otherChildId", "otherChildValue"));
		DataChild child = CoraDataAtomic.withNameInDataAndValue("childId", "child value");
		defaultRecordGroup.addChild(child);
		assertTrue(defaultRecordGroup.containsChildWithNameInData("childId"));
	}

	@Test
	public void testContainsChildWithIdNotFound() {
		DataChild child = CoraDataAtomic.withNameInDataAndValue("childId", "child value");
		defaultRecordGroup.addChild(child);
		assertFalse(defaultRecordGroup.containsChildWithNameInData("childId_NOT_FOUND"));
	}

	@Test
	public void testGetAtomicValue() {
		defaultRecordGroup
				.addChild(CoraDataAtomic.withNameInDataAndValue("atomicNameInData", "atomicValue"));
		assertEquals(defaultRecordGroup.getFirstAtomicValueWithNameInData("atomicNameInData"),
				"atomicValue");
	}

	@Test(expectedExceptions = DataMissingException.class, expectedExceptionsMessageRegExp = ""
			+ "Atomic value not found for childNameInData:" + "atomicNameInData_NOT_FOUND")
	public void testExtractAtomicValueNotFound() {
		defaultRecordGroup
				.addChild(CoraDataAtomic.withNameInDataAndValue("atomicNameInData", "atomicValue"));
		defaultRecordGroup.getFirstAtomicValueWithNameInData("atomicNameInData_NOT_FOUND");
	}

	@Test
	public void testGetAllDataAtomicsWithNameInData() {
		DataRecordGroup book = createDataGroupWithTwoAtomicChildrenAndOneGroupChild();

		assertEquals(book.getAllDataAtomicsWithNameInData("someChild").size(), 2);
	}

	@Test
	public void testGetAllDataAtomicsWithNameInDataNoResult() throws Exception {
		DataRecordGroup dataGroup = CoraDataRecordGroup.withNameInData("someNameInData");
		List<DataAtomic> aList = dataGroup.getAllDataAtomicsWithNameInData("someNameInData");
		assertEquals(aList.size(), 0);
	}

	private DataRecordGroup createDataGroupWithTwoAtomicChildrenAndOneGroupChild() {
		DataRecordGroup book = CoraDataRecordGroup.withNameInData("book");
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
		DataRecordGroup book = createDataGroupWithTwoAtomicChildrenAndOneGroupChild();
		assertEquals(book.getFirstDataAtomicWithNameInData("someChild"), book.getChildren().get(0));
	}

	@Test(expectedExceptions = DataMissingException.class, expectedExceptionsMessageRegExp = ""
			+ "DataAtomic not found for childNameInData:childNameInData_NOT_FOUND")
	public void testGetFirstDataAtomicWithNameInDataNotFound() {
		defaultRecordGroup.addChild(
				CoraDataAtomic.withNameInDataAndValue("someChildNameInData", "atomicValue"));
		defaultRecordGroup.getFirstDataAtomicWithNameInData("childNameInData_NOT_FOUND");
	}

	@Test
	public void testGetAllDataAtomicsdWithNameInDataAndAttributesOneMatch() {
		DataRecordGroup dataGroup = createDataGroupWithDataAtomicChildren();

		List<DataAtomic> atomicsFound = (List<DataAtomic>) dataGroup
				.getAllDataAtomicsWithNameInDataAndAttributes("childOne",
						CoraDataAttribute.withNameInDataAndValue("otherAttribute", "alternative"));

		assertEquals(atomicsFound.size(), 1);
		DataChild expectedMatchingChild = dataGroup.getChildren().get(2);
		assertSame(atomicsFound.get(0), expectedMatchingChild);
	}

	private DataRecordGroup createDataGroupWithDataAtomicChildren() {
		DataRecordGroup dataGroup = CoraDataRecordGroup.withNameInData("someDataGroup");

		CoraDataAtomic childAtomic1 = CoraDataAtomic.withNameInDataAndValueAndRepeatId("childOne",
				"value1", "1");
		childAtomic1.addAttributeByIdWithValue("oneAttribute", "deafult");
		dataGroup.addChild(childAtomic1);

		CoraDataAtomic childAtomic2 = CoraDataAtomic.withNameInDataAndValueAndRepeatId("childOne",
				"value1", "2");
		dataGroup.addChild(childAtomic2);
		CoraDataAtomic childAtomic3 = CoraDataAtomic.withNameInDataAndValueAndRepeatId("childOne",
				"value1", "3");
		childAtomic3.addAttributeByIdWithValue("otherAttribute", "alternative");
		dataGroup.addChild(childAtomic3);
		return dataGroup;
	}

	@Test
	public void testGetAllDataAtomicsdWithNameInDataAndAttributesNoMatch() {
		DataRecordGroup dataGroup = createDataGroupWithDataAtomicChildren();

		List<DataAtomic> atomicsFound = (List<DataAtomic>) dataGroup
				.getAllDataAtomicsWithNameInDataAndAttributes("childOne", CoraDataAttribute
						.withNameInDataAndValue("nonMatchingAttribute", "alternative"));

		assertEquals(atomicsFound.size(), 0);
	}

	@Test
	public void testGetGroup() {
		defaultRecordGroup
				.addChild(CoraDataAtomic.withNameInDataAndValue("atomicNameInData", "atomicValue"));
		DataGroup dataGroup2 = CoraDataGroup.withNameInData("childNameInData");
		dataGroup2.addChild(CoraDataGroup.withNameInData("grandChildNameInData"));
		defaultRecordGroup.addChild(dataGroup2);
		assertEquals(defaultRecordGroup.getFirstGroupWithNameInData("childNameInData"), dataGroup2);
	}

	@Test(expectedExceptions = DataMissingException.class, expectedExceptionsMessageRegExp = ""
			+ "Group not found for childNameInData:childNameInData_NOT_FOUND")
	public void testGetFirstGroupWithNameInDataNotFound() {
		defaultRecordGroup
				.addChild(CoraDataAtomic.withNameInDataAndValue("atomicNameInData", "atomicValue"));
		DataGroup dataGroup2 = CoraDataGroup.withNameInData("childNameInData");
		dataGroup2.addChild(CoraDataGroup.withNameInData("grandChildNameInData"));
		defaultRecordGroup.addChild(dataGroup2);
		defaultRecordGroup.getFirstGroupWithNameInData("childNameInData_NOT_FOUND");
	}

	@Test
	public void testGetFirstChildWithNameInData() {
		defaultRecordGroup
				.addChild(CoraDataAtomic.withNameInDataAndValue("atomicNameInData", "atomicValue"));
		DataGroup dataGroup2 = CoraDataGroup.withNameInData("childNameInData");
		dataGroup2.addChild(CoraDataGroup.withNameInData("grandChildNameInData"));
		defaultRecordGroup.addChild(dataGroup2);
		assertEquals(defaultRecordGroup.getFirstChildWithNameInData("childNameInData"), dataGroup2);
	}

	@Test(expectedExceptions = DataMissingException.class, expectedExceptionsMessageRegExp = ""
			+ "Element not found for childNameInData:childNameInData_NOT_FOUND")
	public void testGetFirstChildWithNameInDataNotFound() {
		defaultRecordGroup
				.addChild(CoraDataAtomic.withNameInDataAndValue("atomicNameInData", "atomicValue"));
		DataGroup dataGroup2 = CoraDataGroup.withNameInData("childNameInData");
		dataGroup2.addChild(CoraDataGroup.withNameInData("grandChildNameInData"));
		defaultRecordGroup.addChild(dataGroup2);
		defaultRecordGroup.getFirstChildWithNameInData("childNameInData_NOT_FOUND");
	}

	@Test
	public void testGetAllGroupsWithNameInData() {
		defaultRecordGroup
				.addChild(CoraDataAtomic.withNameInDataAndValue("atomicNameInData", "atomicValue"));
		addTwoGroupChildrenWithSameNameInData(defaultRecordGroup);

		List<DataGroup> groupsFound = defaultRecordGroup
				.getAllGroupsWithNameInData("childNameInData");
		assertEquals(groupsFound.size(), 2);
	}

	private void addTwoGroupChildrenWithSameNameInData(DataRecordGroup parentDataGroup) {
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
		defaultRecordGroup
				.addChild(CoraDataAtomic.withNameInDataAndValue("atomicNameInData", "atomicValue"));

		List<DataGroup> groupsFound = defaultRecordGroup
				.getAllGroupsWithNameInData("childNameInData");
		assertEquals(groupsFound.size(), 0);
	}

	@Test
	public void testGetAllGroupsWithNameInDataAndAttributesOneMatch() {
		DataRecordGroup dataGroup = CoraDataRecordGroup.withNameInData("someDataGroup");
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
			DataRecordGroup dataGroup) {
		addAndReturnDataGroupChildWithNameInData(dataGroup, "groupId2");
		addAndReturnDataGroupChildWithNameInData(dataGroup, "groupId3");
		addAndReturnDataGroupChildWithNameInData(dataGroup, "groupId2");
		DataGroup child3 = addAndReturnDataGroupChildWithNameInDataAndAttributes(dataGroup,
				"groupId2", CoraDataAttribute.withNameInDataAndValue("nameInData", "value1"));
		return child3;
	}

	private DataGroup addAndReturnDataGroupChildWithNameInData(DataRecordGroup dataGroup,
			String nameInData) {
		DataGroup child = CoraDataGroup.withNameInData(nameInData);
		dataGroup.addChild(child);
		return child;
	}

	private DataGroup addAndReturnDataGroupChildWithNameInDataAndAttributes(
			DataRecordGroup dataGroup, String nameInData, CoraDataAttribute... attributes) {
		DataGroup child = CoraDataGroup.withNameInData(nameInData);
		dataGroup.addChild(child);
		for (CoraDataAttribute attribute : attributes) {
			child.addAttributeByIdWithValue(attribute.getNameInData(), attribute.getValue());
		}
		return child;
	}

	@Test
	public void testGetAllGroupsWithNameInDataAndAttributesTwoMatches() {
		DataRecordGroup dataGroup = CoraDataRecordGroup.withNameInData("someDataGroup");
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
		DataRecordGroup dataGroup = CoraDataRecordGroup.withNameInData("someDataGroup");
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
		DataRecordGroup dataGroup = CoraDataRecordGroup.withNameInData("someDataGroup");
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
		DataRecordGroup dataGroup = CoraDataRecordGroup.withNameInData("someDataGroup");
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
		DataRecordGroup dataGroup = CoraDataRecordGroup.withNameInData("someDataGroup");
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
		DataRecordGroup dataGroup = CoraDataRecordGroup.withNameInData("someDataGroup");
		createAndAddAnAtomicChildToDataGroup(dataGroup);
		boolean childWasRemoved = dataGroup.removeFirstChildWithNameInData("childId");
		assertTrue(childWasRemoved);
		assertFalse(dataGroup.containsChildWithNameInData("childId"));
	}

	@Test
	public void testRemoveChildMoreThanOneChildExist() {
		DataRecordGroup dataGroup = CoraDataRecordGroup.withNameInData("someDataGroup");
		createAndAddAnAtomicChildToDataGroup(dataGroup);
		createAndAddAnAtomicChildToDataGroup(dataGroup);
		boolean childWasRemoved = dataGroup.removeFirstChildWithNameInData("childId");
		assertTrue(childWasRemoved);
		assertTrue(dataGroup.containsChildWithNameInData("childId"));
	}

	@Test
	public void testRemoveChildNotFound() {
		DataRecordGroup dataGroup = CoraDataRecordGroup.withNameInData("someDataGroup");
		createAndAddAnAtomicChildToDataGroup(dataGroup);
		boolean childWasRemoved = dataGroup.removeFirstChildWithNameInData("childId_NOTFOUND");
		assertFalse(childWasRemoved);
	}

	private DataChild createAndAddAnAtomicChildToDataGroup(DataRecordGroup dataGroup) {
		return createAndAddAnAtomicChildToDataGroupUsingNameInData(dataGroup, "childId");
	}

	private DataChild createAndAddAnAtomicChildToDataGroupUsingNameInData(DataRecordGroup dataGroup,
			String nameInData) {
		DataChild child = CoraDataAtomic.withNameInDataAndValue(nameInData, "child value");
		dataGroup.addChild(child);
		return child;
	}

	@Test
	public void testRemoveAllChildrenWithNameInData() {
		DataRecordGroup dataGroup = CoraDataRecordGroup.withNameInData("someDataGroup");
		createAndAddAnAtomicChildWithRepeatIdToDataGroup(dataGroup, "0");
		createAndAddAnAtomicChildWithRepeatIdToDataGroup(dataGroup, "1");
		boolean childWasRemoved = dataGroup.removeAllChildrenWithNameInData("childId");
		assertTrue(childWasRemoved);
		assertFalse(dataGroup.containsChildWithNameInData("childId"));
	}

	private DataChild createAndAddAnAtomicChildWithRepeatIdToDataGroup(DataRecordGroup dataGroup,
			String repeatId) {
		DataChild child = CoraDataAtomic.withNameInDataAndValueAndRepeatId("childId", "child value",
				repeatId);
		dataGroup.addChild(child);
		return child;
	}

	@Test
	public void testRemoveAllChildrenWithNameInDataWhenOtherChildrenExist() {
		DataRecordGroup dataGroup = CoraDataRecordGroup.withNameInData("someDataGroup");
		createAndAddAnAtomicChildWithRepeatIdToDataGroup(dataGroup, "0");
		createAndAddAnAtomicChildWithRepeatIdToDataGroup(dataGroup, "1");
		createAndAddAnAtomicChildToDataGroupUsingNameInData(dataGroup, "someOtherChildId");

		boolean childWasRemoved = dataGroup.removeAllChildrenWithNameInData("childId");
		assertTrue(childWasRemoved);
		assertFalse(dataGroup.containsChildWithNameInData("childId"));
		assertTrue(dataGroup.containsChildWithNameInData("someOtherChildId"));
	}

	@Test
	public void testRemoveAllChildNotFound() {
		DataRecordGroup dataGroup = CoraDataRecordGroup.withNameInData("someDataGroup");
		createAndAddAnAtomicChildToDataGroup(dataGroup);
		assertFalse(dataGroup.removeAllChildrenWithNameInData("childId_NOTFOUND"));
	}

	@Test
	public void testGetAllChildrenWithNameInDataNoChildren() {
		DataRecordGroup dataGroup = CoraDataRecordGroup.withNameInData("someDataGroup");
		List<DataChild> allChildrenWithNameInData = dataGroup
				.getAllChildrenWithNameInData("someChildNameInData");
		assertTrue(allChildrenWithNameInData.isEmpty());

	}

	@Test
	public void testGetAllChildrenWithNameInDataNoMatchingChildren() {
		DataRecordGroup dataGroup = CoraDataRecordGroup.withNameInData("someDataGroup");
		createAndAddAtomicChild(dataGroup, "someChildNameInData", "0");
		List<DataChild> allChildrenWithNameInData = dataGroup
				.getAllChildrenWithNameInData("someOtherChildNameInData");
		assertTrue(allChildrenWithNameInData.isEmpty());

	}

	@Test
	public void testGetAllChildrenWithNameInDataOneMatchingAtomicChild() {
		DataRecordGroup dataGroup = CoraDataRecordGroup.withNameInData("someDataGroup");
		CoraDataAtomic atomicChild = createAndAddAtomicChild(dataGroup, "someChildNameInData", "0");

		List<DataChild> allChildrenWithNameInData = dataGroup
				.getAllChildrenWithNameInData("someChildNameInData");
		assertEquals(allChildrenWithNameInData.size(), 1);
		assertSame(allChildrenWithNameInData.get(0), atomicChild);
	}

	private CoraDataAtomic createAndAddAtomicChild(DataRecordGroup dataGroup, String nameInData,
			String repeatId) {
		CoraDataAtomic atomicChild = CoraDataAtomic.withNameInDataAndValue(nameInData, "someValue");
		atomicChild.setRepeatId(repeatId);
		dataGroup.addChild(atomicChild);
		return atomicChild;
	}

	@Test
	public void testGetAllChildrenWithNameInDataMultipleMatchesDifferentTypes() {
		CoraDataAtomic atomicChild = createAndAddAtomicChild(defaultRecordGroup,
				"someChildNameInData", "0");
		CoraDataAtomic atomicChild2 = createAndAddAtomicChild(defaultRecordGroup,
				"someChildNameInData", "1");
		CoraDataAtomic atomicChild3 = createAndAddAtomicChild(defaultRecordGroup,
				"someNOTChildNameInData", "2");

		DataGroup dataGroupChild = CoraDataGroup.withNameInData("someChildNameInData");
		defaultRecordGroup.addChild(dataGroupChild);

		List<DataChild> allChildrenWithNameInData = defaultRecordGroup
				.getAllChildrenWithNameInData("someChildNameInData");
		assertEquals(allChildrenWithNameInData.size(), 3);
		assertSame(allChildrenWithNameInData.get(0), atomicChild);
		assertSame(allChildrenWithNameInData.get(1), atomicChild2);
		assertSame(allChildrenWithNameInData.get(2), dataGroupChild);
		assertFalse(allChildrenWithNameInData.contains(atomicChild3));
	}

	@Test
	public void testRemoveChildrenWithAttributesNoMatchWrongChildNoAttributes() {
		createAndAddAnAtomicChildWithRepeatIdToDataGroup(defaultRecordGroup, "0");
		boolean childWasRemoved = defaultRecordGroup
				.removeAllChildrenWithNameInDataAndAttributes("NOTchildId");
		assertFalse(childWasRemoved);
		assertTrue(defaultRecordGroup.containsChildWithNameInData("childId"));
	}

	@Test
	public void testRemoveChildrenWithAttributesNoMatchNoAttributes() {
		createAndAddAnAtomicChildWithRepeatIdToDataGroup(defaultRecordGroup, "0");
		boolean childWasRemoved = defaultRecordGroup.removeAllChildrenWithNameInDataAndAttributes(
				"childId", CoraDataAttribute.withNameInDataAndValue("someName", "someValue"));
		assertFalse(childWasRemoved);
		assertTrue(defaultRecordGroup.containsChildWithNameInData("childId"));
	}

	@Test
	public void testRemoveChildrenWithAttributesNoMatchWithWrongAttributes() {
		DataGroup childDataGroup = CoraDataGroup.withNameInData("childId");
		childDataGroup.addAttributeByIdWithValue("someName", "someValue");
		defaultRecordGroup.addChild(childDataGroup);

		boolean childWasRemoved = defaultRecordGroup.removeAllChildrenWithNameInDataAndAttributes(
				"childId", CoraDataAttribute.withNameInDataAndValue("someName", "someOtherValue"));
		assertFalse(childWasRemoved);
		assertTrue(defaultRecordGroup.containsChildWithNameInData("childId"));
	}

	@Test
	public void testRemoveChildrenWithAttributesOneMatchNoAttributes() {
		createAndAddAnAtomicChildWithRepeatIdToDataGroup(defaultRecordGroup, "0");
		boolean childWasRemoved = defaultRecordGroup
				.removeAllChildrenWithNameInDataAndAttributes("childId");
		assertTrue(childWasRemoved);
	}

	@Test
	public void testRemoveChildrenWithAttributesOneMatchWithAttributes() {
		DataGroup childDataGroup = CoraDataGroup.withNameInData("childId");
		childDataGroup.addAttributeByIdWithValue("someName", "someValue");
		defaultRecordGroup.addChild(childDataGroup);
		boolean childWasRemoved = defaultRecordGroup.removeAllChildrenWithNameInDataAndAttributes(
				"childId", CoraDataAttribute.withNameInDataAndValue("someName", "someValue"));
		assertTrue(childWasRemoved);
		assertFalse(defaultRecordGroup.containsChildWithNameInData("childId"));
	}

	@Test
	public void testRemoveChildrenWithAttributesTwoChildrenOneMatchWithAttributes() {
		setUpDataGroupWithTwoChildrenOneWithAttributes();

		assertEquals(defaultRecordGroup.getAllChildrenWithNameInData("childId").size(), 2);

		boolean childWasRemoved = defaultRecordGroup.removeAllChildrenWithNameInDataAndAttributes(
				"childId", CoraDataAttribute.withNameInDataAndValue("someName", "someValue"));

		assertTrue(childWasRemoved);
		assertTrue(defaultRecordGroup.containsChildWithNameInData("childId"));

		List<DataChild> allChildrenWithNameInData = defaultRecordGroup
				.getAllChildrenWithNameInData("childId");
		assertEquals(allChildrenWithNameInData.size(), 1);
		assertTrue(allChildrenWithNameInData.get(0) instanceof CoraDataAtomic);

	}

	@Test
	public void testRemoveChildrenWithAttributesTwoChildrenOneMatchWithoutAttributes() {
		setUpDataGroupWithTwoChildrenOneWithAttributes();

		boolean childWasRemoved = defaultRecordGroup
				.removeAllChildrenWithNameInDataAndAttributes("childId");
		assertTrue(childWasRemoved);
		assertTrue(defaultRecordGroup.containsChildWithNameInData("childId"));

		List<DataChild> allChildrenWithNameInData = defaultRecordGroup
				.getAllChildrenWithNameInData("childId");
		assertEquals(allChildrenWithNameInData.size(), 1);
		assertTrue(allChildrenWithNameInData.get(0) instanceof CoraDataGroup);
	}

	private void setUpDataGroupWithTwoChildrenOneWithAttributes() {
		DataGroup childDataGroup = CoraDataGroup.withNameInData("childId");
		childDataGroup.addAttributeByIdWithValue("someName", "someValue");
		defaultRecordGroup.addChild(childDataGroup);
		createAndAddAnAtomicChildWithRepeatIdToDataGroup(defaultRecordGroup, "0");
	}

	@Test
	public void testRemoveChildrenWithAttributesTwoChildrenNoMatchWithAttributes() {
		setUpDataGroupWithTwoChildrenOneWithAttributes();

		boolean childWasRemoved = defaultRecordGroup.removeAllChildrenWithNameInDataAndAttributes(
				"childId", CoraDataAttribute.withNameInDataAndValue("someNOTName", "someValue"));
		assertFalse(childWasRemoved);
		assertTrue(defaultRecordGroup.containsChildWithNameInData("childId"));

		List<DataChild> allChildrenWithNameInData = defaultRecordGroup
				.getAllChildrenWithNameInData("childId");
		assertEquals(allChildrenWithNameInData.size(), 2);
		assertTrue(allChildrenWithNameInData.get(0) instanceof CoraDataGroup);
		assertTrue(allChildrenWithNameInData.get(1) instanceof CoraDataAtomic);
	}

	@Test
	public void testRemoveChildrenWithAttributesMultipleChildrenTwoMatchesWithAttributes() {
		setUpDataGroupWithMultipleChildrenWithAttributesAndWithoutAttributes();

		boolean childWasRemoved = defaultRecordGroup.removeAllChildrenWithNameInDataAndAttributes(
				"childId", CoraDataAttribute.withNameInDataAndValue("someName", "someValue"));
		assertTrue(childWasRemoved);
		assertTrue(defaultRecordGroup.containsChildWithNameInData("childId"));

		List<DataChild> allChildrenWithNameInData = defaultRecordGroup
				.getAllChildrenWithNameInData("childId");
		assertEquals(allChildrenWithNameInData.size(), 3);
		assertTrue(allChildrenWithNameInData.get(0) instanceof CoraDataAtomic);
		assertTrue(allChildrenWithNameInData.get(1) instanceof CoraDataAtomic);
		assertTrue(allChildrenWithNameInData.get(2) instanceof CoraDataGroup);

		assertEquals(defaultRecordGroup.getAllChildrenWithNameInData("childOtherId").size(), 1);
	}

	private void setUpDataGroupWithMultipleChildrenWithAttributesAndWithoutAttributes() {
		DataGroup childDataGroupWithAttribute = createChildGroupWithNameInDataAndRepatIdAndAttributes(
				"childId", "0");
		defaultRecordGroup.addChild(childDataGroupWithAttribute);
		DataGroup childDataGroupWithAttribute2 = createChildGroupWithNameInDataAndRepatIdAndAttributes(
				"childId", "1");
		defaultRecordGroup.addChild(childDataGroupWithAttribute2);

		createAndAddAnAtomicChildWithRepeatIdToDataGroup(defaultRecordGroup, "0");
		createAndAddAnAtomicChildWithRepeatIdToDataGroup(defaultRecordGroup, "1");

		DataGroup childDataGroupWithAtttributeOtherName = createChildGroupWithNameInDataAndRepatIdAndAttributes(
				"childOtherId", "0");
		defaultRecordGroup.addChild(childDataGroupWithAtttributeOtherName);

		DataGroup childDataGroupWithExtraAttribute = createChildGroupWithNameInDataAndRepatIdAndAttributes(
				"childId", "0");
		childDataGroupWithExtraAttribute.addAttributeByIdWithValue("someOtherName", "someValue");
		defaultRecordGroup.addChild(childDataGroupWithExtraAttribute);
	}

	private DataGroup createChildGroupWithNameInDataAndRepatIdAndAttributes(String nameInData,
			String repeatId) {
		DataGroup childDataGroup = CoraDataGroup.withNameInData(nameInData);
		childDataGroup.addAttributeByIdWithValue("someName", "someValue");
		childDataGroup.setRepeatId(repeatId);
		return childDataGroup;
	}

	@Test
	public void testGetAllChildrenWithNameInDataAndAttributesNoMatch() {
		DataGroup childGroup = createChildGroupWithNameInDataAndRepatIdAndAttributes(
				"someChildNameInData", "0");
		defaultRecordGroup.addChild(childGroup);

		List<DataChild> children = defaultRecordGroup
				.getAllChildrenWithNameInDataAndAttributes("someChildNameInData");

		assertTrue(children.isEmpty());
	}

	@Test
	public void testGetAllChildrenWithNameInDataAndAttributesNoMatchNotMatchingNameInData() {
		DataGroup childGroup = createChildGroupWithNameInDataAndRepatIdAndAttributes(
				"someChildNameInData", "0");
		defaultRecordGroup.addChild(childGroup);

		CoraDataAttribute attribute = CoraDataAttribute.withNameInDataAndValue("someName",
				"someValue");
		List<DataChild> children = defaultRecordGroup
				.getAllChildrenWithNameInDataAndAttributes("someOtherChildNameInData", attribute);

		assertTrue(children.isEmpty());
	}

	@Test
	public void testGetAllChildrenWithNameInDataAndAttributesMatch() {
		DataGroup childGroup = createChildGroupWithNameInDataAndRepatIdAndAttributes(
				"someChildNameInData", "0");
		defaultRecordGroup.addChild(childGroup);

		CoraDataAttribute attribute = CoraDataAttribute.withNameInDataAndValue("someName",
				"someValue");

		List<DataChild> children = defaultRecordGroup
				.getAllChildrenWithNameInDataAndAttributes("someChildNameInData", attribute);

		assertEquals(children.size(), 1);

		assertSame(children.get(0), childGroup);
	}

	@Test
	public void testGetAllChildrenWithNameInDataAndAttributesDataAtomicChild() {
		DataGroup childGroup = createChildGroupWithNameInDataAndRepatIdAndAttributes(
				"someChildNameInData", "0");
		defaultRecordGroup.addChild(childGroup);

		CoraDataAtomic coraDataAtomic = CoraDataAtomic.withNameInDataAndValue("someChildNameInData",
				"someValue");

		defaultRecordGroup.addChild(coraDataAtomic);

		List<DataChild> children = defaultRecordGroup
				.getAllChildrenWithNameInDataAndAttributes("someChildNameInData");

		assertEquals(children.size(), 1);
		assertSame(children.get(0), coraDataAtomic);

	}

	@Test
	public void testGetAllChildrenWithNameInDataAndAttributesMatchRepeatingGroup() {
		DataGroup childGroup = createChildGroupWithNameInDataAndRepatIdAndAttributes(
				"someChildNameInData", "0");
		defaultRecordGroup.addChild(childGroup);

		DataGroup childGroup2 = createChildGroupWithNameInDataAndRepatIdAndAttributes(
				"someChildNameInData", "1");
		defaultRecordGroup.addChild(childGroup2);

		CoraDataAttribute attribute = CoraDataAttribute.withNameInDataAndValue("someName",
				"someValue");

		List<DataChild> children = defaultRecordGroup
				.getAllChildrenWithNameInDataAndAttributes("someChildNameInData", attribute);

		assertEquals(children.size(), 2);

		assertSame(children.get(0), childGroup);
		assertSame(children.get(1), childGroup2);
	}

	@Test
	public void testGetAllChildrenWithNameInDataAndAttributesMultipleChildrenMatchOneGroup() {
		DataGroup childGroup = createChildGroupWithNameInDataAndRepatIdAndAttributes(
				"someChildNameInData", "0");
		defaultRecordGroup.addChild(childGroup);

		DataGroup childGroupOtherNameInData = createChildGroupWithNameInDataAndRepatIdAndAttributes(
				"someOtherChildNameInData", "1");
		defaultRecordGroup.addChild(childGroupOtherNameInData);

		DataGroup childGroup2 = CoraDataGroup.withNameInData("someChildNameInData");
		defaultRecordGroup.addChild(childGroup2);

		CoraDataAttribute attribute = CoraDataAttribute.withNameInDataAndValue("someName",
				"someValue");

		List<DataChild> childrenWithAttributes = defaultRecordGroup
				.getAllChildrenWithNameInDataAndAttributes("someChildNameInData", attribute);

		assertEquals(childrenWithAttributes.size(), 1);
		assertSame(childrenWithAttributes.get(0), childGroup);

		List<DataChild> childrenWithoutAttributes = defaultRecordGroup
				.getAllChildrenWithNameInDataAndAttributes("someChildNameInData");

		assertEquals(childrenWithoutAttributes.size(), 1);
		assertSame(childrenWithoutAttributes.get(0), childGroup2);
	}

	@Test(expectedExceptions = DataMissingException.class, expectedExceptionsMessageRegExp = ""
			+ "Group not found for childNameInData:recordInfo")
	public void testGetType_NoRecordInfo() throws Exception {
		defaultRecordGroup.getType();
	}

	@Test(expectedExceptions = DataMissingException.class, expectedExceptionsMessageRegExp = ""
			+ "Element not found for childNameInData:type")
	public void testGetType_NoTypeLink() throws Exception {
		defaultRecordGroup.addChild(CoraDataGroup.withNameInData(RECORD_INFO));

		defaultRecordGroup.getType();
	}

	@Test
	public void testGetType() throws Exception {
		CoraDataGroup recordInfo = CoraDataGroup.withNameInData(RECORD_INFO);
		defaultRecordGroup.addChild(recordInfo);
		recordInfo
				.addChild(CoraDataRecordLink.usingNameInDataAndTypeAndId("type", "", "someTypeId"));

		assertEquals(defaultRecordGroup.getType(), "someTypeId");
	}

	@Test
	public void testSetType() throws Exception {
		CoraDataGroup recordInfo = CoraDataGroup.withNameInData(RECORD_INFO);
		defaultRecordGroup.addChild(recordInfo);
		recordInfo
				.addChild(CoraDataRecordLink.usingNameInDataAndTypeAndId("type", "", "someTypeId"));

		defaultRecordGroup.setType("someOtherTypeId");

		assertEquals(defaultRecordGroup.getType(), "someOtherTypeId");
	}

	@Test
	public void testSetType_NoType() throws Exception {
		CoraDataGroup recordInfo = CoraDataGroup.withNameInData(RECORD_INFO);
		defaultRecordGroup.addChild(recordInfo);

		defaultRecordGroup.setType("someOtherTypeId");

		assertEquals(defaultRecordGroup.getType(), "someOtherTypeId");
		DataRecordLink dataDividerLink = (DataRecordLink) recordInfo
				.getFirstChildWithNameInData("type");
		assertEquals(dataDividerLink.getLinkedRecordType(), "recordType");
	}

	@Test
	public void testSetType_NoRecordInfo() throws Exception {
		defaultRecordGroup.setType("someOtherTypeId");

		assertEquals(defaultRecordGroup.getType(), "someOtherTypeId");
	}

	@Test(expectedExceptions = DataMissingException.class, expectedExceptionsMessageRegExp = ""
			+ "Group not found for childNameInData:recordInfo")
	public void testGetId_NoRecordInfo() throws Exception {
		defaultRecordGroup.getId();
	}

	@Test(expectedExceptions = DataMissingException.class, expectedExceptionsMessageRegExp = ""
			+ "Atomic value not found for childNameInData:id")
	public void testGetId_NoIdLink() throws Exception {
		defaultRecordGroup.addChild(CoraDataGroup.withNameInData(RECORD_INFO));

		defaultRecordGroup.getId();
	}

	@Test
	public void testGetId() throws Exception {
		CoraDataGroup recordInfo = CoraDataGroup.withNameInData(RECORD_INFO);
		defaultRecordGroup.addChild(recordInfo);
		recordInfo.addChild(CoraDataAtomic.withNameInDataAndValue("id", "someId"));

		assertEquals(defaultRecordGroup.getId(), "someId");
	}

	@Test
	public void testSetId() throws Exception {
		CoraDataGroup recordInfo = CoraDataGroup.withNameInData(RECORD_INFO);
		defaultRecordGroup.addChild(recordInfo);
		recordInfo.addChild(CoraDataRecordLink.usingNameInDataAndTypeAndId("id", "", "someIdId"));

		defaultRecordGroup.setId("someOtherId");

		assertEquals(defaultRecordGroup.getId(), "someOtherId");
	}

	@Test
	public void testSetId_NoId() throws Exception {
		CoraDataGroup recordInfo = CoraDataGroup.withNameInData(RECORD_INFO);
		defaultRecordGroup.addChild(recordInfo);

		defaultRecordGroup.setId("someOtherId");

		assertEquals(defaultRecordGroup.getId(), "someOtherId");
		DataAtomic id = (DataAtomic) recordInfo.getFirstChildWithNameInData("id");
		assertEquals(id.getValue(), "someOtherId");
	}

	@Test
	public void testSetId_NoRecordInfo() throws Exception {
		defaultRecordGroup.setId("someOtherId");

		assertEquals(defaultRecordGroup.getId(), "someOtherId");
	}

	@Test(expectedExceptions = DataMissingException.class, expectedExceptionsMessageRegExp = ""
			+ "Group not found for childNameInData:recordInfo")
	public void testGetDataDivider_NoRecordInfo() throws Exception {
		defaultRecordGroup.getDataDivider();
	}

	@Test(expectedExceptions = DataMissingException.class, expectedExceptionsMessageRegExp = ""
			+ "Element not found for childNameInData:dataDivider")
	public void testGetDataDivider_NoDataDividerLink() throws Exception {
		defaultRecordGroup.addChild(CoraDataGroup.withNameInData(RECORD_INFO));

		defaultRecordGroup.getDataDivider();
	}

	@Test
	public void testGetDataDivider() throws Exception {
		CoraDataGroup recordInfo = CoraDataGroup.withNameInData(RECORD_INFO);
		defaultRecordGroup.addChild(recordInfo);
		recordInfo.addChild(CoraDataRecordLink.usingNameInDataAndTypeAndId("dataDivider", "",
				"someDataDividerId"));

		assertEquals(defaultRecordGroup.getDataDivider(), "someDataDividerId");
	}

	@Test
	public void testSetDataDivider() throws Exception {
		CoraDataGroup recordInfo = CoraDataGroup.withNameInData(RECORD_INFO);
		defaultRecordGroup.addChild(recordInfo);
		recordInfo.addChild(CoraDataRecordLink.usingNameInDataAndTypeAndId("dataDivider", "",
				"someDataDividerId"));

		defaultRecordGroup.setDataDivider("someOtherDataDividerId");

		assertEquals(defaultRecordGroup.getDataDivider(), "someOtherDataDividerId");
	}

	@Test
	public void testSetDataDivider_NoDataDivider() throws Exception {
		CoraDataGroup recordInfo = CoraDataGroup.withNameInData(RECORD_INFO);
		defaultRecordGroup.addChild(recordInfo);

		defaultRecordGroup.setDataDivider("someOtherDataDividerId");

		assertEquals(defaultRecordGroup.getDataDivider(), "someOtherDataDividerId");
		DataRecordLink dataDividerLink = (DataRecordLink) recordInfo
				.getFirstChildWithNameInData("dataDivider");
		assertEquals(dataDividerLink.getLinkedRecordType(), "system");
	}

	@Test
	public void testSetDataDivider_NoRecordInfo() throws Exception {
		defaultRecordGroup.setDataDivider("someOtherDataDividerId");

		assertEquals(defaultRecordGroup.getDataDivider(), "someOtherDataDividerId");
	}

	@Test(expectedExceptions = DataMissingException.class, expectedExceptionsMessageRegExp = ""
			+ "Group not found for childNameInData:recordInfo")
	public void testGetValidationType_NoRecordInfo() throws Exception {
		defaultRecordGroup.getValidationType();
	}

	@Test(expectedExceptions = DataMissingException.class, expectedExceptionsMessageRegExp = ""
			+ "Element not found for childNameInData:validationType")
	public void testGetValidationType_NoValidationTypeLink() throws Exception {
		defaultRecordGroup.addChild(CoraDataGroup.withNameInData(RECORD_INFO));

		defaultRecordGroup.getValidationType();
	}

	@Test
	public void testGetValidationType() throws Exception {
		CoraDataGroup recordInfo = CoraDataGroup.withNameInData(RECORD_INFO);
		defaultRecordGroup.addChild(recordInfo);
		recordInfo.addChild(CoraDataRecordLink.usingNameInDataAndTypeAndId(VALIDATION_TYPE, "",
				"someValidationTypeId"));

		assertEquals(defaultRecordGroup.getValidationType(), "someValidationTypeId");
	}

	@Test
	public void testSetValidationType() throws Exception {
		CoraDataGroup recordInfo = CoraDataGroup.withNameInData(RECORD_INFO);
		defaultRecordGroup.addChild(recordInfo);
		recordInfo.addChild(CoraDataRecordLink.usingNameInDataAndTypeAndId(VALIDATION_TYPE, "",
				"someValidationTypeId"));

		defaultRecordGroup.setValidationType("someOtherValidationTypeId");

		assertEquals(defaultRecordGroup.getValidationType(), "someOtherValidationTypeId");
	}

	@Test
	public void testSetValidationType_NoValidationType() throws Exception {
		CoraDataGroup recordInfo = CoraDataGroup.withNameInData(RECORD_INFO);
		defaultRecordGroup.addChild(recordInfo);

		defaultRecordGroup.setValidationType("someOtherValidationTypeId");

		assertEquals(defaultRecordGroup.getValidationType(), "someOtherValidationTypeId");
		DataRecordLink ValidationTypeLink = (DataRecordLink) recordInfo
				.getFirstChildWithNameInData(VALIDATION_TYPE);
		assertEquals(ValidationTypeLink.getLinkedRecordType(), VALIDATION_TYPE);
	}

	@Test
	public void testSetValidationType_NoRecordInfo() throws Exception {
		defaultRecordGroup.setValidationType("someOtherValidationTypeId");

		assertEquals(defaultRecordGroup.getValidationType(), "someOtherValidationTypeId");
	}
}