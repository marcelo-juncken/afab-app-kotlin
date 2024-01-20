package di

import com.google.gson.GsonBuilder
import core.data.remote.DBConnection
import core.data.remote.JDBCConnection
import feature_auth.data.local.DataProtection
import feature_auth.data.local.TokenStorage
import feature_auth.data.local.platform.WindowsDataProtection
import feature_auth.data.local.platform.WindowsTokenStorage
import feature_auth.data.remote.Auth0Retrofit
import feature_auth.data.repository.AppUserRepositoryImpl
import feature_auth.data.repository.AuthRepositoryImpl
import feature_auth.data.repository.datasource.IDatabaseConnection
import feature_auth.data.repository.datasource.MongoDBConnectionImpl
import feature_auth.domain.repository.AppUserRepository
import feature_auth.domain.repository.AuthRepository
import feature_auth.domain.use_case.*
import feature_auth.presentation.login.LoginViewModel
import feature_auth.presentation.register.RegisterViewModel
import feature_config.domain.use_case.*
import feature_config.presentation.DevicesViewModel
import feature_config.presentation.SettingsViewModel
import feature_payments.data.local.Converters
import feature_payments.data.repository.ExcelRepositoryImpl
import feature_payments.data.repository.PaymentTemplateRepositoryImpl
import feature_payments.data.repository.PaymentsRepositoryImpl
import feature_payments.data.util.GsonParser
import feature_payments.data.util.JsonParser
import feature_payments.domain.repository.ExcelRepository
import feature_payments.domain.repository.PaymentTemplateRepository
import feature_payments.domain.repository.PaymentsRepository
import feature_payments.domain.use_case.data.GetJobsUseCase
import feature_payments.domain.use_case.data.GetPaymentAndCashUseCase
import feature_payments.domain.use_case.excel.*
import feature_payments.domain.use_case.template.*
import feature_payments.presentation.PaymentsViewModel
import feature_splash.SplashViewModel
import org.koin.dsl.module


val myModule = module {
    single<DBConnection> { JDBCConnection }

    //SQL DATA
    single<PaymentsRepository> {
        PaymentsRepositoryImpl(get())
    }
    single { GetPaymentAndCashUseCase(get(), get()) }
    single { GetJobsUseCase(get()) }

    //Excel
    single<ExcelRepository> {
        ExcelRepositoryImpl()
    }
    single { GeralSheetUseCase(get()) }
    single { CashSheetUseCase(get()) }
    single { SortSheetUseCase(get()) }
    single { SplitSheetsUseCase(get()) }
    single { CreateLegendsUseCase(get()) }
    single { SaveWorkbookUseCase(get()) }
    single { ManageTemplatesUseCase(get(), get(), get(), get()) }
    single { SavePaymentTemplateUseCase(get(), get(), get()) }
    single { LoadPaymentTemplatesUseCase(get(), get(), get()) }
    single { DeletePaymentTemplateUseCase(get(), get(), get()) }
    single { EditPaymentNameTemplateUseCase(get(), get(), get()) }
    single { CheckLoggedInUserUseCase(get(), get()) }

    single { ExcelUseCase(get(), get(), get(), get(), get(), get()) }


    single<IDatabaseConnection> {
        MongoDBConnectionImpl(
            domain = getProperty("mongo.domain"),
            dbName = getProperty("mongo.dbName"),
            user = getProperty("mongo.user"),
            password = getProperty("mongo.password")
        )
    }

    factory { LoginViewModel(get()) }
    factory { RegisterViewModel(get()) }
    single { PaymentsViewModel(get(), get(), get(), get(), get(), get()) }
    single { SettingsViewModel(get(), get(), get()) }
    factory { SplashViewModel(get(), get(), get(), get()) }
    factory { DevicesViewModel(get(), get(), get()) }


    //Auth
    single { Auth0Retrofit.auth0Api }
    single<TokenStorage> { WindowsTokenStorage(get()) }
    single<DataProtection> { WindowsDataProtection() }
    single<AuthRepository> {
        AuthRepositoryImpl(
            auth0Api = get(),
            domain = getProperty("auth0.domain"),
            clientId = getProperty("auth0.clientId"),
            audience = getProperty("auth0.audience")
        )
    }

    single<AppUserRepository> {
        AppUserRepositoryImpl(get())
    }

    single<PaymentTemplateRepository> {
        PaymentTemplateRepositoryImpl(get())
    }

    // Auth
    single { RegisterUseCase(get(), get(), get()) }
    single { LoginUseCase(get(), get(), get()) }
    single { LogoutUserUseCase(get()) }
    single { CheckAdminRoleUseCase(get(), get()) }
    single { GetUsersDevicesUseCase(get()) }
    single { RemoveUserDeviceUseCase(get(), get()) }
    single { ToggleUserAccessUseCase(get(), get()) }
    single { SaveDeviceUseCase(get()) }
    single { SaveThemeUseCase(get()) }
    single { LoadThemeUseCase(get()) }

    //Type Converters
    single<JsonParser> {
        GsonParser(gson = GsonBuilder().create())
    }
    single { Converters(get()) }
}