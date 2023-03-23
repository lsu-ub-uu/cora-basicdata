/*
 * Copyright 2022 Uppsala University Library
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

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataAttribute;
import se.uu.ub.cora.data.DataChildFilter;
import se.uu.ub.cora.data.spies.DataAttributeSpy;
import se.uu.ub.cora.data.spies.DataChildSpy;

public class DataChildFilterTest {

	String nameInData = "someNameInData";
	DataChildFilter filterOnlyNameInData;
	private DataChildSpy childEmptyNameInData;
	private DataChildSpy childOneAttribute;

	@BeforeMethod
	public void setUp() {
		filterOnlyNameInData = CoraDataChildFilter.usingNameInData(nameInData);
		childEmptyNameInData = new DataChildSpy();

		childOneAttribute = new DataChildSpy();
		childOneAttribute.MRV.setDefaultReturnValuesSupplier("getNameInData",
				(Supplier<String>) () -> nameInData);
		DataAttributeSpy attributeOne = createAttribute("attributeOne", "attributeOneValue");
		List<DataAttribute> attributes = List.of(attributeOne);
		childOneAttribute.MRV.setDefaultReturnValuesSupplier("getAttributes",
				(Supplier<List<DataAttribute>>) () -> attributes);
		childOneAttribute.MRV.setDefaultReturnValuesSupplier("hasAttributes",
				(Supplier<Boolean>) () -> true);
	}

	private DataAttributeSpy createAttribute(String name, String value) {
		DataAttributeSpy attributeOne = new DataAttributeSpy();
		attributeOne.MRV.setDefaultReturnValuesSupplier("getNameInData",
				(Supplier<String>) () -> name);
		attributeOne.MRV.setDefaultReturnValuesSupplier("getValue", (Supplier<String>) () -> value);
		return attributeOne;
	}

	@Test
	public void childDoesNotMatchNameInData() throws Exception {
		boolean childMatches = filterOnlyNameInData.childMatches(childEmptyNameInData);

		assertFalse(childMatches);
		childEmptyNameInData.MCR.assertMethodWasCalled("getNameInData");
		childEmptyNameInData.MCR.assertMethodNotCalled("getAttributes");
	}

	@Test
	public void childMatchesNameInDataNoAttributes() throws Exception {
		childEmptyNameInData.MRV.setDefaultReturnValuesSupplier("getNameInData",
				(Supplier<String>) () -> nameInData);

		boolean childMatches = filterOnlyNameInData.childMatches(childEmptyNameInData);

		assertTrue(childMatches);
		childEmptyNameInData.MCR.assertMethodWasCalled("getNameInData");
	}

	@Test
	public void childHasAttributesFilterDoesNot() throws Exception {
		boolean childMatches = filterOnlyNameInData.childMatches(childOneAttribute);

		assertFalse(childMatches);
		childOneAttribute.MCR.assertMethodWasCalled("getNameInData");
		childEmptyNameInData.MCR.assertMethodNotCalled("getAttributes");
	}

	@Test
	public void childHasNoAttributesFilterDoes() throws Exception {
		childEmptyNameInData.MRV.setDefaultReturnValuesSupplier("getNameInData",
				(Supplier<String>) () -> nameInData);

		filterOnlyNameInData.addAttributeUsingNameInDataAndPossibleValues("attributeOne",
				Set.of("someValue"));

		boolean childMatches = filterOnlyNameInData.childMatches(childEmptyNameInData);

		assertFalse(childMatches);
		childEmptyNameInData.MCR.assertMethodWasCalled("getNameInData");

		childEmptyNameInData.MCR.assertMethodWasCalled("hasAttributes");
		childEmptyNameInData.MCR.assertMethodNotCalled("getAttributes");
	}

	@Test
	public void childHasAttributesFilterDoesAndMatchEachOther() throws Exception {
		filterOnlyNameInData.addAttributeUsingNameInDataAndPossibleValues("attributeOne",
				Set.of("attributeOneValue"));

		boolean childMatches = filterOnlyNameInData.childMatches(childOneAttribute);

		assertTrue(childMatches);
		assertDataFetchedFromChildOneAttribute();
	}

	private void assertDataFetchedFromChildOneAttribute() {
		childOneAttribute.MCR.assertMethodWasCalled("getNameInData");

		childOneAttribute.MCR.assertMethodWasCalled("hasAttributes");
		childOneAttribute.MCR.assertMethodWasCalled("getAttributes");
	}

	@Test
	public void childHasAttributesFilterDoes_DifferentNameOfAttributes() throws Exception {
		filterOnlyNameInData.addAttributeUsingNameInDataAndPossibleValues("NOTattributeOne",
				Set.of("attributeOneValue"));

		boolean childMatches = filterOnlyNameInData.childMatches(childOneAttribute);

		assertFalse(childMatches);
		assertDataFetchedFromChildOneAttribute();
	}

	@Test
	public void childHasAttributesFilterDoes_NotInValuesOfAttributes() throws Exception {
		filterOnlyNameInData.addAttributeUsingNameInDataAndPossibleValues("attributeOne",
				Set.of("NOTattributeOneValue"));

		boolean childMatches = filterOnlyNameInData.childMatches(childOneAttribute);

		assertFalse(childMatches);
		assertDataFetchedFromChildOneAttribute();
	}

	@Test
	public void childHasAttributesFilterDoes_InSetOfValuesOfAttributes() throws Exception {
		filterOnlyNameInData.addAttributeUsingNameInDataAndPossibleValues("attributeOne",
				Set.of("NOTattributeOneValue", "attributeOneValue"));

		boolean childMatches = filterOnlyNameInData.childMatches(childOneAttribute);

		assertTrue(childMatches);
		assertDataFetchedFromChildOneAttribute();
	}

	@Test
	public void childHasAttributesFilterDoes_childHasOneMoreAttributes() throws Exception {
		DataAttributeSpy attributeOne = createAttribute("attributeOne", "attributeOneValue");
		DataAttributeSpy attributeTwo = createAttribute("attributeTwo", "attributeTwoValue");
		List<DataAttribute> attributes = List.of(attributeOne, attributeTwo);
		childOneAttribute.MRV.setDefaultReturnValuesSupplier("getAttributes",
				(Supplier<List<DataAttribute>>) () -> attributes);
		filterOnlyNameInData.addAttributeUsingNameInDataAndPossibleValues("attributeOne",
				Set.of("attributeOneValue"));

		boolean childMatches = filterOnlyNameInData.childMatches(childOneAttribute);

		assertFalse(childMatches);
		assertDataFetchedFromChildOneAttribute();
	}

	@Test
	public void childHasAttributesFilterDoes_filterHasOneMoreAttributes() throws Exception {
		filterOnlyNameInData.addAttributeUsingNameInDataAndPossibleValues("attributeOne",
				Set.of("attributeOneValue"));
		filterOnlyNameInData.addAttributeUsingNameInDataAndPossibleValues("attributeTwo",
				Set.of("attributeTwoValue"));

		boolean childMatches = filterOnlyNameInData.childMatches(childOneAttribute);

		assertFalse(childMatches);
		assertDataFetchedFromChildOneAttribute();
	}

	@Test
	public void childHasAttributesFilterDoes_twoMatchingAttributes() throws Exception {
		DataAttributeSpy attributeOne = createAttribute("attributeOne", "attributeOneValue");
		DataAttributeSpy attributeTwo = createAttribute("attributeTwo", "attributeTwoValue");
		List<DataAttribute> attributes = List.of(attributeOne, attributeTwo);
		childOneAttribute.MRV.setDefaultReturnValuesSupplier("getAttributes",
				(Supplier<List<DataAttribute>>) () -> attributes);
		filterOnlyNameInData.addAttributeUsingNameInDataAndPossibleValues("attributeOne",
				Set.of("attributeOneValue"));
		filterOnlyNameInData.addAttributeUsingNameInDataAndPossibleValues("attributeTwo",
				Set.of("attributeTwoValue"));

		boolean childMatches = filterOnlyNameInData.childMatches(childOneAttribute);

		assertTrue(childMatches);
		assertDataFetchedFromChildOneAttribute();
	}

	@Test
	public void childHasAttributesFilterDoes_twoAttributesDifferentNameOnOne() throws Exception {
		DataAttributeSpy attributeOne = createAttribute("attributeOne", "attributeOneValue");
		DataAttributeSpy attributeTwo = createAttribute("NOTattributeTwo", "attributeTwoValue");
		List<DataAttribute> attributes = List.of(attributeOne, attributeTwo);
		childOneAttribute.MRV.setDefaultReturnValuesSupplier("getAttributes",
				(Supplier<List<DataAttribute>>) () -> attributes);
		filterOnlyNameInData.addAttributeUsingNameInDataAndPossibleValues("attributeOne",
				Set.of("attributeOneValue"));
		filterOnlyNameInData.addAttributeUsingNameInDataAndPossibleValues("attributeTwo",
				Set.of("attributeTwoValue"));

		boolean childMatches = filterOnlyNameInData.childMatches(childOneAttribute);

		assertFalse(childMatches);
		assertDataFetchedFromChildOneAttribute();
	}

	@Test
	public void childHasAttributesFilterDoes_twoAttributesDifferentValueOnOne() throws Exception {
		DataAttributeSpy attributeOne = createAttribute("attributeOne", "attributeOneValue");
		DataAttributeSpy attributeTwo = createAttribute("attributeTwo", "NOTattributeTwoValue");
		List<DataAttribute> attributes = List.of(attributeOne, attributeTwo);
		childOneAttribute.MRV.setDefaultReturnValuesSupplier("getAttributes",
				(Supplier<List<DataAttribute>>) () -> attributes);
		filterOnlyNameInData.addAttributeUsingNameInDataAndPossibleValues("attributeOne",
				Set.of("attributeOneValue"));
		filterOnlyNameInData.addAttributeUsingNameInDataAndPossibleValues("attributeTwo",
				Set.of("attributeTwoValue"));

		boolean childMatches = filterOnlyNameInData.childMatches(childOneAttribute);

		assertFalse(childMatches);
		assertDataFetchedFromChildOneAttribute();
	}

	@Test
	public void childHasAttributesFilterDoes_twoAttributesWithValueInPossibleValues()
			throws Exception {
		DataAttributeSpy attributeOne = createAttribute("attributeOne", "attributeOneValue");
		DataAttributeSpy attributeTwo = createAttribute("attributeTwo", "AnotherAttributeTwoValue");
		List<DataAttribute> attributes = List.of(attributeOne, attributeTwo);
		childOneAttribute.MRV.setDefaultReturnValuesSupplier("getAttributes",
				(Supplier<List<DataAttribute>>) () -> attributes);
		filterOnlyNameInData.addAttributeUsingNameInDataAndPossibleValues("attributeOne",
				Set.of("attributeOneValue"));
		filterOnlyNameInData.addAttributeUsingNameInDataAndPossibleValues("attributeTwo",
				Set.of("attributeTwoValue", "AnotherAttributeTwoValue"));

		boolean childMatches = filterOnlyNameInData.childMatches(childOneAttribute);

		assertTrue(childMatches);
		assertDataFetchedFromChildOneAttribute();
	}
}
