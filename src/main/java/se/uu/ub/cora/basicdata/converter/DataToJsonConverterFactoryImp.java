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

package se.uu.ub.cora.basicdata.converter;

import se.uu.ub.cora.basicdata.CoraDataAtomic;
import se.uu.ub.cora.basicdata.CoraDataAttribute;
import se.uu.ub.cora.basicdata.CoraDataGroup;
import se.uu.ub.cora.data.DataPart;
import se.uu.ub.cora.json.builder.JsonBuilderFactory;

public class DataToJsonConverterFactoryImp implements DataToJsonConverterFactory {

	@Override
	public DataToJsonConverter createForDataElement(JsonBuilderFactory factory, DataPart dataPart) {

		if (dataPart instanceof CoraDataGroup) {
			return DataGroupToJsonConverter.usingJsonFactoryForDataGroup(factory,
					(CoraDataGroup) dataPart);
		}
		if (dataPart instanceof CoraDataAtomic) {
			return DataAtomicToJsonConverter.usingJsonFactoryForDataAtomic(factory,
					(CoraDataAtomic) dataPart);
		}
		return DataAttributeToJsonConverter.usingJsonFactoryForDataAttribute(factory,
				(CoraDataAttribute) dataPart);
	}
}
