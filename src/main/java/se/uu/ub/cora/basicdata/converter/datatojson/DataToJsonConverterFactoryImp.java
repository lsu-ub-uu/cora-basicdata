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

import se.uu.ub.cora.data.Convertible;
import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataAttribute;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataRecordLink;
import se.uu.ub.cora.data.DataResourceLink;
import se.uu.ub.cora.data.converter.DataToJsonConverter;
import se.uu.ub.cora.data.converter.DataToJsonConverterFactory;
import se.uu.ub.cora.json.builder.JsonBuilderFactory;

public class DataToJsonConverterFactoryImp implements DataToJsonConverterFactory {
	JsonBuilderFactory factory;
	private String url;

	/**
	 * usingUrlInActionLinks will factor {@link DataToJsonConverter}s that generate actionLinks for
	 * linked data when applicable
	 * 
	 * @param factory
	 *            A {@link JsonBuilderFactory} to pass on to factored converters
	 * @param baseUrl
	 *            A String with the base URL for the system, to pass on to factored converters
	 * @return A DataToJsonConverterFactoryImp that generates actionLinks for linked data
	 */
	public static DataToJsonConverterFactoryImp withActionLinksUsingBuilderFactoryAndUrl(
			JsonBuilderFactory factory, String baseUrl) {
		return new DataToJsonConverterFactoryImp(factory, baseUrl);
	}

	private DataToJsonConverterFactoryImp(JsonBuilderFactory factory, String url) {
		this.factory = factory;
		this.url = url;
	}

	/**
	 * withoutActionLinksUsingBuilderFactory will factor {@link DataToJsonConverter}s that does not
	 * generates actionLinks for linked data
	 * 
	 * @param factory
	 *            A {@link JsonBuilderFactory} to pass on to factored converters
	 * 
	 * @return A DataToJsonConverterFactoryImp that does not generates actionLinks for linked data
	 */
	public static DataToJsonConverterFactoryImp withoutActionLinksUsingBuilderFactory(
			JsonBuilderFactory factory) {
		return new DataToJsonConverterFactoryImp(factory);
	}

	private DataToJsonConverterFactoryImp(JsonBuilderFactory factory) {
		this.factory = factory;
	}

	@Override
	public DataToJsonConverter factor(Convertible convertible) {
		if (urlExists()) {
			if (convertible instanceof DataRecordLink) {
				return DataRecordLinkToJsonConverter
						.usingJsonBuilderFactoryAndDataRecordLinkAndBaseUrl(factory,
								(DataRecordLink) convertible, null);
			}
			if (convertible instanceof DataResourceLink) {
				return DataResourceLinkToJsonConverter
						.usingJsonBuilderFactoryAndDataResourceLinkAndRecordUrl(factory,
								(DataResourceLink) convertible, null);
			}
		}
		if (convertible instanceof DataGroup) {
			return DataGroupToJsonConverter.usingConverterFactoryAndBuilderFactoryAndDataGroup(this,
					factory, (DataGroup) convertible);
		}
		if (convertible instanceof DataAtomic) {
			return DataAtomicToJsonConverter.usingJsonBuilderFactoryAndDataAtomic(factory,
					(DataAtomic) convertible);
		}
		return DataAttributeToJsonConverter.usingJsonBuilderFactoryAndDataAttribute(factory,
				(DataAttribute) convertible);
	}

	private boolean urlExists() {
		return url != null;
	}
}
