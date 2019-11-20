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

import se.uu.ub.cora.data.DataAttribute;
import se.uu.ub.cora.data.DataPart;

public final class CoraDataAttribute implements DataAttribute, DataPart {

	private String nameInData;
	private String value;

	private CoraDataAttribute(String nameInData, String value) {
		this.nameInData = nameInData;
		this.value = value;
	}

	public static CoraDataAttribute withNameInDataAndValue(String nameInData, String value) {
		return new CoraDataAttribute(nameInData, value);
	}

	@Override
	public String getNameInData() {
		return nameInData;
	}

	public String getValue() {
		return value;
	}

}
