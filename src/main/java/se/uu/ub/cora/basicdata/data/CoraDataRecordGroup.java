/*
 * Copyright 2022 Olov McKie
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

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.data.DataRecordLink;

public class CoraDataRecordGroup extends CoraDataGroup implements DataRecordGroup {

	private static final String ID = "id";
	private static final String DATA_DIVIDER = "dataDivider";
	private static final String TYPE = "type";
	private static final String RECORD_INFO = "recordInfo";

	public static CoraDataRecordGroup withNameInData(String nameInData) {
		return new CoraDataRecordGroup(nameInData);
	}

	protected CoraDataRecordGroup(String nameInData) {
		super(nameInData);
	}

	@Override
	public String getType() {
		DataGroup recordInfo = this.getFirstGroupWithNameInData(RECORD_INFO);
		DataRecordLink typeLink = (DataRecordLink) recordInfo.getFirstChildWithNameInData(TYPE);
		return typeLink.getLinkedRecordId();
	}

	@Override
	public void setType(String type) {
		ensureRecordInfoExists();
		DataGroup recordInfo = this.getFirstGroupWithNameInData(RECORD_INFO);
		recordInfo.removeAllChildrenMatchingFilter(CoraDataChildFilter.usingNameInData(TYPE));
		recordInfo
				.addChild(CoraDataRecordLink.usingNameInDataAndTypeAndId(TYPE, "recordType", type));
	}

	private void ensureRecordInfoExists() {
		if (!this.containsChildWithNameInData(RECORD_INFO)) {
			this.addChild(CoraDataGroup.withNameInData(RECORD_INFO));
		}
	}

	@Override
	public String getId() {
		DataGroup recordInfo = this.getFirstGroupWithNameInData(RECORD_INFO);
		return recordInfo.getFirstAtomicValueWithNameInData(ID);
	}

	@Override
	public void setId(String id) {
		ensureRecordInfoExists();
		DataGroup recordInfo = this.getFirstGroupWithNameInData(RECORD_INFO);
		recordInfo.removeAllChildrenMatchingFilter(CoraDataChildFilter.usingNameInData(ID));
		recordInfo.addChild(CoraDataAtomic.withNameInDataAndValue(ID, id));
	}

	@Override
	public String getDataDivider() {
		DataGroup recordInfo = this.getFirstGroupWithNameInData(RECORD_INFO);
		DataRecordLink typeLink = (DataRecordLink) recordInfo
				.getFirstChildWithNameInData(DATA_DIVIDER);
		return typeLink.getLinkedRecordId();
	}

	@Override
	public void setDataDivider(String dataDivider) {
		ensureRecordInfoExists();
		DataGroup recordInfo = this.getFirstGroupWithNameInData(RECORD_INFO);
		recordInfo
				.removeAllChildrenMatchingFilter(CoraDataChildFilter.usingNameInData(DATA_DIVIDER));
		recordInfo.addChild(CoraDataRecordLink.usingNameInDataAndTypeAndId(DATA_DIVIDER, "system",
				dataDivider));
	}

	@Override
	public String getValidationType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setValidationType(String validationType) {
		// TODO Auto-generated method stub

	}

}
