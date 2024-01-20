package feature_auth.data.repository

import core.util.Resource
import core.util.SimpleResource
import core.util.StringResources.ERROR_UNKNOWN
import feature_auth.data.dto.Device
import feature_auth.data.dto.DomainAppUser
import feature_auth.data.mappers.toAppUserDisplay
import feature_auth.data.repository.datasource.IDatabaseConnection
import feature_auth.domain.models.UserDisplayInfo
import feature_auth.domain.repository.AppUserRepository
import org.bson.types.ObjectId
import org.litote.kmongo.*
import kotlin.reflect.KProperty1

class AppUserRepositoryImpl(
    private val dbConnection: IDatabaseConnection,
) : AppUserRepository {

    private val users by lazy { dbConnection.database.getCollection<DomainAppUser>("appUser") }
    private val devices by lazy { dbConnection.database.getCollection<Device>("Devices") }

    override suspend fun insertUser(domainAppUser: DomainAppUser): SimpleResource {
        return try {
            val result = users.insertOne(domainAppUser)

            if (!result.wasAcknowledged()) return Resource.Error("Não foi possível criar sua conta.")

            Resource.succeeded
        } catch (e: Exception) {
            Resource.Error(ERROR_UNKNOWN)
        }
    }

    override suspend fun findUserByAuth0Id(auth0Id: String): Resource<DomainAppUser?> {
        return try {
            val id = auth0Id.replace("auth0|", "")

            val user =
                users.findOne(DomainAppUser::auth0UserId eq id) ?: return Resource.Error("Usuário não encontrado.")

            return Resource.Success(user)
        } catch (e: Exception) {
            Resource.Error("Não foi possível obter os dados de usuário.")
        }
    }

    override suspend fun updateUserFieldsById(
        userId: ObjectId,
        vararg fields: Pair<KProperty1<DomainAppUser, *>, Any>,
    ): SimpleResource {
        return try {
            val updateOperations = fields.map { (property, value) -> setValue(property, value) }
            val combinedUpdate = combine(updateOperations)
            val result = users.updateOne(DomainAppUser::_id eq userId, combinedUpdate)

            if (!result.wasAcknowledged()) return Resource.failed("Não foi possível atualizar os dados.")

            Resource.succeeded
        } catch (e: Exception) {
            Resource.failed(e.message)
        }
    }

    override suspend fun upsertDevice(device: Device): SimpleResource {
        return try {

            val filter = Device::deviceId eq device.deviceId

            val update = combine(
                setOnInsert(Device::deviceName, device.deviceName),
                setOnInsert(Device::addedDate, device.addedDate),
                setValue(Device::lastLogin, device.lastLogin),
                inc(Device::accessCount, 1)
            )

            val updateResult = devices.updateOne(filter = filter, update = update, upsert())

            if (!updateResult.wasAcknowledged()) return Resource.failed("Não foi possível atualizar os dados.")

            Resource.succeeded
        } catch (e: Exception) {
            Resource.failed(e.message)
        }
    }

    override suspend fun close() {
        dbConnection.close()
    }

    override suspend fun findUserByPublicKey(deviceId: String, publicKey: String): Resource<DomainAppUser?> {
        return try {
            val user = users.findOne(
                DomainAppUser::devices elemMatch combine(
                    Device::deviceId eq deviceId,
                    Device::publicKey eq publicKey
                )
            )
                ?: return Resource.Error("Usuário não encontrado")

            return Resource.Success(user)
        } catch (e: Exception) {
            Resource.Error("Não foi possível obter os dados de usuário.")
        }
    }

    override suspend fun getAllUsers(): Resource<List<UserDisplayInfo>> {
        return try {
            val user = users.find().toList().map { it.toAppUserDisplay() }
            return Resource.Success(user)
        } catch (e: Exception) {
            Resource.Error("Não foi possível obter os dados de usuário.")
        }
    }

    override suspend fun removeDeviceById(userId: ObjectId, deviceId: String): SimpleResource {
        return try {
            users.updateOneById(id = userId, pullByFilter(DomainAppUser::devices, Device::deviceId eq deviceId))
            return Resource.succeeded
        } catch (e: Exception) {
            Resource.Error("Não foi possível remover o dispositivo.")
        }
    }
}