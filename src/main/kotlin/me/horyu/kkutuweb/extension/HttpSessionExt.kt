/*
 * KKuTu-Web (https://github.com/horyu1234/KKuTu-Web)
 * Copyright (C) 2020. horyu1234(admin@horyu.me)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.horyu.kkutuweb.extension

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import me.horyu.kkutuweb.SessionAttribute
import me.horyu.kkutuweb.oauth.OAuthUser
import javax.servlet.http.HttpSession

private val objectMapper = ObjectMapper().registerModule(KotlinModule())

fun HttpSession.isGuest(): Boolean = (this.getAttribute(SessionAttribute.IS_GUEST.attributeName) ?: true) as Boolean
fun HttpSession.getOAuthUser(): OAuthUser = objectMapper.readValue(this.getAttribute(SessionAttribute.OAUTH_USER.attributeName) as String, OAuthUser::class.java)
fun HttpSession.setOAuthUser(oAuthUser: OAuthUser) = this.setAttribute(SessionAttribute.OAUTH_USER.attributeName, objectMapper.writeValueAsString(oAuthUser))