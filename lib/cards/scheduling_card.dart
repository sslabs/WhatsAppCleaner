import 'package:flutter/material.dart';

class SchedulingCard extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Card(
      margin: EdgeInsets.symmetric(horizontal: 28.0),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          ListTile(
              trailing: Image.asset(
                'assets/clock_large.png',
              ),
              title: Text(
                'Database cleaning',
                style: TextStyle(fontWeight: FontWeight.w800, fontSize: 22),
              ),
              subtitle: Text(
                'Daily database cleaning is off',
                style: TextStyle(fontWeight: FontWeight.w600, fontSize: 18),
              )),
          Row(
            mainAxisAlignment: MainAxisAlignment.end,
            children: [
              TextButton(onPressed: () {}, child: const Text('SCHEDULE')),
            ],
          )
        ],
      ),
    );
  }
}
