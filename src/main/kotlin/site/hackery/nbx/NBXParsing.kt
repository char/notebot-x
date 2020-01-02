package site.hackery.nbx

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream

private const val CURRENT_VERSION = 1

fun encodeSong(song: Song): ByteArray {
    val output = ByteArrayOutputStream()
    DataOutputStream(output).use {
        it.writeShort(1)
        it.writeInt(song.actions.size)

        for (action in song.actions) {
            it.writeByte(action.id.toInt())

            when (action) {
                is Note -> {
                    it.writeShort(action.instrument.toInt())
                    it.writeByte(action.pitch.toInt())
                }

                is Rest -> {
                    it.writeShort(action.duration.toInt())
                }

                else -> error("Unknown action")
            }
        }
    }

    return output.toByteArray()
}

fun decodeSong(songData: ByteArray): Song {
    val stream = DataInputStream(ByteArrayInputStream(songData))
    val version = stream.readShort()

    return when (version.toInt()) {
        1 -> decodeV1(stream)
        else -> error("Unsupported version")
    }
}

private fun decodeV1(stream: DataInputStream): Song {
    val numberOfActions = stream.readInt()
    val actionArray = arrayOfNulls<Action>(numberOfActions)

    for (i in 0 until numberOfActions) {
        val actionId = stream.readByte()
        actionArray[i] = when (actionId.toInt()) {
            1 -> {
                val instrument = stream.readShort()
                val pitch = stream.readByte()

                Note(instrument, pitch)
            }

            2 -> {
                val duration = stream.readShort()

                Rest(duration)
            }

            else -> error("Unsupported action type")
        }
    }

    @Suppress("UNCHECKED_CAST")
    return Song(actionArray as Array<Action>)
}
