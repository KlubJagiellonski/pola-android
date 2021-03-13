package pl.pola_app.ui.delegate

interface HelpMessageFragmentDelegate {
    fun onWantToHelpButtonClick(doNotShowNextTime: Boolean)
    fun onNextTimeButtonClick(doNotShowNextTime: Boolean)
}