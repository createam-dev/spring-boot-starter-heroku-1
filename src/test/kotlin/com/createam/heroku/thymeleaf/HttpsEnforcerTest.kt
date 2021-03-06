package com.createam.heroku.thymeleaf

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Matchers
import org.mockito.Mockito.*
import org.mockito.runners.MockitoJUnitRunner
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@RunWith(MockitoJUnitRunner::class)
class HttpsEnforcerTest {

    private val request: HttpServletRequest = mock(HttpServletRequest::class.java)
    private val response: HttpServletResponse = mock(HttpServletResponse::class.java)
    private val chain: FilterChain = mock(FilterChain::class.java)

    private var TEST_HEROKU_URL: String = "for-sure-no-existing-address.xyz"
    private var EXPECTED_HEROKU_URL: String = "https://for-sure-no-existing-address.xyz"

    @Test
    fun shouldEnforceHttpsOnHeroku() {
        val httpsEnforcer = HttpsEnforcer()
        `when`(request.getHeader(Matchers.eq("x-forwarded-proto"))).thenReturn(TEST_HEROKU_URL)

        httpsEnforcer.doFilter(request, response, chain)

        verify(chain, times(1)).doFilter(request, response)
    }

    @Test
    fun shouldRedirectToSameUrlOnHeroku() {
        val httpsEnforcer = HttpsEnforcer()
        `when`(request.getHeader(Matchers.eq("x-forwarded-proto"))).thenReturn(TEST_HEROKU_URL)
        `when`(request.serverName).thenReturn(TEST_HEROKU_URL)
        `when`(request.requestURI).thenReturn("/api")

        httpsEnforcer.doFilter(request, response, chain)

        val expectedUrl: String = EXPECTED_HEROKU_URL + "/api"
        verify(response, times(1)).sendRedirect(expectedUrl)
    }
}
