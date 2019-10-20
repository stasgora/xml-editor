package dev.sgora.xml_editor

import android.os.Bundle
import dev.sgora.xml_editor.validation.ValidationErrorHandler
import io.flutter.app.FlutterActivity
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugins.GeneratedPluginRegistrant
import mf.javax.xml.transform.stream.StreamSource
import mf.org.apache.xerces.jaxp.validation.XMLSchemaFactory


class MainActivity : FlutterActivity() {
	private val TAG = javaClass.name
	private val CHANNEL = "dev.sgora.xml_editor/validation"

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		GeneratedPluginRegistrant.registerWith(this)

		MethodChannel(flutterView, CHANNEL).setMethodCallHandler { call, result ->
			when (call.method) {
				"validateXML" -> result.success(validateXML(call.argument<String>("xml").orEmpty(), call.argument<String>("xsd").orEmpty()))
				else -> result.notImplemented()
			}
		}
	}

	private fun validateXML(xml: String, xsd: String): Boolean {
		//val xml = assets.open("flutter_assets/assets/$xsd.xsd").bufferedReader().use { it.readText() }
		//Log.d(TAG, xml.lines().size.toString())
		//val unmarshaller = JAXBContext.newInstance(AccountStatement::class.java).createUnmarshaller()
		//unmarshaller.eventHandler = ValidationErrorHandler()
		//val factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
		//unmarshaller.schema = factory.newSchema(StreamSource(assets.open("flutter_assets/assets/$xsd.xsd")))
		//unmarshaller.unmarshal(StreamSource(assets.open("flutter_assets/assets/$xml.xsd")))

		val schema = XMLSchemaFactory().newSchema(StreamSource(assets.open("flutter_assets/assets/$xsd.xsd")))
		val validator = schema.newValidator()
		validator.errorHandler = ValidationErrorHandler(validator)
		validator.validate(StreamSource(assets.open("flutter_assets/assets/$xml.xml")))
		return true
	}
}
