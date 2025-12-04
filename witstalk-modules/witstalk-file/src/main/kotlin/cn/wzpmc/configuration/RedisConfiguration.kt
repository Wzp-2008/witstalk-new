package cn.wzpmc.configuration

import cn.wzpmc.entities.vo.FileVo
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate

@Configuration
class RedisConfiguration {
    @Bean
    fun linkMapper(redisConnectionFactory: RedisConnectionFactory): RedisTemplate<String, FileVo> {
        val template: RedisTemplate<String, FileVo> = RedisTemplate<String, FileVo>()
        template.connectionFactory = redisConnectionFactory
        return template
    }
}