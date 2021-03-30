import 'package:flutter/material.dart';

class CardsPage extends StatelessWidget {

  final List<Widget> cards;

  CardsPage({@required this.cards});

  @override
  Widget build(BuildContext context) {
    return ListView(
      padding: const EdgeInsets.only(top: 10.0),
      children: cards,
    );
  }
}
