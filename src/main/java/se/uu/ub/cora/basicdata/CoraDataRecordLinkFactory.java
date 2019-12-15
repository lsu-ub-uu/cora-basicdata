/*
 * Copyright 2019 Uppsala University Library
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
package se.uu.ub.cora.basicdata;

import se.uu.ub.cora.basicdata.data.CoraDataRecordLink;
import se.uu.ub.cora.data.DataGroupFactory;
import se.uu.ub.cora.data.DataRecordLink;

public class CoraDataRecordLinkFactory implements DataGroupFactory {

	@Override
	public DataRecordLink factorUsingNameInData(String nameInData) {
		return CoraDataRecordLink.withNameInData(nameInData);
	}

	@Override
	public DataRecordLink factorAsLinkWithNameInDataTypeAndId(String nameInData, String recordType,
			String recordId) {
		return CoraDataRecordLink.asLinkWithNameInDataAndTypeAndId(nameInData, recordType,
				recordId);
	}

}
