import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

class SchedulingPage extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.only(top: 8),
      child: SingleChildScrollView(
        child: Column(
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Text('Schedule daily cleanup'),
                Switch(
                  value: false,
                  onChanged: (newValue) {},
                )
              ],
            ),
            SizedBox(
              height: 16,
            ),
            Image.asset('assets/clock_large.png',
                width: 240, fit: BoxFit.cover),
            SizedBox(
              height: 64,
            ),
            Text('Daily database cleaning is off')
          ],
        ),
      ),
    );
  }
}
