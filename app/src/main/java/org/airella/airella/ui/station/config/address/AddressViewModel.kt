package org.airella.airella.ui.station.config.address

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.airella.airella.data.model.common.Address

class AddressViewModel : ViewModel() {

    var address: MutableLiveData<Address> = MutableLiveData()


}