@file:Suppress("EXPERIMENTAL_API_USAGE","EXPERIMENTAL_UNSIGNED_LITERALS","PackageDirectoryMismatch","UnusedImport","unused","LocalVariableName","CanBeVal","PropertyName","EnumEntryName","ClassName","ObjectPropertyName","UnnecessaryVariable","SpellCheckingInspection")
package org.utbot.rd.generated

import com.jetbrains.rd.framework.*
import com.jetbrains.rd.framework.base.*
import com.jetbrains.rd.framework.impl.*

import com.jetbrains.rd.util.lifetime.*
import com.jetbrains.rd.util.reactive.*
import com.jetbrains.rd.util.string.*
import com.jetbrains.rd.util.*
import kotlin.reflect.KClass
import kotlin.jvm.JvmStatic



/**
 * #### Generated from [SynchronizationModel.kt:8]
 */
class SynchronizationModel private constructor(
    private val _suspendTimeoutTimer: RdCall<Boolean, Unit>,
    private val _synchronizationSignal: RdSignal<String>
) : RdExtBase() {
    //companion
    
    companion object : ISerializersOwner {
        
        override fun registerSerializersCore(serializers: ISerializers)  {
        }
        
        
        @JvmStatic
        @JvmName("internalCreateModel")
        @Deprecated("Use create instead", ReplaceWith("create(lifetime, protocol)"))
        internal fun createModel(lifetime: Lifetime, protocol: IProtocol): SynchronizationModel  {
            @Suppress("DEPRECATION")
            return create(lifetime, protocol)
        }
        
        @JvmStatic
        @Deprecated("Use protocol.synchronizationModel or revise the extension scope instead", ReplaceWith("protocol.synchronizationModel"))
        fun create(lifetime: Lifetime, protocol: IProtocol): SynchronizationModel  {
            SynchronizationRoot.register(protocol.serializers)
            
            return SynchronizationModel().apply {
                identify(protocol.identity, RdId.Null.mix("SynchronizationModel"))
                bind(lifetime, protocol, "SynchronizationModel")
            }
        }
        
        
        const val serializationHash = 3813608056984691311L
        
    }
    override val serializersOwner: ISerializersOwner get() = SynchronizationModel
    override val serializationHash: Long get() = SynchronizationModel.serializationHash
    
    //fields
    val suspendTimeoutTimer: RdCall<Boolean, Unit> get() = _suspendTimeoutTimer
    val synchronizationSignal: IAsyncSignal<String> get() = _synchronizationSignal
    //methods
    //initializer
    init {
        _suspendTimeoutTimer.async = true
        _synchronizationSignal.async = true
    }
    
    init {
        bindableChildren.add("suspendTimeoutTimer" to _suspendTimeoutTimer)
        bindableChildren.add("synchronizationSignal" to _synchronizationSignal)
    }
    
    //secondary constructor
    private constructor(
    ) : this(
        RdCall<Boolean, Unit>(FrameworkMarshallers.Bool, FrameworkMarshallers.Void),
        RdSignal<String>(FrameworkMarshallers.String)
    )
    
    //equals trait
    //hash code trait
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("SynchronizationModel (")
        printer.indent {
            print("suspendTimeoutTimer = "); _suspendTimeoutTimer.print(printer); println()
            print("synchronizationSignal = "); _synchronizationSignal.print(printer); println()
        }
        printer.print(")")
    }
    //deepClone
    override fun deepClone(): SynchronizationModel   {
        return SynchronizationModel(
            _suspendTimeoutTimer.deepClonePolymorphic(),
            _synchronizationSignal.deepClonePolymorphic()
        )
    }
    //contexts
}
val IProtocol.synchronizationModel get() = getOrCreateExtension(SynchronizationModel::class) { @Suppress("DEPRECATION") SynchronizationModel.create(lifetime, this) }

