package feature_auth.data.local

import core.util.Constants.ZONE_ID
import core.util.Resource
import core.util.StringResources
import feature_auth.data.dto.Device
import java.net.InetAddress
import java.net.UnknownHostException
import java.time.LocalDateTime
import java.time.ZoneId

object DeviceIdProvider {
    val currentDeviceId = getWindowsDeviceId()
//    fun getDeviceId(): String {
//        val osName = System.getProperty("os.name").lowercase()
//
//        return when {
//            osName.contains("windows") -> getWindowsDeviceId()
//            osName.contains("mac") -> getMacDeviceId()
//            else -> {Unit}
//        }
//    }

    private fun getWindowsDeviceId(): String {
        return try {
            val command = "wmic path Win32_LogicalDisk where DeviceID='C:' get VolumeSerialNumber"
            val process = ProcessBuilder("cmd", "/c", command)
                .redirectErrorStream(true)
                .start()
            val output = process.inputStream.bufferedReader().use { it.readText() }
            process.waitFor()

            val serialNumber = output.split("\n")[1].trim()
            serialNumber
        } catch (e: Exception) {
            "unknown"
        }
    }

    fun getDeviceName(): String {
        return try {
            InetAddress.getLocalHost().hostName
        } catch (e: UnknownHostException) {
            "unknown"
        }
    }

    fun updateDeviceList(devices: MutableList<Device>, publicKey: String? = null): Resource<MutableList<Device>> {
        val device = devices.firstOrNull { it.deviceId == currentDeviceId }
        try {
            val upsertedDevice = if (device == null) {
                Device(
                    deviceId = currentDeviceId,
                    deviceName = getDeviceName(),
                    addedDate = LocalDateTime.now(ZoneId.of(ZONE_ID)),
                    publicKey = publicKey,
                    accessCount = 1L
                )
            } else {
                device.copy(
                    lastLogin = LocalDateTime.now(ZoneId.of(ZONE_ID)),
                    publicKey = publicKey ?: device.publicKey,
                    accessCount = device.accessCount + 1
                )
            }
            devices.remove(device)
            devices.add(upsertedDevice)
            return Resource.Success(devices)

        } catch (e: Exception) {
            return Resource.Error(e.message ?: StringResources.ERROR_UNKNOWN)
        }
    }


//    private fun getMacDeviceId(): String {
//        val ioRegistryRoot = SystemB.INSTANCE.IORegistryEntryFromPath(0, "IODeviceTree:/")
//        val deviceSerialNumber: String? = SystemB.INSTANCE.IORegistryEntryCreateCFProperty(
//            ioRegistryRoot,
//            "IOPlatformSerialNumber",
//            SystemB.INSTANCE.CFAllocatorGetDefault(),
//            0
//        ).toString()
//
//        SystemB.INSTANCE.IOObjectRelease(ioRegistryRoot)
//
//        return deviceSerialNumber ?: "unknown"
//    }

}
