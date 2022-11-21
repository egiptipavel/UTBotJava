package org.utbot.framework.plugin.api.go

import org.utbot.framework.plugin.api.*

/**
 * Parent class for all Go types for compatibility with UTBot framework.
 *
 * To see its children check GoTypesApi.kt at org.utbot.go.api.
 */
abstract class GoClassId(name: String) : ClassId(name) {
    override val isNullable: Boolean
        get() = error("not supported")
    override val canonicalName: String
        get() = error("not supported")
    override val simpleName: String
        get() = error("not supported")
    override val packageName: String
        get() = error("not supported")
    override val isInDefaultPackage: Boolean
        get() = error("not supported")
    override val isPublic: Boolean
        get() = error("not supported")
    override val isProtected: Boolean
        get() = error("not supported")
    override val isPrivate: Boolean
        get() = error("not supported")
    override val isFinal: Boolean
        get() = error("not supported")
    override val isStatic: Boolean
        get() = error("not supported")
    override val isAbstract: Boolean
        get() = error("not supported")
    override val isAnonymous: Boolean
        get() = error("not supported")
    override val isLocal: Boolean
        get() = error("not supported")
    override val isInner: Boolean
        get() = error("not supported")
    override val isNested: Boolean
        get() = error("not supported")
    override val isSynthetic: Boolean
        get() = error("not supported")
    override val allMethods: Sequence<MethodId>
        get() = error("not supported")
    override val allConstructors: Sequence<ConstructorId>
        get() = error("not supported")
    override val typeParameters: TypeParameters
        get() = error("not supported")
    override val outerClass: Class<*>?
        get() = error("not supported")
    override val simpleNameWithEnclosings: String
        get() = error("not supported")
}

/**
 * Parent class for all Go models.
 *
 * To see its children check GoUtModelsApi.kt at org.utbot.go.api.
 */
open class GoUtModel(
    override val classId: GoClassId,
    val requiredImports: Set<String>
) : UtModel(classId) {
    override fun toString(): String = error("not supported")
}

/**
 * Class for Go struct constructors.
 */
class GoStructConstructorId(
    classId: GoClassId,
    val fields: List<FieldId>,
) : ConstructorId(classId, fields.map { it.declaringClass })