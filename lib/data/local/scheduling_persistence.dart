import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';

class SchedulingPersistence {
  static const String _HOUR_KEY = 'HOUR';
  static const String _MINUTE_KEY = 'MINUTE';

  static final SchedulingPersistence _instance =
      SchedulingPersistence._internal();

  SchedulingPersistence._internal();

  factory SchedulingPersistence() {
    return _instance;
  }

  Future<TimeOfDay> readSchedulingTime() async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    var hour = prefs.getInt(_HOUR_KEY) ?? -1;
    var minute = prefs.getInt(_MINUTE_KEY) ?? -1;

    return hour != -1 && minute != -1
        ? TimeOfDay(hour: hour, minute: minute)
        : null;
  }

  Future<bool> writeSchedulingTime(TimeOfDay scheduling) async {
    SharedPreferences prefs = await SharedPreferences.getInstance();

    return await prefs.setInt(_HOUR_KEY, scheduling.hour) &&
        await prefs.setInt(_MINUTE_KEY, scheduling.minute);
  }

  Future<bool> clearSchedulingTime() async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    return await prefs.clear();
  }
}
