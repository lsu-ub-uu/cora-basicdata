/*
 * Copyright 2021 Uppsala University Library
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

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import se.uu.ub.cora.data.Action;
import se.uu.ub.cora.data.DataAttribute;
import se.uu.ub.cora.data.DataResourceLink;
import se.uu.ub.cora.data.spies.DataAttributeSpy;
import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;
import se.uu.ub.cora.testutils.mrv.MethodReturnValues;

public class DataResourceLinkSpy implements DataResourceLink {

	public MethodCallRecorder MCR = new MethodCallRecorder();
	public MethodReturnValues MRV = new MethodReturnValues();

	public DataResourceLinkSpy() {
		MCR.useMRV(MRV);
		MRV.setDefaultReturnValuesSupplier("getNameInData", () -> "fakeDataResourceNameInData");
		MRV.setDefaultReturnValuesSupplier("hasReadAction", () -> false);
		MRV.setDefaultReturnValuesSupplier("getMimeType", () -> "someMimeType");
		MRV.setDefaultReturnValuesSupplier("hasRepeatId", () -> false);
		MRV.setDefaultReturnValuesSupplier("getRepeatId", String::new);
		MRV.setDefaultReturnValuesSupplier("hasAttributes", () -> false);
		MRV.setDefaultReturnValuesSupplier("getAttribute", DataAttributeSpy::new);
		MRV.setDefaultReturnValuesSupplier("getAttributeValue", () -> Optional.empty());
		MRV.setDefaultReturnValuesSupplier("getAttributes", () -> Collections.emptySet());
	}

	@Override
	public void addAction(Action action) {
		MCR.addCall("action", action);
	}

	@Override
	public String getNameInData() {
		return (String) MCR.addCallAndReturnFromMRV();
	}

	@Override
	public boolean hasReadAction() {
		return (boolean) MCR.addCallAndReturnFromMRV();
	}

	@Override
	public String getMimeType() {
		return (String) MCR.addCallAndReturnFromMRV();
	}

	@Override
	public void setMimeType(String mimeType) {
		MCR.addCall("mimeType", mimeType);
	}

	@Override
	public boolean hasRepeatId() {
		return (boolean) MCR.addCallAndReturnFromMRV();
	}

	@Override
	public void setRepeatId(String repeatId) {
		MCR.addCall("repeatId", repeatId);
	}

	@Override
	public String getRepeatId() {
		return (String) MCR.addCallAndReturnFromMRV();
	}

	@Override
	public void addAttributeByIdWithValue(String nameInData, String value) {
		MCR.addCall("nameInData", nameInData, "value", value);
	}

	@Override
	public boolean hasAttributes() {
		return (boolean) MCR.addCallAndReturnFromMRV();
	}

	@Override
	public DataAttribute getAttribute(String nameInData) {
		return (DataAttribute) MCR.addCallAndReturnFromMRV();
	}

	@Override
	public Collection<DataAttribute> getAttributes() {
		return (Collection<DataAttribute>) MCR.addCallAndReturnFromMRV();
	}

	@Override
	public Optional<String> getAttributeValue(String nameInData) {
		return (Optional<String>) MCR.addCallAndReturnFromMRV();
	}

}
