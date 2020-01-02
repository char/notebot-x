package site.hackery.nbx

@Retention(AnnotationRetention.SOURCE)
annotation class Since(val version: Int)

inline class Song(val actions: Array<Action>)

sealed class Action(val id: Byte)

@Since(1)
data class Note(val instrument: Short, val pitch: Byte) : Action(1)

@Since(1)
data class Rest(val duration: Short) : Action(2)
