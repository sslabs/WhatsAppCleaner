package com.sslabs.whatsappcleaner.viewmodel

import androidx.lifecycle.ViewModel
import com.sslabs.whatsappcleaner.repository.Repository

class HomeViewModel(repository: Repository) : ViewModel() {
    val scheduling = repository.getScheduling()
}
