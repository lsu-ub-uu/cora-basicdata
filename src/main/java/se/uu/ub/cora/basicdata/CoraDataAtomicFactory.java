package se.uu.ub.cora.basicdata;

import se.uu.ub.cora.data.DataAtomic;

public class CoraDataAtomicFactory implements DataAtomicFactory {

	@Override
	public DataAtomic factorUsingNameInDataAndValue(String nameInData, String value) {
		return CoraDataAtomic.withNameInDataAndValue(nameInData, value);
	}

}
