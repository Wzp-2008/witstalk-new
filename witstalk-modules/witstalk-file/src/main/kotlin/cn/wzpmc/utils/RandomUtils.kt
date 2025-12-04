package cn.wzpmc.utils

import org.springframework.stereotype.Component
import java.util.*

@Component
class RandomUtils {
    fun generatorRandomString(length: Int): String {
        val builder = StringBuilder()
        for (i in 0..<length) {
            val c = Random().nextInt(33, 126)
            builder.append(c.toChar())
        }
        return builder.toString()
    }

    fun generatorRandomFileName(length: Int): String {
        val builder = StringBuilder()
        for (i in 0..<length) {
            val random = Math.random()
            var c = Random().nextInt(97, 122)
            if (random < 0.3) {
                c = Random().nextInt(48, 57)
            } else if (random < 0.6) {
                c = Random().nextInt(65, 90)
            }
            builder.append(c.toChar())
        }
        return builder.toString()
    }
}