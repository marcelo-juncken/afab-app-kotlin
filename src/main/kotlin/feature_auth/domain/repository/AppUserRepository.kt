package feature_auth.domain.repository

import core.util.Resource
import core.util.SimpleResource
import feature_auth.data.dto.Device
import feature_auth.data.dto.DomainAppUser
import feature_auth.domain.models.UserDisplayInfo
import org.bson.types.ObjectId
import kotlin.reflect.KProperty1


interface AppUserRepository {
    suspend fun insertUser(domainAppUser: DomainAppUser): SimpleResource

    suspend fun findUserByAuth0Id(auth0Id: String): Resource<DomainAppUser?>

    suspend fun updateUserFieldsById(
        userId: ObjectId,
        vararg fields: Pair<KProperty1<DomainAppUser, *>, Any>,
    ): SimpleResource

    suspend fun close()

    suspend fun findUserByPublicKey(deviceId: String, publicKey: String): Resource<DomainAppUser?>
    suspend fun getAllUsers(): Resource<List<UserDisplayInfo>>
    suspend fun removeDeviceById(userId: ObjectId, deviceId: String): SimpleResource
    suspend fun upsertDevice(device: Device): SimpleResource
}