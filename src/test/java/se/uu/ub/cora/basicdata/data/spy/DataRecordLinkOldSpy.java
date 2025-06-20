/*
 * Copyright 2021, 2025 Uppsala University Library
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
package se.uu.ub.cora.basicdata.data.spy;

import java.util.Optional;

import se.uu.ub.cora.data.Action;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataRecordLink;
import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;
import se.uu.ub.cora.testutils.mrv.MethodReturnValues;

public class DataRecordLinkOldSpy extends DataGroupOldSpy implements DataRecordLink {
	public MethodCallRecorder MCR = new MethodCallRecorder();
	public MethodReturnValues MRV = new MethodReturnValues();

	public DataRecordLinkOldSpy(String nameInData) {
		super(nameInData);
		MCR.useMRV(MRV);
		MRV.setDefaultReturnValuesSupplier("hasReadAction", () -> false);
		MRV.setDefaultReturnValuesSupplier("getLinkedRecordType", () -> "someRecordType");
		MRV.setDefaultReturnValuesSupplier("getLinkedRecordId", () -> "someRecordId");
		MRV.setDefaultReturnValuesSupplier("getLinkedRecord", Optional::empty);
	}

	@Override
	public void addAction(Action action) {
		MCR.addCall("action", action);
	}

	@Override
	public boolean hasReadAction() {
		return (boolean) MCR.addCallAndReturnFromMRV();
	}

	@Override
	public String getLinkedRecordType() {
		return (String) MCR.addCallAndReturnFromMRV();
	}

	@Override
	public String getLinkedRecordId() {
		return (String) MCR.addCallAndReturnFromMRV();
	}

	@Override
	public void setLinkedRecord(DataGroup linkedGroup) {
		MCR.addCall("linkedGroup", linkedGroup);

	}

	@Override
	public Optional<DataGroup> getLinkedRecord() {
		return (Optional) MCR.addCallAndReturnFromMRV();
	}

}
