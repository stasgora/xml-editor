package dev.sgora.xml_editor.validation

import io.flutter.Log
import mf.javax.xml.validation.Validator
import mf.org.apache.xerces.impl.Constants
import mf.org.w3c.dom.Node
import org.xml.sax.ErrorHandler
import org.xml.sax.SAXNotRecognizedException
import org.xml.sax.SAXNotSupportedException
import org.xml.sax.SAXParseException

class ValidationErrorHandler(private val xsdValidator: Validator) : ErrorHandler {
	private val TAG = javaClass.name

	override fun warning(exception: SAXParseException) {
		handle(exception)
	}

	override fun error(exception: SAXParseException) {
		handle(exception)
	}

	override fun fatalError(exception: SAXParseException) {
		handle(exception)
	}

	private fun handle(exception: SAXParseException) {
		Log.d(TAG, exception.publicId)
		Log.d(TAG, exception.systemId)
		Log.d(TAG, exception.lineNumber.toString())
		Log.d(TAG, exception.columnNumber.toString())
		getCurrentNode()
	}

	@Throws(SAXNotRecognizedException::class, SAXNotSupportedException::class)
	private fun getCurrentNode() {
		val node = xsdValidator.getProperty(Constants.XERCES_PROPERTY_PREFIX + Constants.CURRENT_ELEMENT_NODE_PROPERTY) as Node
		Log.d(TAG, node.nodeName)
	}
}