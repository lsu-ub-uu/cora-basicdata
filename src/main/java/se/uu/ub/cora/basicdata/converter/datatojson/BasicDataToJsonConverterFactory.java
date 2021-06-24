/*
 * Copyright 2015, 2019, 2021 Uppsala University Library
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
import se.uu.ub.cora.json.builder.JsonBuilderFactory;

public class BasicDataToJsonConverterFactory implements DataToJsonConverterFactory {
	JsonBuilderFactory builderFactory;
	String baseUrl;
	String recordUrl;

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
	}

	@Override
	public DataToJsonConverter factorUsingConvertible(Convertible convertible) {
		if (convertible instanceof DataList) {
			return DataListToJsonConverter.usingJsonFactoryForDataList(this, builderFactory,
					(DataList) convertible);
		}
		if (convertible instanceof DataRecord) {
			RecordActionsToJsonConverter actionsConverter = RecordActionsToJsonConverterImp
					.usingConverterFactoryAndBuilderFactoryAndBaseUrl(this, builderFactory,
							baseUrl);
			return DataRecordToJsonConverter
					.usingConverterFactoryAndActionsConverterAndBuilderFactoryAndBaseUrlAndDataRecord(
							this, actionsConverter, builderFactory, baseUrl,
							(DataRecord) convertible);
		}

		if (baseUrlIsKnownGenerateRecordLinks()) {
			if (convertible instanceof DataRecordLink) {
				return DataRecordLinkToJsonConverter
						.usingConverterFactoryAndJsonBuilderFactoryAndDataRecordLinkAndBaseUrl(this,
								builderFactory, (DataRecordLink) convertible, baseUrl);
			}
		}
		if (convertible instanceof DataResourceLink) {
			if (recordUrl != null) {
				return DataResourceLinkToJsonConverter
						.usingConverterFactoryJsonBuilderFactoryAndDataResourceLinkAndRecordUrl(
								this, builderFactory, (DataResourceLink) convertible, recordUrl);
			}
		}
		if (convertible instanceof DataGroup) {
			return DataGroupToJsonConverter.usingConverterFactoryAndBuilderFactoryAndDataGroup(this,
					builderFactory, (DataGroup) convertible);
		}
		if (convertible instanceof DataAtomic) {
			return DataAtomicToJsonConverter.usingJsonBuilderFactoryAndDataAtomic(builderFactory,
					(DataAtomic) convertible);
		}
		return DataAttributeToJsonConverter.usingJsonBuilderFactoryAndDataAttribute(builderFactory,
				(DataAttribute) convertible);
	}

	private boolean baseUrlIsKnownGenerateRecordLinks() {
		return baseUrl != null;
	}

	@Override
	public DataToJsonConverter factorUsingBaseUrlAndConvertible(String baseUrl,
			Convertible convertible) {
		this.baseUrl = baseUrl;

		return factorUsingConvertible(convertible);
	}

	@Override
	public DataToJsonConverter factorUsingBaseUrlAndRecordUrlAndConvertible(String baseUrl,
			String recordUrl, Convertible convertible) {
		this.baseUrl = baseUrl;
		this.recordUrl = recordUrl;

		return factorUsingConvertible(convertible);
	}
}
