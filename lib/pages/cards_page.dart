import 'package:flutter/material.dart';
import 'package:whatsapp_cleaner/cards/scheduling_card.dart';

class CardsPage extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Column(children: [
      Padding(
        padding: const EdgeInsets.only(top: 10.0),
        child: SchedulingCard(),
      )
    ]);
  }
}
