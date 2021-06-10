package se.uu.ub.cora.basicdata.converter.datatojson;

import se.uu.ub.cora.basicdata.mcr.MethodCallRecorder;
import se.uu.ub.cora.json.builder.JsonArrayBuilder;
import se.uu.ub.cora.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.json.builder.JsonObjectBuilder;

public class JsonBuilderFactorySpy implements JsonBuilderFactory {

	MethodCallRecorder MCR = new MethodCallRecorder();

	@Override
	public JsonArrayBuilder createArrayBuilder() {
		JsonArrayBuilder out = null;
		MCR.addCall();
		MCR.addReturned(out);
		return out;
	}

	@Override
	public JsonObjectBuilder createObjectBuilder() {
		MCR.addCall();
		JsonObjectBuilderSpy jsonObjectBuilderSpy = new JsonObjectBuilderSpy();
		MCR.addReturned(jsonObjectBuilderSpy);
		return jsonObjectBuilderSpy;
	}

}
