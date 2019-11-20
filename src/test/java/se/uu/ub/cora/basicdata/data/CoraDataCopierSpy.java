package se.uu.ub.cora.basicdata.data;

import se.uu.ub.cora.data.DataElement;
import se.uu.ub.cora.data.copier.DataCopier;

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
