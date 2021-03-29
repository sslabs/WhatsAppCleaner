import 'package:flutter/material.dart';
import 'package:mobx/mobx.dart';

part 'schedule_store.g.dart';

class ScheduleStore = _ScheduleStore with _$ScheduleStore;

abstract class _ScheduleStore with Store {

  @observable
  TimeOfDay scheduledTime;

  @action
  void scheduleTime(TimeOfDay time) {
    scheduledTime = time;
  }

  @computed
  bool get isScheduled => scheduledTime != null;
}