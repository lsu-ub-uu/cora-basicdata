/*
 * Copyright 2015, 2019, 2021, 2024, 2025 Uppsala University Library
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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.basicdata.data.CoraDataAtomic;
import se.uu.ub.cora.basicdata.data.CoraDataAttribute;
import se.uu.ub.cora.basicdata.data.CoraDataGroup;
import se.uu.ub.cora.basicdata.data.CoraDataList;
import se.uu.ub.cora.basicdata.data.CoraDataRecord;
import se.uu.ub.cora.basicdata.data.CoraDataRecordLink;
import se.uu.ub.cora.basicdata.data.CoraDataResourceLink;
import se.uu.ub.cora.data.Convertible;
import se.uu.ub.cora.data.converter.DataToJsonConverter;
import se.uu.ub.cora.data.converter.DataToJsonConverterFactory;
import se.uu.ub.cora.data.converter.ExternalUrls;
import se.uu.ub.cora.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;

public class BasicDataToJsonConverterFactoryTest {
	private DataToJsonConverterFactory converterFactory;
	private JsonBuilderFactorySpy builderFactory;
	private CoraDataGroup coraDataGroup;
	private CoraDataAtomic dataAtomic;
	private CoraDataAttribute dataAttribute;
	private CoraDataRecordLink dataRecordLink;
	private CoraDataResourceLink dataResourceLink;
	private static final String BASE_URL = "some/url/";
	private static final String IIIF_URL = "someIiifUrl";
	private ExternalUrls externalUrls;

	@BeforeMethod
	public void beforeMethod() {
		createConvertibles();
		builderFactory = new JsonBuilderFactorySpy();
		converterFactory = BasicDataToJsonConverterFactory.usingBuilderFactory(builderFactory);

		setExternalUrls();
	}

	private void setExternalUrls() {
		externalUrls = new ExternalUrls();
		externalUrls.setBaseUrl(BASE_URL);
		externalUrls.setIfffUrl(IIIF_URL);
	}

	private void createConvertibles() {
		coraDataGroup = CoraDataGroup.withNameInData("groupNameInData");
		dataAtomic = CoraDataAtomic.withNameInDataAndValue("atomicNameInData", "atomicValue");
		dataAttribute = CoraDataAttribute.withNameInDataAndValue("attributeNameInData",
				"attributeValue");
		dataRecordLink = CoraDataRecordLink.withNameInData("recordLinkNameInData");
		dataResourceLink = CoraDataResourceLink.withNameInDataAndTypeAndIdAndMimeType(
				"recordLinkNameInData", null, null, "someMimeType");
	}

	@Test
	public void testDataList() {
		CoraDataList coraDataList = CoraDataList.withContainDataOfType("someType");
		DataListToJsonConverter dataToJsonConverter = (DataListToJsonConverter) converterFactory
				.factorUsingConvertible(coraDataList);
		assertSame(dataToJsonConverter.converterFactory, converterFactory);
		assertSame(dataToJsonConverter.builderFactory, builderFactory);
		assertSame(dataToJsonConverter.dataList, coraDataList);

	}

	@Test
	public void testRecordNoUrl() {
		CoraDataRecord coraDataRecord = CoraDataRecord.withDataRecordGroup(null);

		DataRecordToJsonConverter dataToJsonConverter = (DataRecordToJsonConverter) converterFactory
				.factorUsingConvertible(coraDataRecord);

		assertSame(dataToJsonConverter.converterFactory, converterFactory);
		assertTrue(dataToJsonConverter.actionsConverter instanceof RecordActionsToJsonConverterImp);
		assertSame(dataToJsonConverter.builderFactory, builderFactory);
		assertTrue(dataToJsonConverter.onlyForTestGetOptionalExternalUrls().isEmpty());
		assertSame(dataToJsonConverter.dataRecord, coraDataRecord);
	}

	@Test
	public void testDependenciesOfActionConverterNoUrl() {
		CoraDataRecord coraDataRecord = CoraDataRecord.withDataRecordGroup(null);

		DataRecordToJsonConverter dataToJsonConverter = (DataRecordToJsonConverter) converterFactory
				.factorUsingConvertible(coraDataRecord);

		RecordActionsToJsonConverterImp actionsConverter = (RecordActionsToJsonConverterImp) dataToJsonConverter.actionsConverter;
		assertSame(actionsConverter.converterFactory, converterFactory);
		assertSame(actionsConverter.builderFactory, builderFactory);
		assertTrue(dataToJsonConverter.onlyForTestGetOptionalExternalUrls().isEmpty());
	}

	@Test
	public void testRecordWithUrl() {
		CoraDataRecord coraDataRecord = CoraDataRecord.withDataRecordGroup(null);
		DataRecordToJsonConverter dataToJsonConverter = (DataRecordToJsonConverter) converterFactory
				.factorUsingConvertibleAndExternalUrls(coraDataRecord, externalUrls);
		assertSame(dataToJsonConverter.converterFactory, converterFactory);
		assertTrue(dataToJsonConverter.actionsConverter instanceof RecordActionsToJsonConverterImp);
		assertSame(dataToJsonConverter.builderFactory, builderFactory);
		assertSame(dataToJsonConverter.onlyForTestGetOptionalExternalUrls().get().getBaseUrl(),
				BASE_URL);
		assertSame(dataToJsonConverter.onlyForTestGetOptionalExternalUrls().get().getIfffUrl(),
				IIIF_URL);
		assertSame(dataToJsonConverter.dataRecord, coraDataRecord);
	}

	@Test
	public void testDependenciesOfActionConverterWithUrl() {
		CoraDataRecord coraDataRecord = CoraDataRecord.withDataRecordGroup(null);
		DataRecordToJsonConverter dataToJsonConverter = (DataRecordToJsonConverter) converterFactory
				.factorUsingConvertibleAndExternalUrls(coraDataRecord, externalUrls);
		RecordActionsToJsonConverterImp actionsConverter = (RecordActionsToJsonConverterImp) dataToJsonConverter.actionsConverter;
		assertSame(actionsConverter.converterFactory, converterFactory);
		assertSame(actionsConverter.builderFactory, builderFactory);
		assertSame(dataToJsonConverter.onlyForTestGetOptionalExternalUrls().get().getBaseUrl(),
				BASE_URL);
	}

	@Test
	public void testDataGroupNoUrl() {
		DataGroupToJsonConverter dataToJsonConverter = (DataGroupToJsonConverter) converterFactory
				.factorUsingConvertible(coraDataGroup);

		JsonBuilderFactory jsonBuilderFactory = dataToJsonConverter.jsonBuilderFactory;
		assertSame(jsonBuilderFactory, builderFactory);

	}

	@Test
	public void testDataAtomicNoUrl() {
		DataAtomicToJsonConverter dataToJsonConverter = (DataAtomicToJsonConverter) converterFactory
				.factorUsingConvertible(dataAtomic);

		JsonBuilderFactory jsonBuilderFactory = dataToJsonConverter.factory;
		assertSame(jsonBuilderFactory, builderFactory);
	}

	@Test
	public void testDataAttributeNoUrl() {
		DataAttributeToJsonConverter dataToJsonConverter = (DataAttributeToJsonConverter) converterFactory
				.factorUsingConvertible(dataAttribute);

		JsonBuilderFactory jsonBuilderFactory = dataToJsonConverter.factory;
		assertSame(jsonBuilderFactory, builderFactory);
	}

	@Test
	public void testRecordLinkNoUrl() {
		DataGroupToJsonConverter dataToJsonConverter = (DataGroupToJsonConverter) converterFactory
				.factorUsingConvertible(dataRecordLink);

		JsonBuilderFactory jsonBuilderFactory = dataToJsonConverter.jsonBuilderFactory;
		assertSame(jsonBuilderFactory, builderFactory);
		assertFalse(dataToJsonConverter instanceof DataRecordLinkToJsonConverter);
	}

	@Test
	public void testDataResourceLinkNoUrl() {
		DataResourceLinkToJsonConverter dataToJsonConverter = (DataResourceLinkToJsonConverter) converterFactory
				.factorUsingConvertible(dataResourceLink);

		assertSame(dataToJsonConverter.onlyForTestGetJsonBuilderFactory(), builderFactory);
		assertFalse(dataToJsonConverter.onlyForTestGetBaseUrl().isPresent());
		assertTrue(dataToJsonConverter instanceof DataResourceLinkToJsonConverter);

	}

	@Test
	public void testDataGroupWithUrl() {
		DataGroupToJsonConverter dataToJsonConverter = (DataGroupToJsonConverter) converterFactory
				.factorUsingConvertible(coraDataGroup);

		JsonBuilderFactory jsonBuilderFactory = dataToJsonConverter.jsonBuilderFactory;
		assertSame(jsonBuilderFactory, builderFactory);
	}

	@Test
	public void testDataAtomicWithUrl() {
		DataAtomicToJsonConverter dataToJsonConverter = (DataAtomicToJsonConverter) converterFactory
				.factorUsingConvertible(dataAtomic);

		JsonBuilderFactory jsonBuilderFactory = dataToJsonConverter.factory;
		assertSame(jsonBuilderFactory, builderFactory);
	}

	@Test
	public void testDataAttributeWithUrl() {
		DataAttributeToJsonConverter converter = (DataAttributeToJsonConverter) converterFactory
				.factorUsingConvertible(dataAttribute);

		JsonBuilderFactory jsonBuilderFactory = converter.factory;
		assertSame(jsonBuilderFactory, builderFactory);
	}

	@Test
	public void testRecordLinkWithUrl() {
		DataRecordLinkToJsonConverter converter = (DataRecordLinkToJsonConverter) converterFactory
				.factorUsingBaseUrlAndConvertible(BASE_URL, dataRecordLink);

		JsonBuilderFactory jsonBuilderFactory = converter.jsonBuilderFactory;
		assertSame(jsonBuilderFactory, builderFactory);
		assertSame(converter.converterFactory, converterFactory);
		assertEquals(converter.baseURL, BASE_URL);
	}

	@Test
	public void testRectorDownToRecordLink() {
		converterFactory.factorUsingBaseUrlAndConvertible(BASE_URL, dataRecordLink);
		DataToJsonConverter converter = converterFactory.factorUsingConvertible(dataRecordLink);

		assertTrue(converter instanceof DataRecordLinkToJsonConverter);
	}

	@Test
	public void testDataResourceLinkWithBaseUrl() {
		DataResourceLinkToJsonConverter converter = (DataResourceLinkToJsonConverter) converterFactory
				.factorUsingBaseUrlAndConvertible(BASE_URL, dataResourceLink);

		JsonBuilderFactory jsonBuilderFactory = converter.jsonBuilderFactory;
		assertSame(jsonBuilderFactory, builderFactory);
		assertSame(converter.onlyForTestGetConverterFactory(), converterFactory);
		assertEquals(converter.onlyForTestGetBaseUrl().get(), BASE_URL);
	}

	@Test
	public void testDataResourceLinkWithExternalUrls_withoutBaseUrl() {
		ExternalUrls withouthBaseUrl = new ExternalUrls();
		DataResourceLinkToJsonConverter converter = (DataResourceLinkToJsonConverter) converterFactory
				.factorUsingConvertibleAndExternalUrls(dataResourceLink,
						withouthBaseUrl);

		JsonBuilderFactory jsonBuilderFactory = converter.jsonBuilderFactory;
		assertSame(jsonBuilderFactory, builderFactory);
		assertSame(converter.onlyForTestGetConverterFactory(), converterFactory);
		assertTrue(converter.onlyForTestGetBaseUrl().isEmpty());
	}

	@Test
	public void testDataResourceLinkWithOutBaseUrl() {
		DataResourceLinkToJsonConverter converter = (DataResourceLinkToJsonConverter) converterFactory
				.factorUsingConvertible(dataResourceLink);

		JsonBuilderFactory jsonBuilderFactory = converter.jsonBuilderFactory;
		assertSame(jsonBuilderFactory, builderFactory);
		assertSame(converter.onlyForTestGetConverterFactory(), converterFactory);
		assertTrue(converter.onlyForTestGetBaseUrl().isEmpty());
	}

	@Test
	public void testRectorDownToRecordLink2() {
		converterFactory.factorUsingConvertibleAndExternalUrls(dataRecordLink, externalUrls);
		DataToJsonConverter converter = converterFactory.factorUsingConvertible(dataRecordLink);

		assertTrue(converter instanceof DataGroupToJsonConverter);
	}

	@Test
	public void testGenerateLinksForResourceWithoutRecordUrlSetShouldReturnDataGroup() {

		converterFactory.factorUsingConvertibleAndExternalUrls(dataResourceLink, externalUrls);
		DataToJsonConverter converter = converterFactory.factorUsingConvertible(dataResourceLink);

		assertTrue(converter instanceof DataResourceLinkToJsonConverter);
	}

	@Test
	public void testFactorUsingBaseUrlAndConvertibleUsesFactorUsingConvertible() {
		BasicDataToJsonConverterFactoryForTest forTest = new BasicDataToJsonConverterFactoryForTest();

		DataToJsonConverter converter = forTest
				.factorUsingConvertibleAndExternalUrls(dataRecordLink, externalUrls);

		assertEquals(forTest.onlyForTestGetExternalUrls().get().getBaseUrl(), BASE_URL);
		forTest.MCR.assertParameters("factorUsingConvertible", 0, dataRecordLink);
		forTest.MCR.assertReturn("factorUsingConvertible", 0, converter);

	}

	@Test
	public void testFactorUsingRecordUrlAndConvertibleUsesFactorUsingConvertible() {
		BasicDataToJsonConverterFactoryForTest forTest = new BasicDataToJsonConverterFactoryForTest();
		DataToJsonConverter converter = forTest.factorUsingBaseUrlAndConvertible(BASE_URL,
				dataRecordLink);

		assertEquals(forTest.onlyForTestGetExternalUrls().get().getBaseUrl(), BASE_URL);
		forTest.MCR.assertParameters("factorUsingConvertible", 0, dataRecordLink);
		forTest.MCR.assertReturn("factorUsingConvertible", 0, converter);
	}

	class BasicDataToJsonConverterFactoryForTest extends BasicDataToJsonConverterFactory {
		BasicDataToJsonConverterFactoryForTest() {
			super(null);
		}

		MethodCallRecorder MCR = new MethodCallRecorder();

		@Override
		public DataToJsonConverter factorUsingConvertible(Convertible convertible) {
			MCR.addCall("convertible", convertible);
			DataToJsonConverter converter = new DataToJsonConverterSpy();
			MCR.addReturned(converter);
			return converter;
		}
	}
}
