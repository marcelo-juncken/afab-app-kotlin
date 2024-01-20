package core.data

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.time.Instant

data class PaymentTemplateItem(
    @BsonId
    val _id: ObjectId = ObjectId(),
    val userId: ObjectId? = null,
    var templateName: String,
    val savedStates : Map<String, Any>,
    val templateType: String?,
    val createdAt: Instant = Instant.now(),
)
