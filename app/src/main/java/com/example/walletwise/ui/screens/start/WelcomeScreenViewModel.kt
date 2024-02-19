package com.example.walletwise.ui.screens.start

import androidx.annotation.RawRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walletwise.R
import com.example.walletwise.data.PreferenceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WelcomeScreenViewModel(private val preferenceRepository: PreferenceRepository) : ViewModel() {
    private val des =
        arrayOf(
            "Keep track of your daily expenses with ease. Stay on top of your budget and make informed financial decisions.",
            "Logging daily expenses has never been this easy. With WalletWise, you can quickly input your expenses and see where your money goes, day by day.",
            "Your data is stored locally on your device, your personal expense data isn't uploaded anywhere. Plus, set up a secure pass lock to ensure only you can access your financial data."
        )

    @RawRes
    private val anims = arrayOf(R.raw.opening, R.raw.daily, R.raw.security)

    private val _uiState = MutableStateFlow(WelcomeUiState(desc = des[0], anim = anims[0]))
    val uiState = _uiState.asStateFlow()

    private fun updateContent(index: Int) {
        if (index != des.size) {
            _uiState.update {
                it.copy(desc = des[index], anim = anims[index])
            }
        }
    }

    fun updateInput(input: String) {
        _uiState.update {
            it.copy(input = input)
        }
    }

    fun submit(last: Boolean, index: Int) {
        viewModelScope.launch {
            if (last) {
                preferenceRepository.finishWelcome()
                preferenceRepository.enablePin(true)
                preferenceRepository.setPass(uiState.value.input)
                _uiState.update {
                    it.copy(completed = true)
                }
            } else {
                if (index == 1)
                    preferenceRepository.setName(uiState.value.input)
                updateContent(index)
            }
            _uiState.update {
                it.copy(input = "")
            }
        }
    }
}

data class WelcomeUiState(
    val desc: String = "",
    @RawRes val anim: Int = 0,
    val input: String = "",
    val completed:Boolean = false
)