package com.particlesector.soniflac.core.common

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Dispatcher(val dispatcher: SoniFlacDispatchers)

enum class SoniFlacDispatchers {
    Default,
    IO,
}
