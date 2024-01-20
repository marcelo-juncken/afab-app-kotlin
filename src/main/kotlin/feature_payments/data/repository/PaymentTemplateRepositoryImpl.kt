package feature_payments.data.repository

import com.mongodb.client.model.UpdateOptions
import core.data.PaymentTemplateItem
import core.data.SavedTemplatesType
import core.util.Resource
import core.util.SimpleResource
import core.util.StringResources
import feature_auth.data.repository.datasource.IDatabaseConnection
import feature_payments.domain.repository.PaymentTemplateRepository
import org.bson.types.ObjectId
import org.litote.kmongo.and
import org.litote.kmongo.eq

class PaymentTemplateRepositoryImpl(
    dbConnection: IDatabaseConnection,
) : PaymentTemplateRepository {

    private val paymentTemplatesItem by lazy {  dbConnection.database.getCollection<PaymentTemplateItem>("Templates") }

    override suspend fun upsertPaymentsTemplate(template: PaymentTemplateItem): SimpleResource {
        return try {
            val result = paymentTemplatesItem.updateOne(
                PaymentTemplateItem::_id eq template._id,
                template,
                UpdateOptions().upsert(true)
            )

            if (!result.wasAcknowledged()) return Resource.Error("Não foi possível salvar esse template")

            Resource.succeeded
        } catch (e: Exception) {
            Resource.Error(e.message ?: StringResources.ERROR_UNKNOWN)
        }
    }

    override suspend fun deletePaymentsTemplate(templateId: ObjectId): SimpleResource {
        try {
            val templates = paymentTemplatesItem.deleteOneById(templateId)

            if (templates.deletedCount <= 0) return Resource.Error("Não foi possível deletar o template.")

            return Resource.succeeded
        } catch (e: Exception) {
            return Resource.Error(e.message ?: StringResources.ERROR_UNKNOWN)
        }
    }

    override suspend fun getUserTemplates(userId: ObjectId): Resource<List<PaymentTemplateItem>> {
        return try {
            val templates = paymentTemplatesItem.find(
                and(
                    PaymentTemplateItem::userId eq userId,
                    PaymentTemplateItem::templateType eq SavedTemplatesType.PAYMENT.name
                )
            ).toList()

            Resource.Success(templates)
        } catch (e: Exception) {
            Resource.Error(e.message ?: StringResources.ERROR_UNKNOWN)
        }
    }
}