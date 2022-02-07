package info.nightscout.androidaps.utils.buildHelper

//PBA import info.nightscout.androidaps.BuildConfig
import info.nightscout.androidaps.interfaces.Config
import info.nightscout.androidaps.plugins.general.maintenance.PrefFileListProvider
//PBA import java.io.File

class BuildHelperImpl constructor(
    private val config: Config,
    fileListProvider: PrefFileListProvider
) : BuildHelper {

    private var devBranch = false
    private var engineeringMode = false

    init {
//PBA        val engineeringModeSemaphore = File(fileListProvider.ensureExtraDirExists(), "engineering_mode")

//PBA        engineeringMode = engineeringModeSemaphore.exists() && engineeringModeSemaphore.isFile
//PBA        devBranch = BuildConfig.VERSION.contains("-") || BuildConfig.VERSION.matches(Regex(".*[a-zA-Z]+.*"))
        engineeringMode = true
        devBranch = true
    }

    override fun isEngineeringModeOrRelease(): Boolean =
        if (!config.APS) true else engineeringMode || !devBranch

    override fun isEngineeringMode(): Boolean = engineeringMode

    override fun isDev(): Boolean = devBranch
}