package com.synergics.stb.iptv.leanback.models;

import com.synergics.stb.iptv.leanback.BaseApplication
import java.io.*
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.collections.ArrayList

class M3UParser {

    fun insertToDb(file: File, clear: Boolean = false){
        if (clear){
            BaseApplication.database?.tvItemDao()?.clear()
            BaseApplication.database?.tvItemDao()?.resetAI()
            BaseApplication.database?.tvCategoriesDao()?.clear()
            BaseApplication.database?.tvCategoriesDao()?.resetAI()
        }
        val inputStream = FileInputStream(file)
        val enteries = M3UParser().parse(inputStream)

        val temp = mutableListOf<TVItems>()
        for (entry in enteries){
            val playlistItem = TVItems()

            val categoryName = if(entry.groupTitle == null || entry.groupTitle?.isEmpty() == true) "Others" else entry.groupTitle
            playlistItem.icon = entry.tvgLogo
            playlistItem.duration = entry.duration
            playlistItem.group = categoryName
            playlistItem.nama = entry.channelName
            playlistItem.url = entry.channelUri

            val tvgCategory = BaseApplication.database?.tvCategoriesDao()?.getByNama(categoryName)

            if (tvgCategory == null){
                val id = BaseApplication.database?.tvCategoriesDao()?.insert(TVCategories(categoryName))
                if (id != null)
                    playlistItem.categoryId = id.toInt()
            } else {
                playlistItem.categoryId = tvgCategory.id
            }

            if (playlistItem.url != null && playlistItem.url?.isNotEmpty() == true)
                temp.add(playlistItem)
        }

        if (temp.isNotEmpty())
            BaseApplication.database?.tvItemDao()?.insert(temp)
    }
    /**
     * Parse the m3u file
     *
     * @param stream pointing to your m3u file
     * @return
     */
    fun parse(stream: InputStream?): List<Entry> {
        if (stream == null) {
            throw ParsingException(0, "Cannot read stream")
        }
        val entries: MutableList<Entry> = ArrayList()
        var lineNbr = 0
        var line: String?
        try {
            val buffer = BufferedReader(InputStreamReader(stream))
            line = buffer.readLine()
            if (line == null) {
                throw ParsingException(0, "Empty stream")
            }
            lineNbr++
            checkStart(line)
            val globalTvgShif = extract(line, TVG_SHIFT_REGEX)
            var entry: Entry.Builder? = null
            while (buffer.readLine().also { line = it } != null) {
                lineNbr++
                if (isExtInfo(line)) {
                    entry = extractExtInfo(globalTvgShif, line)
                } else {
                    if (entry == null) {
                        throw ParsingException(lineNbr, "Missing " + M3U_INFO_MARKER)
                    }
                    entries.add(entry.channelUri(line).build())
                }
            }
        } catch (e: IOException) {
            throw ParsingException(lineNbr, "Cannot read file", e)
        }
        return entries
    }

    private fun checkStart(line: String?) {
        if (line != null) {
            if (!line.contains(M3U_START_MARKER)) {
                throw ParsingException(1, "First line of the file should be " + M3U_START_MARKER)
            }
        }
    }

    private fun isExtInfo(line: String?): Boolean {
        return line!!.contains(M3U_INFO_MARKER)
    }

    private fun extractExtInfo(globalTvgShift: String?, line: String?): Entry.Builder {
        val duration = extract(line, DURATION_REGEX)
        val tvgId = extract(line, TVG_ID_REGEX)
        val tvgName = extract(line, TVG_NAME_REGEX)
        var tvgShift = extract(line, TVG_SHIFT_REGEX)
        if (tvgShift == null) {
            tvgShift = globalTvgShift
        }
        val radio = extract(line, RADIO_REGEX)
        val tvgLogo = extract(line, TVG_LOGO_REGEX)
        val groupTitle = extract(line, GROUP_TITLE_REGEX)
        val channelName = extract(line, CHANNEL_NAME_REGEX)
        return Entry.Builder().channelName(channelName)
            .duration(duration)
            .groupTitle(groupTitle)
            .radio(radio)
            .tvgId(tvgId)
            .tvgLogo(tvgLogo)
            .tvgName(tvgName)
            .tvgShift(tvgShift)
    }

    private fun extract(line: String?, pattern: Pattern): String? {
        val matcher: Matcher = pattern.matcher(line ?: "")
        return if (matcher.matches()) {
            matcher.group(1)
        } else null
    }

    companion object {
        private const val M3U_START_MARKER = "#EXTM3U"
        private const val M3U_INFO_MARKER = "#EXTINF:"
        private val DURATION_REGEX: Pattern =
            Pattern.compile(".*#EXTINF:(.+?) .*", Pattern.CASE_INSENSITIVE)
        private val TVG_ID_REGEX: Pattern =
            Pattern.compile(".*tvg-id=\"(.?|.+?)\".*", Pattern.CASE_INSENSITIVE)
        private val TVG_NAME_REGEX: Pattern =
            Pattern.compile(".*tvg-name=\"(.?|.+?)\".*", Pattern.CASE_INSENSITIVE)
        private val TVG_LOGO_REGEX: Pattern =
            Pattern.compile(".*tvg-logo=\"(.?|.+?)\".*", Pattern.CASE_INSENSITIVE)
        private val TVG_SHIFT_REGEX: Pattern =
            Pattern.compile(".*tvg-shift=\"(.?|.+?)\".*", Pattern.CASE_INSENSITIVE)
        private val GROUP_TITLE_REGEX: Pattern =
            Pattern.compile(".*group-title=\"(.?|.+?)\".*", Pattern.CASE_INSENSITIVE)
        private val RADIO_REGEX: Pattern =
            Pattern.compile(".*radio=\"(.?|.+?)\".*", Pattern.CASE_INSENSITIVE)
        private val CHANNEL_NAME_REGEX: Pattern =
            Pattern.compile(".*,(.+?)$", Pattern.CASE_INSENSITIVE)
    }
}
