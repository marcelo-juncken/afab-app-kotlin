package feature_payments.presentation

import feature_payments.presentation.util.PaidType
import androidx.compose.ui.graphics.Color
import core.data.PaymentTemplateItem
import feature_payments.domain.models.JobOrder

sealed interface PaymentsFormEvent {
    data class JobChanged(val job: JobOrder?, val searchText : String) : PaymentsFormEvent
    data class IniDateChanged(val date: String) : PaymentsFormEvent
    data class EndDateChanged(val date: String) : PaymentsFormEvent
    data class CreateDateChanged(val date: String) : PaymentsFormEvent
    data class ColorType(val type: PaidType?, val color: Color) : PaymentsFormEvent
    data class CbCreateDateToggled(val isChecked: Boolean) : PaymentsFormEvent
    data class CbAlternativeCodeToggled(val isChecked: Boolean) : PaymentsFormEvent
    data class CbCreateGeralToggled(val isChecked: Boolean) : PaymentsFormEvent
    data class CbCreateExecToggled(val isChecked: Boolean) : PaymentsFormEvent
    data class CbCreateProdToggled(val isChecked: Boolean) : PaymentsFormEvent
    data class CbCreatePosToggled(val isChecked: Boolean) : PaymentsFormEvent
    data class CbCreateCashToggled(val isChecked: Boolean) : PaymentsFormEvent
    data class CbSplitGeralToggled(val isChecked: Boolean) : PaymentsFormEvent
    data class CbSplitExecToggled(val isChecked: Boolean) : PaymentsFormEvent
    data class CbSplitProdToggled(val isChecked: Boolean) : PaymentsFormEvent
    data class CbSplitPosToggled(val isChecked: Boolean) : PaymentsFormEvent
    data class CbSplitCashToggled(val isChecked: Boolean) : PaymentsFormEvent

    data class DeleteTemplate(val paymentTemplateItem: PaymentTemplateItem) : PaymentsFormEvent
    data class SaveTemplate(val templateName : String) : PaymentsFormEvent
    class EditTemplate(val templateItem: PaymentTemplateItem, val templateNewName: String) : PaymentsFormEvent
    class LoadTemplate(val templateItem: PaymentTemplateItem) : PaymentsFormEvent

    object CancelSaveTemplate : PaymentsFormEvent

    object Submit : PaymentsFormEvent
    object Logout : PaymentsFormEvent
}