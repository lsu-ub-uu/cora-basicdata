import se.uu.ub.cora.basicdata.CoraDataFactory;
import se.uu.ub.cora.basicdata.converter.datatojson.BasicDataToJsonConverterFactoryCreator;
import se.uu.ub.cora.basicdata.converter.jsontodata.JsonToDataConverterFactoryImp;
import se.uu.ub.cora.basicdata.copier.CoraDataCopierFactoryImp;

module se.uu.ub.cora.basicdata {
	requires transitive se.uu.ub.cora.json;
	requires transitive se.uu.ub.cora.data;

	exports se.uu.ub.cora.basicdata.converter;
	exports se.uu.ub.cora.basicdata.converter.datatojson;
	exports se.uu.ub.cora.basicdata.converter.jsontodata;

	provides se.uu.ub.cora.data.DataFactory with CoraDataFactory;

	provides se.uu.ub.cora.data.converter.JsonToDataConverterFactory
			with JsonToDataConverterFactoryImp;
	provides se.uu.ub.cora.data.converter.DataToJsonConverterFactoryCreator
			with BasicDataToJsonConverterFactoryCreator;
	provides se.uu.ub.cora.data.copier.DataCopierFactory with CoraDataCopierFactoryImp;
}