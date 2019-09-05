package se.uu.ub.cora.basicdata;

import se.uu.ub.cora.data.DataAtomic;

public interface DataAtomicFactory {

	DataAtomic factorUsingNameInDataAndValue(String nameInData, String value);

}
