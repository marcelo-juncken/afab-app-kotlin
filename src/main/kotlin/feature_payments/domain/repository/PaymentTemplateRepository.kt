package feature_payments.domain.repository

import core.data.PaymentTemplateItem
import core.util.Resource
import core.util.SimpleResource
import org.bson.types.ObjectId

interface PaymentTemplateRepository {
    suspend fun getUserTemplates(userId: ObjectId): Resource<List<PaymentTemplateItem>>
    suspend fun upsertPaymentsTemplate(template: PaymentTemplateItem): SimpleResource
    suspend fun deletePaymentsTemplate(templateId: ObjectId): SimpleResource
}