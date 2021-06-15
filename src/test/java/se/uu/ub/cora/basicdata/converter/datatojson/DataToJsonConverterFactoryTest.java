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

package se.uu.ub.cora.basicdata.converter.datatojson;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertSame;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.basicdata.data.CoraDataAtomic;
import se.uu.ub.cora.basicdata.data.CoraDataAttribute;
import se.uu.ub.cora.basicdata.data.CoraDataGroup;
import se.uu.ub.cora.basicdata.data.CoraDataRecordLink;
import se.uu.ub.cora.basicdata.data.CoraDataResourceLink;
import se.uu.ub.cora.data.converter.DataToJsonConverterFactory;
import se.uu.ub.cora.json.builder.JsonBuilderFactory;

public class DataToJsonConverterFactoryTest {
	private DataToJsonConverterFactory factoryNoUrl;
	private DataToJsonConverterFactory factoryWithUrl;
	private JsonBuilderFactorySpy factory;
	private CoraDataGroup coraDataGroup;
	private CoraDataAtomic dataAtomic;
	private CoraDataAttribute dataAttribute;
	private CoraDataRecordLink dataRecordLink;
	private CoraDataResourceLink dataResourceLink;

	@BeforeMethod
	public void beforeMethod() {
		createConvertibles();
		factory = new JsonBuilderFactorySpy();
		factoryNoUrl = DataToJsonConverterFactoryImp.withoutActionLinksUsingBuilderFactory(factory);
		String url = "some/url";
		factoryWithUrl = DataToJsonConverterFactoryImp
				.withActionLinksUsingBuilderFactoryAndUrl(factory, url);
	}

	private void createConvertibles() {
		coraDataGroup = CoraDataGroup.withNameInData("groupNameInData");
		dataAtomic = CoraDataAtomic.withNameInDataAndValue("atomicNameInData", "atomicValue");
		dataAttribute = CoraDataAttribute.withNameInDataAndValue("attributeNameInData",
				"attributeValue");
		dataRecordLink = CoraDataRecordLink.withNameInData("recordLinkNameInData");
		dataResourceLink = CoraDataResourceLink.withNameInData("recordLinkNameInData");
	}

	@Test
	public void testDataGroupNoUrl() {
		DataGroupToJsonConverter dataToJsonConverter = (DataGroupToJsonConverter) factoryNoUrl
				.factor(coraDataGroup);

		JsonBuilderFactory jsonBuilderFactory = dataToJsonConverter.jsonBuilderFactory;
		assertSame(jsonBuilderFactory, factory);

	}

	@Test
	public void testDataAtomicNoUrl() {
		DataAtomicToJsonConverter dataToJsonConverter = (DataAtomicToJsonConverter) factoryNoUrl
				.factor(dataAtomic);

		JsonBuilderFactory jsonBuilderFactory = dataToJsonConverter.factory;
		assertSame(jsonBuilderFactory, factory);
	}

	@Test
	public void testDataAttributeNoUrl() {
		DataAttributeToJsonConverter dataToJsonConverter = (DataAttributeToJsonConverter) factoryNoUrl
				.factor(dataAttribute);

		JsonBuilderFactory jsonBuilderFactory = dataToJsonConverter.factory;
		assertSame(jsonBuilderFactory, factory);
	}

	@Test
	public void testRecordLinkNoUrl() throws Exception {
		DataGroupToJsonConverter dataToJsonConverter = (DataGroupToJsonConverter) factoryNoUrl
				.factor(dataRecordLink);

		JsonBuilderFactory jsonBuilderFactory = dataToJsonConverter.jsonBuilderFactory;
		assertSame(jsonBuilderFactory, factory);
		assertFalse(dataToJsonConverter instanceof DataRecordLinkToJsonConverter);
	}

	@Test
	public void testDataResourceLinkNoUrl() throws Exception {
		DataGroupToJsonConverter dataToJsonConverter = (DataGroupToJsonConverter) factoryNoUrl
				.factor(dataResourceLink);

		JsonBuilderFactory jsonBuilderFactory = dataToJsonConverter.jsonBuilderFactory;
		assertSame(jsonBuilderFactory, factory);
		assertFalse(dataToJsonConverter instanceof DataResourceLinkToJsonConverter);
	}

	// TODO: Implement Converter for DataList add test
	// TODO: Implement Converter for DataRecord add test

	@Test
	public void testDataGroupWithUrl() {
		DataGroupToJsonConverter dataToJsonConverter = (DataGroupToJsonConverter) factoryWithUrl
				.factor(coraDataGroup);

		JsonBuilderFactory jsonBuilderFactory = dataToJsonConverter.jsonBuilderFactory;
		assertSame(jsonBuilderFactory, factory);
	}

	@Test
	public void testDataAtomicWithUrl() {
		DataAtomicToJsonConverter dataToJsonConverter = (DataAtomicToJsonConverter) factoryWithUrl
				.factor(dataAtomic);

		JsonBuilderFactory jsonBuilderFactory = dataToJsonConverter.factory;
		assertSame(jsonBuilderFactory, factory);
	}

	@Test
	public void testDataAttributeWithUrl() {
		DataAttributeToJsonConverter converter = (DataAttributeToJsonConverter) factoryWithUrl
				.factor(dataAttribute);

		JsonBuilderFactory jsonBuilderFactory = converter.factory;
		assertSame(jsonBuilderFactory, factory);
	}

	@Test
	public void testRecordLinkWithUrl() throws Exception {
		DataRecordLinkToJsonConverter converter = (DataRecordLinkToJsonConverter) factoryWithUrl
				.factor(dataRecordLink);

		JsonBuilderFactory jsonBuilderFactory = converter.jsonBuilderFactory;
		assertSame(jsonBuilderFactory, factory);
	}

	@Test
	public void testDataResourceLinkWithUrl() throws Exception {
		DataResourceLinkToJsonConverter dataToJsonConverter = (DataResourceLinkToJsonConverter) factoryWithUrl
				.factor(dataResourceLink);

		JsonBuilderFactory jsonBuilderFactory = dataToJsonConverter.jsonBuilderFactory;
		assertSame(jsonBuilderFactory, factory);
	}

	// @Test
	// public void testDataRecordLinkWithUrl() throws Exception {
	//
	// DataRecordLinkToJsonConverter dataToJsonConverter = (DataRecordLinkToJsonConverter)
	// dataToJsonConverterFactoryWithUrl
	// .factor(dataRecordLink);
	//
	// JsonBuilderFactory jsonBuilderFactory = dataToJsonConverter.jsonBuilderFactory;
	// assertSame(jsonBuilderFactory, factory);
	// }

	// @Test
	// public void testJsonCreatorFactoryDataResourceLink() throws Exception {
	//
	// Convertible convertible = CoraDataRecordLink.withNameInData("recordLinkNameInData");
	//
	// DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory.factor(convertible);
	//
	// // assertEquals(dataToJsonConverter.getClass(), "");
	// assertTrue(dataToJsonConverter instanceof DataResourceLinkToJsonConverter);
	// }
}
