import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:whatsapp_cleaner/screens/main_screen.dart';
import 'package:whatsapp_cleaner/stores/schedule_store.dart';

void main() => runApp(WhatsAppCleaner(title: 'WhatsApp Cleaner'));

class WhatsAppCleaner extends StatelessWidget {

  final title;

  WhatsAppCleaner({@required this.title});

  @override
  Widget build(BuildContext context) {
    return Provider<ScheduleStore>(
      create: (_) => ScheduleStore(),
      child: MaterialApp(
        title: 'WhatsApp Cleaner',
        theme: ThemeData(
          primarySwatch: Colors.indigo,
          visualDensity: VisualDensity.adaptivePlatformDensity,
        ),
        home: MainScreen(title: title,),
        debugShowCheckedModeBanner: false,
      ),
    );
  }
}
