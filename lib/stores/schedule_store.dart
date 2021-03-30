import 'package:flutter/material.dart';
import 'package:mobx/mobx.dart';
import 'package:whatsapp_cleaner/data/local/scheduling_persistence.dart';

part 'schedule_store.g.dart';

class ScheduleStore = _ScheduleStore with _$ScheduleStore;

abstract class _ScheduleStore with Store {
  SchedulingPersistence _schedulingPersistence = SchedulingPersistence();

  @observable
  ObservableFuture<TimeOfDay> scheduledTime;

  @computed
  bool get isScheduled => scheduledTime?.value != null;

  _ScheduleStore() {
    scheduledTime = _loadScheduling();
  }

  @action
  Future<void> scheduleTime(TimeOfDay time) async {
    var success = time != null
        ? await _schedulingPersistence.writeSchedulingTime(time)
        : await _schedulingPersistence.clearSchedulingTime();

    scheduledTime = success
        ? scheduledTime = _loadScheduling()
        : ObservableFuture(Future<TimeOfDay>(() => null));
  }

  ObservableFuture<TimeOfDay> _loadScheduling() =>
      ObservableFuture(_schedulingPersistence.readSchedulingTime());
}
