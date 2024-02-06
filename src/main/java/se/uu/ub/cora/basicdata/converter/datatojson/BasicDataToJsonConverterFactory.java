/*
 * Copyright 2015, 2019, 2021, 2023 Uppsala University Library
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

import java.util.Optional;

import se.uu.ub.cora.data.Convertible;
import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataAttribute;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataList;
import se.uu.ub.cora.data.DataRecord;
import se.uu.ub.cora.data.DataRecordLink;
import se.uu.ub.cora.data.DataResourceLink;
import se.uu.ub.cora.data.converter.DataToJsonConverter;
import se.uu.ub.cora.data.converter.DataToJsonConverterFactory;
import se.uu.ub.cora.data.converter.ExternalUrls;
import se.uu.ub.cora.json.builder.JsonBuilderFactory;

public class BasicDataToJsonConverterFactory implements DataToJsonConverterFactory {
	JsonBuilderFactory builderFactory;
	private Optional<ExternalUrls> externalUrls;
	private Optional<String> recordUrl;

	/**
	 * withoutActionLinksUsingBuilderFactory will factor {@link DataToJsonConverter}s that does not
	 * generates actionLinks for linked data
	 * 
	 * @param factory
	 *            A {@link JsonBuilderFactory} to pass on to factored converters
	 * 
	 * @return A DataToJsonConverterFactoryImp that does not generates actionLinks for linked data
	 */
	public static BasicDataToJsonConverterFactory usingBuilderFactory(JsonBuilderFactory factory) {
		return new BasicDataToJsonConverterFactory(factory);
	}

	BasicDataToJsonConverterFactory(JsonBuilderFactory factory) {
		this.builderFactory = factory;
		externalUrls = Optional.empty();
		recordUrl = Optional.empty();
	}

	@Override
	public DataToJsonConverter factorUsingConvertible(Convertible convertible) {
		if (isDataList(convertible)) {
			return DataListToJsonConverter.usingJsonFactoryForDataList(this, builderFactory,
					(DataList) convertible);
		}

		if (isDataRecord(convertible)) {
			RecordActionsToJsonConverter actionsConverter;
			if (externalUrls.isPresent()) {
				actionsConverter = RecordActionsToJsonConverterImp
						.usingConverterFactoryAndBuilderFactoryAndBaseUrl(this, builderFactory,
								externalUrls.get().getBaseUrl());

			} else {
				actionsConverter = RecordActionsToJsonConverterImp
						.usingConverterFactoryAndBuilderFactoryAndBaseUrl(this, builderFactory,
								null);
			}

			return DataRecordToJsonConverter
					.usingConverterFactoryAndActionsConverterAndBuilderFactoryAndDataRecordAndBaseUrlAndIiifUrl(
							(DataRecord) convertible, this, actionsConverter, builderFactory,
							externalUrls);
		}

		if (isDataRecordLinkAndHasBaseUrl(convertible)) {
			return DataRecordLinkToJsonConverter
					.usingConverterFactoryAndJsonBuilderFactoryAndDataRecordLinkAndBaseUrl(this,
							builderFactory, (DataRecordLink) convertible,
							externalUrls.get().getBaseUrl());
		}

		if (isDataResourceLinkAndHasRecordUrl(convertible)) {

			return DataResourceLinkToJsonConverter
					.usingConverterFactoryJsonBuilderFactoryAndDataResourceLinkAndRecordUrl(this,
							builderFactory, (DataResourceLink) convertible, recordUrl);
		}

		if (isDataResourceLinkAndHasNoRecordUrl(convertible)) {

			return DataResourceLinkToJsonConverter
					.usingConverterFactoryJsonBuilderFactoryAndDataResourceLinkAndRecordUrl(this,
							builderFactory, (DataResourceLink) convertible, recordUrl);
		}

		if (isDataGroup(convertible)) {
			return DataGroupToJsonConverter.usingConverterFactoryAndBuilderFactoryAndDataGroup(this,
					builderFactory, (DataGroup) convertible);
		}
		if (isDataAtomic(convertible)) {
			return DataAtomicToJsonConverter.usingJsonBuilderFactoryAndDataAtomic(builderFactory,
					(DataAtomic) convertible);
		}
		return DataAttributeToJsonConverter.usingJsonBuilderFactoryAndDataAttribute(builderFactory,
				(DataAttribute) convertible);
	}

	private boolean isDataList(Convertible convertible) {
		return convertible instanceof DataList;
	}

	private boolean isDataRecord(Convertible convertible) {
		return convertible instanceof DataRecord;
	}

	private boolean isDataAtomic(Convertible convertible) {
		return convertible instanceof DataAtomic;
	}

	private boolean isDataGroup(Convertible convertible) {
		return convertible instanceof DataGroup;
	}

	private boolean isDataResourceLinkAndHasRecordUrl(Convertible convertible) {
		return (convertible instanceof DataResourceLink) && (recordUrl.isPresent());
	}

	private boolean isDataResourceLinkAndHasNoRecordUrl(Convertible convertible) {
		return (convertible instanceof DataResourceLink) && (recordUrl.isEmpty());
	}

	private boolean isDataRecordLinkAndHasBaseUrl(Convertible convertible) {
		return externalUrls.isPresent() && externalUrls.get().hasBaseUrl()
				&& isRecordLink(convertible);
	}

	private boolean isRecordLink(Convertible convertible) {
		return convertible instanceof DataRecordLink;
	}

	@Override
	public DataToJsonConverter factorUsingConvertibleAndExternalUrls(Convertible convertible,
			ExternalUrls externalUrls) {
		this.externalUrls = Optional.of(externalUrls);
		return factorUsingConvertible(convertible);
	}

	@Override
	public DataToJsonConverter factorUsingBaseUrlAndRecordUrlAndConvertible(String baseUrl,
			String recordUrl, Convertible convertible) {
		ExternalUrls tmpExternal = new ExternalUrls();
		tmpExternal.setBaseUrl(baseUrl);
		externalUrls = Optional.of(tmpExternal);
		this.recordUrl = Optional.of(recordUrl);

		return factorUsingConvertible(convertible);
	}

	public Optional<ExternalUrls> onlyForTestGetExternalUrls() {
		return externalUrls;
	}

	public Optional<String> onlyForTestGetRecordUrl() {
		return recordUrl;
	}
}
