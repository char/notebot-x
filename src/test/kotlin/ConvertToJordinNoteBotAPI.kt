import `in`.jord.notebot.api.MinecraftNoteBot
import `in`.jord.notebot.api.NoteBot
import `in`.jord.notebot.api.action.Note
import `in`.jord.notebot.api.action.NoteBotAction
import `in`.jord.notebot.api.action.Rest
import `in`.jord.notebot.api.data.MinecraftInstrument
import site.hackery.nbx.decodeSong
import java.io.File
import java.io.IOException
import java.util.Collections.min
import kotlin.math.min

import site.hackery.nbx.Rest as NBXRest
import site.hackery.nbx.Note as NBXNote
import site.hackery.nbx.Song as NBXSong

typealias NBAPISong = List<NoteBotAction<MinecraftInstrument>>

fun convert(song: NBXSong): NBAPISong {
    val convertedSong = mutableListOf<NoteBotAction<MinecraftInstrument>>()

    for (action in song.actions) {
        when (action) {
            is NBXNote -> {
                val instrument = MinecraftInstrument.values()[action.instrument.toInt()]
                convertedSong.add(Note.of(instrument, action.pitch))
            }

            is NBXRest -> {
                var duration = action.duration.toInt()
                while (duration > 0) {
                    val segmentDuration = min(Rest.REST_DURATION_MAX, duration)
                    convertedSong.add(Rest.of(segmentDuration))
                    duration -= segmentDuration
                }
            }
        }
    }

    return convertedSong
}

fun main() {
    val songs = File("contrib", "songs")
    val convertedSongs = File("contrib", "converted_songs")

    try {
        convertedSongs.mkdir()
    } catch (e: IOException) {}

    val nbapiBot = MinecraftNoteBot()

    for (file in songs.listFiles()) {
        val nbxSong = decodeSong(file.readBytes())
        val nbapiSong = convert(nbxSong)
        nbapiBot.write(File(convertedSongs, file.nameWithoutExtension + ".notebot"), nbapiSong)
    }
}
