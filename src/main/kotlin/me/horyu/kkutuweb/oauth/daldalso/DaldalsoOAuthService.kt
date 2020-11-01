package me.horyu.kkutuweb.oauth.daldalso

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.scribejava.core.builder.ServiceBuilder
import com.github.scribejava.core.model.OAuthRequest
import com.github.scribejava.core.model.Verb
import com.github.scribejava.core.oauth.OAuth20Service
import me.horyu.kkutuweb.SessionAttribute
import me.horyu.kkutuweb.oauth.Gender
import me.horyu.kkutuweb.oauth.OAuthService
import me.horyu.kkutuweb.oauth.OAuthUser
import me.horyu.kkutuweb.oauth.VendorType
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.servlet.http.HttpSession


@Service
class DaldalsoOAuthService(
        @Autowired private val objectMapper: ObjectMapper
) : OAuthService() {
    private val logger = LoggerFactory.getLogger(DaldalsoOAuthService::class.java)
    private val protectedResourceUrl = "https://daldal.so/oauth/api/me"

    override fun init(apiKey: String, apiSecret: String, callbackUrl: String) {
        oAuth20Service = ServiceBuilder(apiKey)
                .apiSecret(apiSecret)
                .callback(callbackUrl)
                .userAgent("KKuTu-Web (https://github.com/horyu1234/KKuTu-Web)")
                .build(DaldalsoApi)
    }

    override fun getAuthorizationUrl(httpSession: HttpSession): String {
        val randomState = getRandomState()
        httpSession.setAttribute(SessionAttribute.OAUTH_STATE.attributeName, randomState)
        httpSession.setAttribute(SessionAttribute.OAUTH_20_SERVICE.attributeName, oAuth20Service)

        return oAuth20Service.getAuthorizationUrl(randomState)
    }

    override fun abstractLogin(httpSession: HttpSession, oAuth20Service: OAuth20Service, code: String): Boolean {
        try {
            val accessToken = oAuth20Service.getAccessToken(code)

            val request = OAuthRequest(Verb.GET, protectedResourceUrl)
            oAuth20Service.signRequest(accessToken, request)

            val response = oAuth20Service.execute(request)
            val jsonResponse = objectMapper.readTree(response.body)

            val oAuthUser = OAuthUser(VendorType.DALDALSO,
                    jsonResponse["key"].longValue().toString(),
                    jsonResponse["name"].textValue(),
                    jsonResponse["profile"]["image"].textValue(),
                    Gender.OTHER,
                    0,
                    0)

            httpSession.setAttribute(SessionAttribute.IS_GUEST.attributeName, false)
            httpSession.setAttribute(SessionAttribute.OAUTH_USER.attributeName, oAuthUser)
            return true
        } catch (exception: Exception) {
            exception.printStackTrace()
            logger.warn("${httpSession.id} 세션에서 로그인에 실패하였습니다. ${exception.message}")
            return false
        }
    }
}