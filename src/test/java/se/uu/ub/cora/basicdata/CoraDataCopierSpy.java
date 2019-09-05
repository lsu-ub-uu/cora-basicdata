package se.uu.ub.cora.basicdata;

import se.uu.ub.cora.data.DataCopier;
import se.uu.ub.cora.data.DataElement;

public class CoraDataCopierSpy implements DataCopier {

	public boolean copyWasCalled = false;
	public DataElement returnedElement;

	@Override
	public DataElement copy() {
		copyWasCalled = true;
		returnedElement = new CoraDataElementSpy();
		return returnedElement;
	}

}
