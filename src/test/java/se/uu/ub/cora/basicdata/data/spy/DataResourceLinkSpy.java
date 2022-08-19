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

import java.util.ArrayList;
import java.util.List;

import se.uu.ub.cora.data.Action;
import se.uu.ub.cora.data.DataResourceLink;
import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;

public class DataResourceLinkSpy extends DataGroupOldSpy implements DataResourceLink {

	public MethodCallRecorder MCR = new MethodCallRecorder();

	public String nameInData;
	public List<Action> actions = new ArrayList<>();
	public boolean hasReadAction = false;

	public DataResourceLinkSpy(String nameInData) {
		super(nameInData);
	}

	@Override
	public void addAction(Action action) {
		MCR.addCall("action", action);
		actions.add(action);

	}

	@Override
	public String getNameInData() {
		MCR.addCall();
		return "fakeDataResourceNameInData";
	}

	@Override
	public boolean hasReadAction() {
		MCR.addCall();
		MCR.addReturned(hasReadAction);
		return hasReadAction;
	}

	@Override
	public String getMimeType() {
		MCR.addCall();
		String mimeType = "somMimeType";
		MCR.addReturned(mimeType);
		return mimeType;
	}

	@Override
	public void setStreamId(String streamId) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getStreamId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setFileName(String fileName) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getFileName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setFileSize(String fileSize) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getFileSize() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setMimeType(String mimeType) {
		// TODO Auto-generated method stub

	}

}
