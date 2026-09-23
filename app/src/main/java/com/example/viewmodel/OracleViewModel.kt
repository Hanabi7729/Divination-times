package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.DivinationEntity
import com.example.data.local.UserPreferences
import com.example.data.repository.DivinationRepository
import com.example.engine.ChoiceEngine
import com.example.engine.LocalizationManager
import com.example.engine.LocalizedStrings
import com.example.engine.YesNoEngine
import com.example.model.ChoiceResult
import com.example.model.CrystalCatalog
import com.example.model.CrystalSkin
import com.example.model.FortuneStyle
import com.example.model.YesNoAnswer
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OracleViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DivinationRepository
    private val connectivityManager: ConnectivityManager

    init {
        val database = AppDatabase.getInstance(application)
        val prefs = UserPreferences(application)
        repository = DivinationRepository(database.divinationDao(), prefs)
        connectivityManager = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    // Navigation Tab (0: Yes/No, 1: Decide, 2: Shop, 3: History)
    private val _currentTab = MutableStateFlow(0)
    val currentTab: StateFlow<Int> = _currentTab.asStateFlow()

    // Premium & Economy
    private val _isPremium = MutableStateFlow(repository.isPremium)
    val isPremium: StateFlow<Boolean> = _isPremium.asStateFlow()

    private val _stardust = MutableStateFlow(repository.stardust)
    val stardust: StateFlow<Int> = _stardust.asStateFlow()

    // Customization
    private val _selectedSkinId = MutableStateFlow(repository.selectedSkinId)
    val selectedSkinId: StateFlow<String> = _selectedSkinId.asStateFlow()

    private val _selectedAnimationId = MutableStateFlow(repository.selectedAnimationId)
    val selectedAnimationId: StateFlow<String> = _selectedAnimationId.asStateFlow()

    private val _selectedStyleId = MutableStateFlow(repository.selectedStyleId)
    val selectedStyleId: StateFlow<String> = _selectedStyleId.asStateFlow()

    private val _unlockedSkinIds = MutableStateFlow(repository.unlockedSkinIds)
    val unlockedSkinIds: StateFlow<Set<String>> = _unlockedSkinIds.asStateFlow()

    private val _unlockedAnimationIds = MutableStateFlow(repository.unlockedAnimationIds)
    val unlockedAnimationIds: StateFlow<Set<String>> = _unlockedAnimationIds.asStateFlow()

    private val _unlockedStyleIds = MutableStateFlow(repository.unlockedStyleIds)
    val unlockedStyleIds: StateFlow<Set<String>> = _unlockedStyleIds.asStateFlow()

    // Localization
    private val _languageCode = MutableStateFlow(repository.languageCode)
    val languageCode: StateFlow<String> = _languageCode.asStateFlow()

    private val _strings = MutableStateFlow(LocalizationManager.getStrings(repository.languageCode))
    val strings: StateFlow<LocalizedStrings> = _strings.asStateFlow()

    // Network & Offline simulation
    private val _simulateOffline = MutableStateFlow(repository.simulateOffline)
    val simulateOffline: StateFlow<Boolean> = _simulateOffline.asStateFlow()

    // Modals
    private val _showAdDialog = MutableStateFlow(false)
    val showAdDialog: StateFlow<Boolean> = _showAdDialog.asStateFlow()

    private val _showPremiumDialog = MutableStateFlow(false)
    val showPremiumDialog: StateFlow<Boolean> = _showPremiumDialog.asStateFlow()

    private val _showOfflineGateDialog = MutableStateFlow(false)
    val showOfflineGateDialog: StateFlow<Boolean> = _showOfflineGateDialog.asStateFlow()

    private val _showLanguageDialog = MutableStateFlow(false)
    val showLanguageDialog: StateFlow<Boolean> = _showLanguageDialog.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    // Yes/No Section State
    private val _yesNoQuestion = MutableStateFlow("")
    val yesNoQuestion: StateFlow<String> = _yesNoQuestion.asStateFlow()

    private val _isDiviningYesNo = MutableStateFlow(false)
    val isDiviningYesNo: StateFlow<Boolean> = _isDiviningYesNo.asStateFlow()

    private val _currentYesNoAnswer = MutableStateFlow<YesNoAnswer?>(null)
    val currentYesNoAnswer: StateFlow<YesNoAnswer?> = _currentYesNoAnswer.asStateFlow()

    // Choice / Decision Section State
    private val _choiceOptions = MutableStateFlow(
        listOf("Pizza Night", "Sushi Bar", "Mexican Tacos", "Home Cooking")
    )
    val choiceOptions: StateFlow<List<String>> = _choiceOptions.asStateFlow()

    private val _isDiviningChoice = MutableStateFlow(false)
    val isDiviningChoice: StateFlow<Boolean> = _isDiviningChoice.asStateFlow()

    private val _currentChoiceResult = MutableStateFlow<ChoiceResult?>(null)
    val currentChoiceResult: StateFlow<ChoiceResult?> = _currentChoiceResult.asStateFlow()

    // History Flow
    val allHistory: StateFlow<List<DivinationEntity>> = repository.allRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Helpers
    val activeSkin: CrystalSkin
        get() = CrystalCatalog.getSkin(_selectedSkinId.value)

    val activeStyle: FortuneStyle
        get() = CrystalCatalog.getStyle(_selectedStyleId.value)

    private fun isActuallyOnline(): Boolean {
        if (_simulateOffline.value) return false
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun checkOfflineAccessAllowed(): Boolean {
        // If device is online, anyone can divine.
        // If device is offline (or simulated offline), only Premium users can divine!
        val online = isActuallyOnline()
        if (online) return true
        if (_isPremium.value) return true
        // Blocked: Show Offline gate!
        _showOfflineGateDialog.value = true
        return false
    }

    fun setTab(index: Int) {
        _currentTab.value = index
    }

    fun setYesNoQuestion(q: String) {
        _yesNoQuestion.value = q
    }

    fun divineYesNo() {
        if (_isDiviningYesNo.value) return
        if (!checkOfflineAccessAllowed()) return

        val question = _yesNoQuestion.value
        viewModelScope.launch {
            _isDiviningYesNo.value = true
            _currentYesNoAnswer.value = null
            delay(1600) // Arcane focus transition

            val answer = YesNoEngine.divine(
                question = question,
                styleId = _selectedStyleId.value,
                languageCode = _languageCode.value
            )
            _currentYesNoAnswer.value = answer
            _isDiviningYesNo.value = false

            // Reward Stardust (2x if Premium)
            val stardustEarned = if (_isPremium.value) 50 else 25
            addStardust(stardustEarned)

            // Save to Room DB
            repository.saveDivination(
                DivinationEntity(
                    type = "YES_NO",
                    questionOrTitle = question.ifBlank { "Unspoken Query to the Stars" },
                    answerOrChoice = answer.shortAnswer,
                    cosmicInsight = answer.cosmicWisdom,
                    styleUsed = activeStyle.name,
                    skinUsed = activeSkin.name,
                    alignmentScore = answer.alignmentPercentage
                )
            )

            // Check Ad trigger (every 3 divinations for free users)
            incrementDivinationCount()
        }
    }

    fun addChoiceOption(option: String) {
        val trimmed = option.trim()
        if (trimmed.isNotBlank() && !_choiceOptions.value.contains(trimmed)) {
            _choiceOptions.value = _choiceOptions.value + trimmed
        }
    }

    fun removeChoiceOption(option: String) {
        _choiceOptions.value = _choiceOptions.value.filter { it != option }
    }

    fun setChoicePreset(options: List<String>) {
        _choiceOptions.value = options
        _currentChoiceResult.value = null
    }

    fun divineChoice() {
        if (_isDiviningChoice.value) return
        if (!checkOfflineAccessAllowed()) return
        if (_choiceOptions.value.size < 2) return

        viewModelScope.launch {
            _isDiviningChoice.value = true
            _currentChoiceResult.value = null
            delay(1800)

            val result = ChoiceEngine.divineChoice(
                options = _choiceOptions.value,
                styleId = _selectedStyleId.value,
                styleName = activeStyle.name
            )
            _currentChoiceResult.value = result
            _isDiviningChoice.value = false

            val stardustEarned = if (_isPremium.value) 50 else 25
            addStardust(stardustEarned)

            repository.saveDivination(
                DivinationEntity(
                    type = "CHOICE",
                    questionOrTitle = "Choice Consultation",
                    optionsJson = _choiceOptions.value.joinToString(", "),
                    answerOrChoice = result.selectedOption,
                    cosmicInsight = result.propheticReason,
                    styleUsed = result.styleUsed,
                    skinUsed = activeSkin.name,
                    alignmentScore = result.cosmicHarmony
                )
            )

            incrementDivinationCount()
        }
    }

    private fun incrementDivinationCount() {
        val newCount = repository.divinationCount + 1
        repository.divinationCount = newCount
        if (!_isPremium.value && newCount % 3 == 0) {
            _showAdDialog.value = true
        }
    }

    fun addStardust(amount: Int) {
        val newAmount = repository.stardust + amount
        repository.stardust = newAmount
        _stardust.value = newAmount
    }

    fun equipSkin(skinId: String) {
        val skin = CrystalCatalog.getSkin(skinId)
        if (skin.isPremiumOnly && !_isPremium.value) {
            _showPremiumDialog.value = true
            return
        }
        if (_unlockedSkinIds.value.contains(skinId) || (_isPremium.value && skin.isPremiumOnly)) {
            repository.selectedSkinId = skinId
            _selectedSkinId.value = skinId
        }
    }

    fun unlockSkin(skinId: String) {
        val skin = CrystalCatalog.getSkin(skinId)
        if (skin.isPremiumOnly) {
            _showPremiumDialog.value = true
            return
        }
        if (_stardust.value >= skin.priceStardust) {
            addStardust(-skin.priceStardust)
            val updated = _unlockedSkinIds.value + skinId
            repository.unlockedSkinIds = updated
            _unlockedSkinIds.value = updated
            equipSkin(skinId)
        } else {
            _statusMessage.value = _strings.value.needMoreStardust
        }
    }

    fun equipAnimation(animId: String) {
        if (_unlockedAnimationIds.value.contains(animId)) {
            repository.selectedAnimationId = animId
            _selectedAnimationId.value = animId
        }
    }

    fun unlockAnimation(animId: String) {
        val anim = CrystalCatalog.getAnimation(animId)
        if (_stardust.value >= anim.priceStardust) {
            addStardust(-anim.priceStardust)
            val updated = _unlockedAnimationIds.value + animId
            repository.unlockedAnimationIds = updated
            _unlockedAnimationIds.value = updated
            equipAnimation(animId)
        } else {
            _statusMessage.value = _strings.value.needMoreStardust
        }
    }

    fun equipStyle(styleId: String) {
        if (_isPremium.value || _unlockedStyleIds.value.contains(styleId)) {
            repository.selectedStyleId = styleId
            _selectedStyleId.value = styleId
        }
    }

    fun unlockStyle(styleId: String) {
        val style = CrystalCatalog.getStyle(styleId)
        if (_stardust.value >= style.priceStardust) {
            addStardust(-style.priceStardust)
            val updated = _unlockedStyleIds.value + styleId
            repository.unlockedStyleIds = updated
            _unlockedStyleIds.value = updated
            equipStyle(styleId)
        } else {
            _statusMessage.value = _strings.value.needMoreStardust
        }
    }

    fun claimDailyMeditation() {
        val now = System.currentTimeMillis()
        val oneDayMillis = 24 * 60 * 60 * 1000L
        if (now - repository.lastDailyMeditationDate >= oneDayMillis) {
            repository.lastDailyMeditationDate = now
            addStardust(100)
            _statusMessage.value = _strings.value.meditateBonusReceived
        } else {
            _statusMessage.value = "Daily crystal leylines already absorbed for today. Return tomorrow!"
        }
    }

    fun upgradeToPremium() {
        repository.isPremium = true
        _isPremium.value = true
        // Unlock Archmage skin and equip
        val updatedSkins = _unlockedSkinIds.value + "archmage"
        repository.unlockedSkinIds = updatedSkins
        _unlockedSkinIds.value = updatedSkins
        equipSkin("archmage")
        // Dismiss any ad/offline blocker
        _showAdDialog.value = false
        _showOfflineGateDialog.value = false
        _statusMessage.value = _strings.value.premiumUnlockedTitle
    }

    fun setLanguage(langCode: String) {
        repository.languageCode = langCode
        _languageCode.value = langCode
        _strings.value = LocalizationManager.getStrings(langCode)
    }

    fun toggleSimulateOffline() {
        val toggled = !_simulateOffline.value
        repository.simulateOffline = toggled
        _simulateOffline.value = toggled
    }

    fun toggleFavorite(recordId: Long, currentFav: Boolean) {
        viewModelScope.launch {
            repository.toggleFavorite(recordId, !currentFav)
        }
    }

    fun deleteHistoryRecord(recordId: Long) {
        viewModelScope.launch {
            repository.deleteRecord(recordId)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    fun dismissAdDialog() {
        _showAdDialog.value = false
    }

    fun showPremium() {
        _showPremiumDialog.value = true
    }

    fun dismissPremium() {
        _showPremiumDialog.value = false
    }

    fun dismissOfflineGate() {
        _showOfflineGateDialog.value = false
    }

    fun showLanguage() {
        _showLanguageDialog.value = true
    }

    fun dismissLanguage() {
        _showLanguageDialog.value = false
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }
}
