import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter_mobx/flutter_mobx.dart';
import 'package:provider/provider.dart';
import 'package:whatsapp_cleaner/stores/schedule_store.dart';

class SchedulingPage extends StatefulWidget {
  @override
  _SchedulingPageState createState() => _SchedulingPageState();
}

class _SchedulingPageState extends State<SchedulingPage> {
  ScheduleStore scheduleStore;

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    scheduleStore = Provider.of<ScheduleStore>(context);
  }

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
                Observer(
                  builder: (_) => Switch(
                    value: scheduleStore.isScheduled,
                    onChanged: toggleScheduling,
                  ),
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
            Observer(
              builder: (_) => SizedBox(
                  width: 240,
                  child: scheduleStore.isScheduled
                      ? RichText(
                          text: TextSpan(
                              style: TextStyle(
                                  fontSize: 14.0, color: Colors.black),
                              children: [
                                TextSpan(
                                    text:
                                        'Daily cleaning of WhatsApp databases is scheduled for '),
                                TextSpan(
                                    text:
                                        '${scheduleStore.scheduledTime.format(context)}',
                                    style:
                                        TextStyle(fontWeight: FontWeight.w600)),
                                TextSpan(text: '.')
                              ]),
                        )
                      : Text('Daily database cleaning is off')),
            )
          ],
        ),
      ),
    );
  }

  void toggleScheduling(bool enable) async {
    if (enable) {
      var selectedTime = await showTimePicker(
        initialTime: TimeOfDay.now(),
        context: context,
      );
      scheduleStore.scheduleTime(selectedTime);
    } else {
      scheduleStore.scheduleTime(null);
    }
  }
}
