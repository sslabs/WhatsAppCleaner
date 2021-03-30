import 'package:flutter/material.dart';
import 'package:flutter_mobx/flutter_mobx.dart';
import 'package:provider/provider.dart';
import 'package:whatsapp_cleaner/stores/schedule_store.dart';

class SchedulingCard extends StatefulWidget {

  final VoidCallback action;

  SchedulingCard({this.action, Key key}) : super(key: key);

  @override
  _SchedulingCardState createState() => _SchedulingCardState();
}

class _SchedulingCardState extends State<SchedulingCard> {
  ScheduleStore scheduleStore;

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    scheduleStore = Provider.of<ScheduleStore>(context);
  }

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
              subtitle: Observer(
                builder: (_) => scheduleStore.isScheduled
                    ? RichText(
                        text: TextSpan(
                            style: TextStyle(
                                fontWeight: FontWeight.w600,
                                fontSize: 18.0,
                                color: Colors.black54),
                            children: [
                              TextSpan(
                                  text:
                                      'Daily database cleaning scheduled to '),
                              TextSpan(
                                  text:
                                      '${scheduleStore.scheduledTime.format(context)}',
                                  style: TextStyle(
                                      fontWeight: FontWeight.w600,
                                      color: Colors.black87)),
                              TextSpan(text: '.')
                            ]),
                      )
                    : Text(
                        'Daily database cleaning is off',
                        style: TextStyle(
                            fontWeight: FontWeight.w600, fontSize: 18),
                      ),
              )),
          Row(
            mainAxisAlignment: MainAxisAlignment.start,
            children: [
              TextButton(
                  onPressed: widget.action,
                  child: Observer(
                      builder: (_) => scheduleStore.isScheduled
                          ? Text('CHANGE')
                          : Text('SCHEDULE'))),
            ],
          )
        ],
      ),
    );
  }
}
