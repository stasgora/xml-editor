import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

void main() => runApp(MyApp());

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'XML Editor',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: MyHomePage(title: 'Edit XML'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  MyHomePage({Key key, this.title}) : super(key: key);

  final String title;

  @override
  _MyHomePageState createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  static const platform = const MethodChannel("dev.sgora.xml_editor/validation");

  List<Widget> widgets = List();
  bool valid;

  Future<void> validateXML() async {
    bool valid;
    try {
      valid = await platform.invokeMethod('validateXML', {
        'xml': 'account-statement-1',
        'xsd': 'account-statement'
      });
    } on PlatformException catch (e) {
      debugPrint(e.message);
    }
    setState(() {
      this.valid = valid;
    });
  }

  void _incrementCounter() {
    setState(() {
      widgets.add(FlatButton(child: Text(valid.toString())));
      validateXML();
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: Center(
        child: Column(
          children: widgets,
        ),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: _incrementCounter,
        tooltip: 'Increment',
        child: Icon(Icons.add),
      ),
    );
  }
}
