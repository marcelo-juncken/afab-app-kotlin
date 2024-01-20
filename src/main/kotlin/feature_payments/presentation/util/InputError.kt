package feature_payments.presentation.util
import core.util.Error

sealed class InputError : Error() {
    object EmptyField : InputError()
    object InvalidDate : InputError()
    object InvalidYear : InputError()
    object InitialDateLaterThanEndDate : InputError()
    object InitialDateLaterThanCreatedDate : InputError()
    object InvalidJob : InputError()
    object EmptyList : InputError()
    object EmptyCreate : InputError()
    object MaxLengthError : InputError()
}
