package com.sslabs.whatsappcleaner.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Transformations
import com.sslabs.whatsappcleaner.R
import com.sslabs.whatsappcleaner.repository.Repository
import com.sslabs.whatsappcleaner.shortFormat

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val scheduling = Repository.getScheduling(application.baseContext)

    val schedulingTimeText = Transformations.map(scheduling) {
        if (it == null) {
            application.getString(R.string.schedule_card_off_body)
        } else {
            application.getString(R.string.schedule_card_on_body, it.shortFormat())
        }
    }

    val cardActionText = Transformations.map(scheduling) {
        if (it == null) {
            application.getString(R.string.schedule_card_set_action_text)
        } else {
            application.getString(R.string.schedule_card_change_action_text)
        }
    }
}
