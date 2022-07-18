package com.synergics.stb.iptv.leanback.models

class Entry private constructor() {
    /**
     * duration of the
     *
     * @return the duration or null
     */
    var duration: String? = null
        private set

    /**
     * group-title is channels group name
     *
     * @return the group-title or null if it doesn't exist
     */
    var groupTitle: String? = null
        private set

    /**
     * tvg-id is value of channel id in EPG xml file
     *
     * @return the tvg-id value or null if it doesn't exist
     */
    var tvgId: String? = null
        private set

    /**
     * tvg-name is value of display-name in EPG
     *
     * @return the tvg-name value or null if it doesn't exist
     */
    var tvgName: String? = null
        private set

    /**
     * tvg-logo is the path to the channel logo file
     *
     * @return the tvg-logo value or null if it doesn't exist
     */
    var tvgLogo: String? = null
        private set

    /**
     * tvg-shift is value in hours to shift EPG time.
     *
     * @return the tvg-shift value or null if it doesn't exist
     */
    var tvgShift: String? = null
        private set

    /**
     * radio indicates if the channel is a radio or not
     *
     * @return the radio value or null if it doesn't exist
     */
    var radio: String? = null
        private set

    /**
     * the uri can be a relative or absolute path. It can also be an URL
     *
     * @return the channel URI
     */
    var channelUri: String? = null
        private set

    /**
     * channel name of the channel
     *
     * @return the channel name
     */
    var channelName: String? = null
        private set

    override fun toString(): String {
        return "Entry{" +
                "duration='" + duration + '\'' +
                ", groupTitle='" + groupTitle + '\'' +
                ", tvgId='" + tvgId + '\'' +
                ", tvgName='" + tvgName + '\'' +
                ", tvgLogo='" + tvgLogo + '\'' +
                ", tvgShift='" + tvgShift + '\'' +
                ", radio='" + radio + '\'' +
                ", channelUri='" + channelUri + '\'' +
                ", channelName='" + channelName + '\'' +
                '}'
    }

    class Builder {
        private var duration: String? = null
        private val name: String? = null
        private var groupTitle: String? = null
        private var tvgId: String? = null
        private var tvgName: String? = null
        private var tvgLogo: String? = null
        private var tvgShift: String? = null
        private var radio: String? = null
        private var channelUri: String? = null
        private var channelName: String? = null
        fun duration(duration: String?): Builder {
            this.duration = duration
            return this
        }

        fun groupTitle(groupTitle: String?): Builder {
            this.groupTitle = groupTitle
            return this
        }

        fun tvgId(tvgId: String?): Builder {
            this.tvgId = tvgId
            return this
        }

        fun tvgName(tvgName: String?): Builder {
            this.tvgName = tvgName
            return this
        }

        fun tvgLogo(tvgLogo: String?): Builder {
            this.tvgLogo = tvgLogo
            return this
        }

        fun tvgShift(tvgShift: String?): Builder {
            this.tvgShift = tvgShift
            return this
        }

        fun radio(radio: String?): Builder {
            this.radio = radio
            return this
        }

        fun channelUri(channelUri: String?): Builder {
            this.channelUri = channelUri
            return this
        }

        fun channelName(channelName: String?): Builder {
            this.channelName = channelName
            return this
        }

        fun build(): Entry {
            val entry = Entry()
            entry.duration = duration
            entry.groupTitle = groupTitle
            entry.tvgId = tvgId
            entry.tvgName = tvgName
            entry.tvgLogo = tvgLogo
            entry.tvgShift = tvgShift
            entry.radio = radio
            entry.channelUri = channelUri
            entry.channelName = channelName
            return entry
        }
    }
}