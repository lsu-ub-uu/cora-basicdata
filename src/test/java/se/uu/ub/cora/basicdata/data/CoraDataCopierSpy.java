package se.uu.ub.cora.basicdata.data;

import se.uu.ub.cora.basicdata.data.spy.CoraDataElementSpy;
import se.uu.ub.cora.data.DataChild;
import se.uu.ub.cora.data.copier.DataCopier;

public class CoraDataCopierSpy implements DataCopier {

	public boolean copyWasCalled = false;
	public DataChild returnedElement;

	@Override
	public DataChild copy() {
		copyWasCalled = true;
		returnedElement = new CoraDataElementSpy();
		return returnedElement;
	}

}
