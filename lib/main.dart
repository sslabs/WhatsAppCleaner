import 'package:flutter/material.dart';
import 'package:whatsapp_cleaner/screens/main_screen.dart';

void main() => runApp(WhatsAppCleaner(title: 'WhatsApp Cleaner'));

class WhatsAppCleaner extends StatelessWidget {

  final title;

  WhatsAppCleaner({@required this.title});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'WhatsApp Cleaner',
      theme: ThemeData(
        primarySwatch: Colors.indigo,
        visualDensity: VisualDensity.adaptivePlatformDensity,
      ),
      home: MainScreen(title: title,),
      debugShowCheckedModeBanner: false,
    );
  }
}
