package core.util

import core.util.Constants.MAX_NAME_LENGTH
import core.util.Constants.MAX_PASSWORD_LENGTH
import core.util.Constants.MAX_TEMPLATE_ITEMS
import core.util.Constants.MAX_TEMPLATE_NAME_LENGTH
import core.util.Constants.MIN_NAME_LENGTH
import core.util.Constants.MIN_PASSWORD_LENGTH
import feature_auth.presentation.util.AuthError

object StringResources {

    const val EMPTY_PAYMENT_AND_CASH = "Não há nenhuma informação nos filtros selecionados."
    const val JOB_IS_EMPTY = "   Não há jobs cadastrados."
    const val JOB_LIST_IS_EMPTY = "Nenhum job encontrado."
    const val HINT_SEARCH_DROPDOWN = "Job"
    const val ERROR_UNKNOWN = "Um erro desconhecido ocorreu."
    const val GO_BACK = "Voltar"
    const val HIDE_PASSWORD = "Hide password"
    const val SHOW_PASSWORD = "Show password"
    const val EMPTY_FIELD = "O campo não pode estar vazio."
    const val ERROR_MAX_LENGTH = "Esse campo só pode ter no máximo $MAX_TEMPLATE_NAME_LENGTH caracteres"
    const val ERROR_LIST_FULL = "A lista está cheia. Você só pode adicionar no máximo $MAX_TEMPLATE_ITEMS elementos."
    const val INVALID_JOB = "Esse job é invalido."
    const val CONNECTION_NULL = "Erro de conexão. Verifique sua internet."
    const val QUERY_ERROR = "Erro na query: "

    const val INVALID_DATE = "Data inválida."
    const val INVALID_YEAR = "Ano inválido."
    const val INITIAL_DATE_LATER_THAN_FINAL_DATE = "A data final deve ser maior que a data inicial."
    const val INITIAL_DATE_LATER_THAN_CREATED_DATE = "A data de criação deve ser maior que a data inicial."
    const val CREATE_DATE = "Data de criação (dd/mm/aaaa)"
    const val INITIAL_DATE = "Data inicial (dd/mm/aaaa)"
    const val END_DATE = "Data final (dd/mm/aaaa)"

    const val EXPORT_TO_EXCEL = "Exportar para excel"
    const val JOB_NUMBER = "Job"
    const val LIMIT_BY_CREATE_DATE = "Limitar pela data de criação"
    const val USE_ALTERNATIVE_CODE = "Usar código alternativo"

    const val ERROR_FILE_ALREADY_OPENED = "Verifique se o arquivo já não está aberto e feche-o."
    const val ERROR_SELECT_AT_LEAST_ONE_OPTION_TO_CREATE = "Selecione Cash e/ou Geral."

    const val TEXT_CREATE = "Criar"
    const val TEXT_SPLIT = "Separar"
    const val TEXT_GERAL = "Geral"
    const val TEXT_EXEC = "Exec"
    const val TEXT_PROD = "Prod"
    const val TEXT_POS = "Pos"
    const val TEXT_CASH = "Cash"

    const val CANCEL_BUTTON = "Cancelar"
    const val SAVE_BUTTON = "Salvar"

    //Auth
    const val HINT_FIELD_FIRST_NAME = "Nome"
    const val HINT_FIELD_LAST_NAME = "Sobrenome"
    const val HINT_FIELD_EMAIL = "Email"
    const val HINT_FIELD_PASSWORD = "Senha"
    const val HINT_FIELD_CONFIRM_PASSWORD = "Confirme sua senha"
    const val BUTTON_LOGIN = "Logar"
    const val BUTTON_REGISTER = "Cadastrar"
        //Errors
    const val ERROR_NAME_TOO_SHORT = "Esse campo precisa possuir no mínimo $MIN_NAME_LENGTH caracteres."
    const val ERROR_NAME_TOO_LONG = "Esse campo precisa possuir no máximo $MAX_NAME_LENGTH caracteres."
    const val ERROR_INVALID_NAME = "Esse nome possui caracteres inválidos."

    const val ERROR_PASSWORD_TOO_SHORT = "Esse campo precisa possuir no mínimo $MIN_PASSWORD_LENGTH caracteres."
    const val ERROR_PASSWORD_TOO_LONG = "Esse campo precisa possuir no máximo $MAX_PASSWORD_LENGTH caracteres."
    const val ERROR_INVALID_PASSWORD = "A senha deve possuir ao menos uma letra maiúscula, uma minúscula, um número e um caracter especial."
    const val ERROR_INVALID_PASSWORD_MATCH = "As senhas novas não batem."

    const val ERROR_INVALID_EMAIL = "Esse email é inválido."

    const val PERMISSION_NOT_GRANTED = "Você não tem permissão para fazer esta ação."

    //Themes
    const val LIGHT_THEMES_TEXT = "Temas claros:"
    const val DARK_THEMES_TEXT = "Temas escuros:"
}